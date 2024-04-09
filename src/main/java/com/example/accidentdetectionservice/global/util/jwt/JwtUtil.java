package com.example.accidentdetectionservice.global.util.jwt;

import com.example.accidentdetectionservice.domain.user.entity.UserRoleEnum;
import com.example.accidentdetectionservice.global.redis.RedisRepository;
import com.example.accidentdetectionservice.global.redis.RefreshTokenService;
import com.example.accidentdetectionservice.global.redis.entity.RefreshToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "JWT 관련 로그")
public class JwtUtil {

    // JWT 데이터
    // accessToken 값, Header name, 권환 이름 (user or admin)
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String REFRESH_HEADER = "Refresh";

    //redis 값 조회 헤더
    public static final String REFRESH_PREFIX = "refresh:";

    // 사용자 권한 값의 KEY, 권한을 구분하기 위함
    public static final String AUTHORIZATION_KEY = "auth";

    public static final String USER_KEY = "user";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";

    // 토큰 만료시간
    private final long TOKEN_TIME = 30 * 60 * 1000L; // 30분, 밀리세컨드

    private final RedisRepository redisRepository;

    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey; //jwt.secret.key
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 생성자 호출 뒤에 실행, 요청의 반복 호출 방지
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // JWT 토큰 생성
    public String createToken(String username, UserRoleEnum role, Long userId) {
        Date date = new Date();

        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(username) // 사용자 식별자값(ID)
                .claim(AUTHORIZATION_KEY, role) // key 값으로 꺼내어 쓸 수 있다.
                .claim(USER_KEY, userId)
                .setExpiration(new Date(date.getTime() + TOKEN_TIME * 60)) // 만료 시간
                .setIssuedAt(date) // 발급일
                .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                .compact();
    }

    // 생성된 JWT Cookie 에 저장
    public void addJwtToCookie(String token, String refreshToken, HttpServletResponse res) {
        try {
            token = URLEncoder.encode(token, "utf-8")
                .replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

            //access 토큰을 쿠키에 설정
            Cookie accessCookie = new Cookie(AUTHORIZATION_HEADER, token); // Name-Value
            accessCookie.setPath("/");

            //refresh 토큰을 쿠키에 설정
            Cookie refreshCookie = new Cookie(REFRESH_HEADER, refreshToken);
            refreshCookie.setPath("/"); // 쿠키 경로 설정 (필요에 따라 변경 가능)

            //Response 객체에 Cookie 추가
            res.addCookie(accessCookie);
            res.addCookie(refreshCookie);

        } catch (UnsupportedEncodingException e) {
            log.error(e.toString());
        }
    }

    // JWT 토큰 substring
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        throw new NullPointerException("Not Found Token");
    }

    // 토큰 검증, JWT 위변조 확인
    // parseBuilder() : 구성 성분을 분해하고 분석
    public String validateToken(String accessToken, String refreshTokenValue,
        HttpServletResponse res) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);
            log.debug("Valid JWT Token");
            return accessToken;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error(e.toString());
            throw new JwtException("Invalid JWT signature, 유효하지 않은 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error(e.toString());
            //refresh 토큰 값전달해서 유효 확인
            String value = redisRepository.getValue(REFRESH_PREFIX + refreshTokenValue);
            if (value == null) { // refresh 만료
                throw new JwtException("Expired JWT, 만료된 JWT 입니다.");
            }
            // accessToken 이 만료되었다면 재발급 로직
            try {
                ObjectMapper objectMapper = new ObjectMapper();

                //refreshToken 값
                RefreshToken refreshToken = objectMapper.readValue(value, RefreshToken.class);

                String username = refreshToken.getUsername();
                UserRoleEnum role = refreshToken.getRole();
                Long key = refreshToken.getKey();
                //access 토큰 다시 발급 (Bearer ~~)
                accessToken = createToken(username, role, key);

                // Refresh Token Rotation (기존 Refresh 토큰 제거 후 새로 발급)
                // time 0L 설정후 제거
                Long refreshExpireTime = refreshTokenService.getRefreshTokenTimeToLive(
                    REFRESH_PREFIX + refreshTokenValue);
                redisRepository.setExpire(REFRESH_PREFIX + refreshTokenValue, 0L);

                String newRefreshToken = refreshTokenService.refreshTokenRotation(username, role,
                    refreshExpireTime, key);

                addJwtToCookie(accessToken, newRefreshToken, res);

                //Bearer 제거
                accessToken = substringToken(accessToken);
                return accessToken;

            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        } catch (UnsupportedJwtException e) {
            log.error(e.toString());
            throw new JwtException("Unsupported JWT, 지원되지 않는 JWT 입니다.");
        } catch (IllegalArgumentException e) {
            log.error(e.toString());
            throw new JwtException("JWT claims is empty, 잘못된 JWT 입니다.");
        }
    }

    // 토큰에서 사용자 정보 가져오기
    // Payload 부분에는 토큰에 담긴 정보
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getTokenFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
                    try {
                        return URLDecoder.decode(cookie.getValue(), "UTF-8"); // Encode 되어 넘어간 Value 다시 Decode
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public String getRefreshTokenFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(REFRESH_HEADER)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}

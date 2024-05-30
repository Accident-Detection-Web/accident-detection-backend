package com.example.accidentdetectionservice.global.config;

import com.example.accidentdetectionservice.global.redis.RefreshTokenService;
import com.example.accidentdetectionservice.global.security.JwtAuthenticationFilter;
import com.example.accidentdetectionservice.global.security.JwtAuthorizationFilter;
import com.example.accidentdetectionservice.global.security.UserDetailsServiceImpl;
import com.example.accidentdetectionservice.global.util.jwt.JwtUtil;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, refreshTokenService);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:8080",
                "http://localhost:8081",
                "http://mynextjs.shop",
                "https://backend-capstone.site",
                "http://backend-capstone.site:8080",
                "http://capstone-aiserver.shop"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.REFRESH_HEADER,
            "Cache-Control", "Content-Type","Temporary_Authorization"));
        configuration.setExposedHeaders(Arrays.asList(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.REFRESH_HEADER
            ,"Temporary_Authorization"));
        configuration.setMaxAge(1800L);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf((csrf) -> csrf.disable());

        //CORS 설정
        http.cors((cors) -> cors.configurationSource(configurationSource()));

        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                    .permitAll()
                    .requestMatchers("/", "/health", "/css/**", "/js/**", "/img/**", "/lib/**",
                        "/scss/**", "/favicon.ico").permitAll()
                    .requestMatchers("/auth/users/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v1/api-docs/**").permitAll()
                    .requestMatchers("/api/accident/receiving-data").permitAll()
                    .requestMatchers("/api/notify/subscribe").permitAll()
                    .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        /**
         * @deprecated
         */
//        http.logout()
//            .logoutUrl("/auth/users/logout")   // 로그아웃 처리 URL (= form action url)
//            .logoutSuccessUrl("/") // 로그아웃 성공 후 targetUrl,
//            .addLogoutHandler((request, response, authentication) -> {
//                HttpSession session = request.getSession();
//                if (session != null) {
//                    session.invalidate();
//                }
//            })  // 로그아웃 핸들러 추가
//            .logoutSuccessHandler((request, response, authentication) -> {
//                response.sendRedirect("/");
//            }) // 로그아웃 성공 핸들러
//            .deleteCookies("Authorization", "remember-me", "Refresh"); // 로그아웃 후 삭제할 쿠키 지정

        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}

package com.example.accidentdetectionservice.domain.accident.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RequiredArgsConstructor
@Slf4j(topic = "NAVER API")
@Component
public class ReverseGeocodingService {

    /**
     * @apiNote application-secret.yml 정보 가져오는 부분 refactoring 필요
     */
    @Value("${naver.client-key}")
    private String clientKey;
    @Value("${naver.client-secret-key}")
    private String clientSecretKey;

    private final ObjectMapper objectMapper;

    /**
     * naver cloud platform reverseGeocoding 을 이용한 경도 위도를 주소로 변환
     * @param latitude
     * @param longitude
     * @return address(String type)
     */
    public String getAddress(double latitude, double longitude){
        try {
            String apiUrl = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc";

            String requestUrl = apiUrl + "?coords=" + longitude + "," + latitude + "&output=json";
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "4nqjg17mp4");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY", "6soiuZVRnR7eETaDKQmIf5x9ktGr9D8TnvaavJOg");

            int responseCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode == 200 ? conn.getInputStream() : conn.getErrorStream(), "UTF-8"));


            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            conn.disconnect();

            // JSON 응답을 파싱하여 주소 정보 추출
            String jsonResponse = response.toString();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode resultsNode = rootNode.get("results");
            JsonNode regionNode = resultsNode.get(0).get("region");

            String area1 = regionNode.get("area1").get("name").asText();
            String area2 = regionNode.get("area2").get("name").asText();

            return area1 + " " + area2;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Test
     */
    public static void main(String[] args) {
        ReverseGeocodingService service = new ReverseGeocodingService(new ObjectMapper());
        String address = service.getAddress(37.554520865005, 127.0806325017);
        System.out.println(address);
    }

}

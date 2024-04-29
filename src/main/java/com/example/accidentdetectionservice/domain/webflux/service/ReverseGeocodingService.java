package com.example.accidentdetectionservice.domain.webflux.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class ReverseGeocodingService {

    @Value("${naver.clientId}")
    private String clientId;
    @Value("${naver.clientSecretId")
    private String clientSecretId;

    private ObjectMapper objectMapper;

    public String getAddress(double latitude, double longitude){
        try {
            String apiUrl = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc";

            String requestUrl = apiUrl + "?coords=" + longitude + "," + latitude + "&output=json";
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            conn.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecretId);

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

}

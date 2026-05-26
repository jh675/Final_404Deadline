package com.example.demo.aiSearch.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GeminiApiClient {

    @Value("${gemini.api-key}")
    private String apiKey;

    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    public String callGemini(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 구글 Gemini API 규격에 맞는 JSON 바디 조립
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> textMap = new HashMap<>();
        textMap.put("text", prompt);
        
        Map<String, Object> partsMap = new HashMap<>();
        partsMap.put("parts", Collections.singletonList(textMap));
        
        body.put("contents", Collections.singletonList(partsMap));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_URL + apiKey, entity, Map.class);
            
            // 구글 응답 JSON에서 text 데이터 파싱해오기
            List candidates = (List) response.getBody().get("candidates");
            Map candidate = (Map) candidates.get(0);
            Map content = (Map) candidate.get("content");
            List parts = (List) content.get("parts");
            Map part = (Map) parts.get(0);
            
            return (String) part.get("text");
        } catch (Exception e) {
            return "AI 비서와 통신 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}

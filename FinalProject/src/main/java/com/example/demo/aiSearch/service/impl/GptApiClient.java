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
import org.springframework.web.client.RestTemplate;

//@Component
public class GptApiClient {

    @Value("${openai.api-key}")
    private String apiKey;

    // GPT 채팅 API 엔드포인트
    private final String GPT_URL = "https://api.openai.com/v1/chat/completions";
    
    // RestTemplate 재사용 (1분 컷 최적화 유지!)
    private final RestTemplate restTemplate = new RestTemplate();

    public String callGpt(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 구글과 다른 GPT용 JSON 바디 조립
        Map<String, Object> body = new HashMap<>();
        
        // 
        body.put("model", "gpt-4o"); 
        body.put("temperature", 0.2);
        
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        
        body.put("messages", Collections.singletonList(message));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(GPT_URL, entity, Map.class);
            
            // ✨ GPT 응답 JSON 파싱 (구조가 다름)
            List choices = (List) response.getBody().get("choices");
            Map choice = (Map) choices.get(0);
            Map msg = (Map) choice.get("message");
            
            return (String) msg.get("content");
        } catch (Exception e) {
            return "GPT 비서와 통신 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
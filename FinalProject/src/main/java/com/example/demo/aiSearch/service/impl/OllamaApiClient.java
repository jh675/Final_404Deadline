package com.example.demo.aiSearch.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

//@Component
public class OllamaApiClient {
//속도가 너무 느린 문제가 있음 검색 시간이 약 1분 걸림
    private final String OLLAMA_URL = "http://192.168.0.6:11434/api/generate";
    private final RestTemplate restTemplate = new RestTemplate();

    public String callOllama(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama3.1"); // 다운로드 받은 모델 이름 (예: llama3, qwen 등)
        body.put("prompt", prompt);
        body.put("stream", false); // 한 번에 답변 받기

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    OLLAMA_URL,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {});
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                return "로컬 AI 비서와 통신 중 알 수 없는 오류가 발생했습니다.";
            }
            Object text = responseBody.get("response");
            return text instanceof String s ? s : "AI 응답 형식을 해석할 수 없습니다.";
        } catch (HttpStatusCodeException e) {
            int statusCode = e.getStatusCode().value();
            String errorBody = e.getResponseBodyAsString(); // Ollama가 보낸 상세 에러 내용
            
            if (statusCode == 404) {
                return "모델을 찾을 수 없습니다. cmd에서 `ollama run qwen2.5:0.5b`를 실행해 모델이 다운로드되어 있는지 확인해 주세요.";
            } else if (statusCode == 500) {
                return "Ollama 서버 내부 오류입니다. (RAM 메모리 부족 또는 버퍼 할당 실패일 확률이 높습니다.) 상세: " + errorBody;
            } else {
                return "[HTTP " + statusCode + " 에러]** 상세 내용: " + errorBody;
            }
            
        // 서버가 꺼져있는 경우
        } catch (ResourceAccessException e) {
            return "Ollama 서버에 접속할 수 없습니다. 윈도우 우측 하단 트레이 아이콘에 Ollama가 켜져 있는지 확인해 주세요!";
            
        // 그 외 알 수 없는 에러
        } catch (Exception e) {
            return "로컬 AI 비서와 통신 중 알 수 없는 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
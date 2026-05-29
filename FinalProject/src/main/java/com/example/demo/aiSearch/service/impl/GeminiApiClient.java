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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

//@Component
public class GeminiApiClient {

	@Value("${gemini.api-key}")
	private String apiKey;

	private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

	// RestTemplate을 1번만 생성하고 계속 재사용
	private final RestTemplate restTemplate = new RestTemplate();

	public String callGemini(String prompt) {
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
		} catch (HttpStatusCodeException e) {
			int statusCode = e.getStatusCode().value();

			if (statusCode == 429) {
				return "무료 제공 사용량을 모두 소진했거나, 질문을 너무 빠르게 연속해서 입력하셨습니다. 잠시만 기다렸다가 다시 질문해 주세요!";
			} else if (statusCode == 503) {
				return "현재 AI 서버 접속자가 많아 일시적으로 혼잡합니다. 1~2분 뒤에 다시 시도해 주세요.";
			} else if (statusCode == 400) {
				return "질문이나 데이터의 길이가 너무 길어서 AI가 처리하지 못했습니다. (제한 용량 초과)";
			} else if (statusCode == 403 || statusCode == 401) {
				return "AI API 인증 키에 문제가 있습니다. 관리자에게 문의해 주세요.";
			} else {
				return "AI 서버와 통신 중 문제가 발생했습니다. (에러 코드: " + statusCode + ") 잠시 후 다시 시도해 주세요.";
			}

			// 통신 자체가 아예 안 되는 경우 (네트워크 단절 등)
		} catch (Exception e) {
			return "AI 비서와 통신을 연결할 수 없습니다. 인터넷 연결이나 서버 상태를 확인해 주세요.";
		}
	}
}

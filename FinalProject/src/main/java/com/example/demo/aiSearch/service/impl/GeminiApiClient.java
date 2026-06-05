package com.example.demo.aiSearch.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

//@Component
public class GeminiApiClient {

	@Value("${gemini.api-key}")
	private String apiKey;

	private final String GEMINI_URL =
			"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

	private final RestTemplate restTemplate = new RestTemplate();

	public String callGemini(String prompt) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> body = new HashMap<>();
		Map<String, Object> textMap = new HashMap<>();
		textMap.put("text", prompt);

		Map<String, Object> partsMap = new HashMap<>();
		partsMap.put("parts", Collections.singletonList(textMap));

		body.put("contents", Collections.singletonList(partsMap));

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

		try {
			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
					GEMINI_URL + apiKey,
					HttpMethod.POST,
					entity,
					new ParameterizedTypeReference<Map<String, Object>>() {});

			Map<String, Object> responseBody = response.getBody();
			if (responseBody == null) {
				return "AI 비서와 통신을 연결할 수 없습니다. 인터넷 연결이나 서버 상태를 확인해 주세요.";
			}
			return extractGeminiText(responseBody);
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
		} catch (Exception e) {
			return "AI 비서와 통신을 연결할 수 없습니다. 인터넷 연결이나 서버 상태를 확인해 주세요.";
		}
	}

	@SuppressWarnings("unchecked")
	private static String extractGeminiText(Map<String, Object> responseBody) {
		Object candidatesObj = responseBody.get("candidates");
		if (!(candidatesObj instanceof List<?> candidates) || candidates.isEmpty()) {
			return "AI 응답 형식을 해석할 수 없습니다.";
		}
		Object candidateObj = candidates.get(0);
		if (!(candidateObj instanceof Map<?, ?>)) {
			return "AI 응답 형식을 해석할 수 없습니다.";
		}
		Map<String, Object> candidate = (Map<String, Object>) candidateObj;
		Object contentObj = candidate.get("content");
		if (!(contentObj instanceof Map<?, ?>)) {
			return "AI 응답 형식을 해석할 수 없습니다.";
		}
		Map<String, Object> content = (Map<String, Object>) contentObj;
		Object partsObj = content.get("parts");
		if (!(partsObj instanceof List<?> parts) || parts.isEmpty()) {
			return "AI 응답 형식을 해석할 수 없습니다.";
		}
		Object partObj = parts.get(0);
		if (!(partObj instanceof Map<?, ?>)) {
			return "AI 응답 형식을 해석할 수 없습니다.";
		}
		Map<String, Object> part = (Map<String, Object>) partObj;
		Object text = part.get("text");
		return text instanceof String s ? s : "AI 응답 형식을 해석할 수 없습니다.";
	}
}

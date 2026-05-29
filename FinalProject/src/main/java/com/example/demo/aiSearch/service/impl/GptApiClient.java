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
import org.springframework.web.client.HttpStatusCodeException;
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

			// GPT 응답 JSON 파싱 (gemini랑 구조가 다름)
			List choices = (List) response.getBody().get("choices");
			Map choice = (Map) choices.get(0);
			Map msg = (Map) choice.get("message");

			return (String) msg.get("content");
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
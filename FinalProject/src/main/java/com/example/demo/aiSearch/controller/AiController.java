package com.example.demo.aiSearch.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.aiSearch.service.impl.AiSearchServiceImpl;
import com.example.demo.login.service.UserVO; // 팀원분 도메인에 맞게 패키지 수정 필요

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiSearchServiceImpl aiSearchService;

    @PostMapping("/assistant")
    public ResponseEntity<String> askAiAssistant(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserVO loginUser, // 현재 로그인한 사용자 정보
            HttpSession session // 세션 내 현재 프로젝트(session.project) 참조용
    ) {
        String userMessage = request.get("message");
        Long userId = loginUser.getId(); 
        String bizNo = loginUser.getBizNo();
        
        // 세션에서 현재 활성화된 프로젝트 정보를 꺼내봄 (있을 수도 있고 없을 수도 있음)
        // 기존 템플릿의 `session.project` 구조 반영
        Long currentPrj = (Long) session.getAttribute("currentProjectId"); 

        // 비즈니스 로직 서비스를 호출하여 최종 조립된 답변을 받아옵니다.
        String aiAnswer = aiSearchService.processIntelligentSearch(userMessage, userId, currentPrj, bizNo);
        
        return ResponseEntity.ok(aiAnswer);
    }
}
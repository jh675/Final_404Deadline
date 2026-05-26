package com.example.demo.aiSearch.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.project.issue.mapper.IssueMapper;
import com.example.demo.project.issue.service.IssueInputVO;
import com.example.demo.project.issue.service.IssueOutputVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiSearchServiceImpl {

    private final IssueMapper issueMapper; 
    private final GeminiApiClient geminiApiClient;

    public String processIntelligentSearch(String userMessage, Long userId, Long prjId) {
        
        // 1. 의도 분석 (이전과 동일)
        String routingIntent = analyzeUserIntentWithLlM(userMessage);
        StringBuilder contextData = new StringBuilder();
        
        // 2. 기존 Mapper 재활용하여 데이터 추출
        if (routingIntent.contains("MY_TASK") || routingIntent.contains("GENERAL")) {
            
            // IssueInputVO 객체를 생성해서 검색 조건으로 사용합니다.
            IssueInputVO searchParam = new IssueInputVO();
            searchParam.setMemId(userId); // "내가 맡은 일" 셋팅
            // searchParam.setStatusCd("02ISSUESTAT"); // 필요하다면 '진행중' 상태만 가져오도록 셋팅 가능
            
            if (prjId != null) {
            	searchParam.setPrjId(prjId);
                
                contextData.append("[현재 선택된 프로젝트의 내 이슈목록]\n");
                
                List<IssueOutputVO> myIssues = issueMapper.selectIssueList(searchParam);
                contextData.append(myIssues.toString());
                
            } else {
                // 프로젝트 선택 없이 전체에서 찾을 때 (prjId 셋팅 없이 호출)
                contextData.append("[전체 프로젝트의 내 업무목록]\n");
                List<IssueOutputVO> allMyIssues = issueMapper.selectIssueList(searchParam);
                contextData.append(allMyIssues.toString());
            }
        } else if (routingIntent.contains("PROJECT_LIST")) {
            // 프로젝트 목록 조회도 기존 Mapper 활용
            // 추가예정 (다른 모듈에 대하여 확장가능)
        }
        
        // 3. Prompt 조립 및 Gemini 호출 
        String finalPrompt = String.format(
            "너는 우리 협업 툴의 똑똑한 AI 비서야. 아래 제공된 [시스템 데이터]를 기반으로 질문에 답해줘.\n\n" +
            "[시스템 데이터]\n%s\n\n" +
            "[사용자 질문]\n%s\n\n" +
            "답변 지침: 데이터에 기반해서만 팩트 기반으로 말하고, 마크다운 스타일을 활용해서 가독성 좋게 요약해줘.",
            contextData.toString(), userMessage
        );

        return geminiApiClient.callGemini(finalPrompt);
    }

    private String analyzeUserIntentWithLlM(String userMessage) {
        return "MY_TASK"; // 임시 리턴
    }
}
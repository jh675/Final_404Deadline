package com.example.demo.project.member.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.demo.project.member.service.MemberListCriteria;
import com.example.demo.project.member.service.MemberService;
import com.example.demo.project.member.service.ProjectMemberRowVO;
import lombok.RequiredArgsConstructor;

/**
 * 프로젝트 멤버 관리 — {@code project/member/memberManagement.html}
 */
@Controller
@RequestMapping("/project/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /** 구성원 목록 화면 — 검색 조건 반영 후 Thymeleaf에 rows 전달 */
    @GetMapping("/memberManagement")
    public String memberManagementPage(
            @RequestParam("prjId") Long prjId,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String grpName,
            @RequestParam(required = false) String prjStartFrom,
            @RequestParam(required = false) String prjStartTo,
            Model model) {

        MemberListCriteria criteria = MemberListCriteria.builder()
                .prjId(prjId)
                .memberName(nullToEmpty(memberName))
                .grpName(nullToEmpty(grpName))
                .prjStartFrom(nullToEmpty(prjStartFrom))
                .prjStartTo(nullToEmpty(prjStartTo))
                .build();

        List<ProjectMemberRowVO> rows = memberService.selectProjectMemberList(criteria);

        model.addAttribute("rows", rows);
        model.addAttribute("prjId", prjId);
        model.addAttribute("memberName", criteria.getMemberName());
        model.addAttribute("grpName", criteria.getGrpName());
        model.addAttribute("prjStartFrom", criteria.getPrjStartFrom());
        model.addAttribute("prjStartTo", criteria.getPrjStartTo());
        return "project/member/memberManagement";
    }

    /** 구성원 목록 JSON (AJAX 검색용, 현재 HTML에서는 미사용) */
    @GetMapping("/memberManagementList")
    @ResponseBody
    public Map<String, Object> memberManagementList(
            @RequestParam("prjId") Long prjId,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String grpName,
            @RequestParam(required = false) String prjStartFrom,
            @RequestParam(required = false) String prjStartTo) {

        MemberListCriteria criteria = MemberListCriteria.builder()
                .prjId(prjId)
                .memberName(nullToEmpty(memberName))
                .grpName(nullToEmpty(grpName))
                .prjStartFrom(nullToEmpty(prjStartFrom))
                .prjStartTo(nullToEmpty(prjStartTo))
                .build();

        List<ProjectMemberRowVO> rows = memberService.selectProjectMemberList(criteria);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", rows);
        body.put("prjId", prjId);
        return body;
    }

    /** Grid에서 선택한 구성원을 프로젝트에서 제거 (MEMBER 행 삭제) */
    @PostMapping("/deleteMembers")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteMembers(
            @RequestBody(required = false) MemberDeleteRequest body) {
        Map<String, Object> ok = new LinkedHashMap<>();
        ok.put("ok", true);
        try {
            if (body == null || body.prjId() == null) {
                return badRequest("요청이 올바르지 않습니다.");
            }
            List<ProjectMemberRowVO> rows = body.members() == null
                    ? List.of()
                    : body.members().stream()
                            .map(k -> ProjectMemberRowVO.builder()
                                    .userId(k.userId())
                                    .grpId(k.grpId())
                                    .build())
                            .toList();
            memberService.deleteMembers(body.prjId(), rows);
            return ResponseEntity.ok(ok);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("ok", false, "message", "멤버 제거 중 오류가 발생했습니다."));
        }
    }

    private static ResponseEntity<Map<String, Object>> badRequest(String message) {
        return ResponseEntity.badRequest().body(Map.of("ok", false, "message", message));
    }

    /** deleteMembers 요청 JSON */
    public record MemberDeleteRequest(Long prjId, List<MemberKey> members) {}

    /** MEMBER 복합키 — userId + grpId */
    public record MemberKey(Long userId, Long grpId) {}

    /** MyBatis 동적 SQL에서 null 대신 빈 문자열로 통일 */
    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}

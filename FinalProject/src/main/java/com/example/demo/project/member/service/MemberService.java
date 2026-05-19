package com.example.demo.project.member.service;

import java.util.List;

/** 프로젝트 구성원 목록·제거 */
public interface MemberService {

    /** prjId 기준 구성원 목록 (이름·그룹·투입일 검색) */
    List<ProjectMemberRowVO> selectProjectMemberList(MemberListCriteria criteria);

    /** 구성원 등록 화면 — 프로젝트명 */
    String selectProjectName(Long prjId);

    /**
     * 프로젝트 수행 기업 소속 사용자 목록 (STATUS_CD=01ACTIVE).
     * {@code excludeRegistered=true} 이면 MEMBER.USER_ID에 없는 사용자만.
     */
    List<CompanyMemberRowVO> selectCompanyMembersByPrjId(Long prjId, boolean excludeRegistered);

    List<MemberGroupPickRowVO> selectProjectGroupsByPrjId(Long prjId);

    MemberDetailVO selectMemberDetail(Long prjId, Long userId, Long grpId);

    List<MemberIssueRowVO> selectMemberIssues(Long prjId, Long userId, Long grpId);

    /** 선택 (userId, grpId) MEMBER 행 삭제 */
    void deleteMembers(Long prjId, List<ProjectMemberRowVO> rows);

    /** 구성원 등록 — MEMBER INSERT (mem_seq) */
    Long registerMember(
            Long prjId,
            Long userId,
            Long grpId,
            String prjStartDate,
            String prjEndDate);

    /** 구성원 수정 — 종료일·소속 그룹 (투입 시작일 변경 없음) */
    void updateMember(
            Long prjId,
            Long userId,
            Long oldGrpId,
            Long grpId,
            String prjEndDate);
}

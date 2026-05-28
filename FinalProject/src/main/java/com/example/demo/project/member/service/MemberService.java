package com.example.demo.project.member.service;

import java.util.List;

/** 프로젝트 구성원 목록·제거 */
public interface MemberService {

    /** prjId 기준 구성원 목록 (이름·그룹·투입일 검색) */
    List<ProjectMemberRowVO> selectProjectMemberList(MemberListCriteria criteria);

    /** 구성원 등록 화면 — 프로젝트명 */
    String selectProjectName(Long prjId);

    /** 구성원 등록 — 프로젝트 기간(투입일·종료일 제한용) */
    ProjectPeriodVO selectProjectPeriod(Long prjId);

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

    /**
     * 구성원 일괄 등록 — 선택한 userId 전부에 대해 동일한 그룹·기간으로 INSERT ALL.
     * 사전 검증을 모두 통과한 뒤 한 번에 N건 INSERT. 하나라도 실패 시 전체 롤백.
     * 등록된 건수를 반환한다.
     */
    int registerMembers(
            Long prjId,
            java.util.List<Long> userIds,
            Long grpId,
            String prjStartDate,
            String prjEndDate);

    /** 구성원 수정 — 투입기간·소속 그룹 */
    void updateMember(
            Long prjId,
            Long userId,
            Long oldGrpId,
            Long grpId,
            String prjStartDate,
            String prjEndDate);
}

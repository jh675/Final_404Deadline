package com.example.demo.project.member.service;

import java.util.List;

/** 프로젝트 구성원 목록·제거 */
public interface MemberService {

    /** prjId 기준 구성원 목록 (이름·그룹 검색) */
    List<ProjectMemberRowVO> selectProjectMemberList(MemberListCriteria criteria);

    /**
     * 프로젝트 수행 기업 소속 사용자 목록 (STATUS_CD=01ACTIVE).
     * {@code excludeRegistered=true} 이면 현재 프로젝트(prjId)에 미등록된 사용자만.
     */
    List<CompanyMemberRowVO> selectCompanyMembersByPrjId(Long prjId, boolean excludeRegistered);

    List<MemberGroupPickRowVO> selectProjectGroupsByPrjId(Long prjId);

    MemberDetailVO selectMemberDetail(Long prjId, Long userId, Long grpId);

    List<MemberIssueRowVO> selectMemberIssues(Long prjId, Long userId, Long grpId);

    /** 선택 (userId, grpId) MEMBER 행 삭제 */
    void deleteMembers(Long prjId, List<ProjectMemberRowVO> rows);

    /**
     * 구성원 일괄 등록 — 선택한 userId 전부에 대해 동일한 그룹으로 INSERT.
     * 사전 검증을 모두 통과한 뒤 한 번에 N건 INSERT. 하나라도 실패 시 전체 롤백.
     */
    int registerMembers(Long prjId, java.util.List<Long> userIds, Long grpId);

    /** 구성원 수정 — 소속 그룹 */
    void updateMember(Long prjId, Long userId, Long oldGrpId, Long grpId);
}

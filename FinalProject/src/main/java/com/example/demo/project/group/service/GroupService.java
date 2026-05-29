package com.example.demo.project.group.service;

import java.util.List;

/** 프로젝트 그룹 목록·삭제 */
public interface GroupService {

    /** prjId 기준 그룹 목록 (그룹명·생성일 검색, 멤버 수 포함) */
    List<ProjectGroupRowVO> selectProjectGroupList(GroupListCriteria criteria);

    GroupDetailVO selectGroupDetail(Long prjId, Long grpId);

    /** 그룹 등록 화면 — 프로젝트명 조회 */
    String selectProjectName(Long prjId);

    /** 동일 프로젝트 내 그룹명 존재 여부 */
    boolean existsGroupName(Long prjId, String grpName);

    List<GroupMemberDetailRowVO> selectGroupMembers(Long prjId, Long grpId);

    /** 그룹 등록 모달 — 프로젝트 구성원 선택 목록 */
    List<GroupMemberPickRowVO> selectGroupMemberPickList(Long prjId);

    List<GroupRoleDetailRowVO> selectGroupRoles(Long prjId, Long grpId);

    /** 해당 그룹에 역할이 GRP_ROLE로 연결되어 있는지(프로젝트 일치 포함) */
    boolean isRoleAssignedToGroup(Long prjId, Long grpId, Long roleCd);

    /** 그룹 제거 — Oracle {@code PROC_GRP_DELETE}를 그룹 ID마다 호출 */
    void deleteGroups(Long prjId, List<Long> grpIds);

    /**
     * 그룹 등록 — Oracle {@code PROC_GRP_INSERT}.
     * {@code userIds}가 null이거나 비어 있으면 프로시저에 memIds를 넘기지 않음(null).
     */
    void insertGroup(Long prjId, String grpName, List<Long> userIds, List<Long> roleCds);

    /**
     * 그룹 구성원 수정 — Oracle {@code PROC_GRP_UPDATE} (목록과 동기화·제거 포함).
     * {@code userIds}가 빈 목록이면 해당 그룹 구성원을 모두 제거한다.
     */
    void updateGroup(Long prjId, Long grpId, List<Long> userIds);

    /** 그룹 보유 권한(GRP_ROLE) 동기화 — 추가는 PROC_ROLE_UPDATE, 제거는 PROC_GRP_ROLE_DELETE */
    void updateGroupRoles(Long prjId, Long grpId, List<Long> roleCds);
}

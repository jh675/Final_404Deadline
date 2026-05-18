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

    List<GroupRoleDetailRowVO> selectGroupRoles(Long prjId, Long grpId);

    /** 그룹 제거 — Oracle {@code PROC_GRP_DELETE}를 그룹 ID마다 호출 */
    void deleteGroups(Long prjId, List<Long> grpIds);

    /**
     * 그룹 등록 — Oracle {@code PROC_GRP_INSERT}.
     * {@code userIds}가 null이거나 비어 있으면 프로시저에 memIds를 넘기지 않음(null).
     */
    void insertGroup(Long prjId, String grpName, List<Long> userIds);
}

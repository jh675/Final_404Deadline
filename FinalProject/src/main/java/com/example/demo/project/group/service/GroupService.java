package com.example.demo.project.group.service;

import java.util.List;

/** 프로젝트 그룹 목록·삭제 */
public interface GroupService {

    /** prjId 기준 그룹 목록 (그룹명·생성일 검색, 멤버 수 포함) */
    List<ProjectGroupRowVO> selectProjectGroupList(GroupListCriteria criteria);

    /** MEMBER → GRP_ROLE → GRP 순으로 삭제 */
    void deleteGroups(Long prjId, List<Long> grpIds);
}

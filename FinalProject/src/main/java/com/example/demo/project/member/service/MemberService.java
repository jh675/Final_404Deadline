package com.example.demo.project.member.service;

import java.util.List;

/** 프로젝트 구성원 목록·제거 */
public interface MemberService {

    /** prjId 기준 구성원 목록 (이름·그룹·투입일 검색) */
    List<ProjectMemberRowVO> selectProjectMemberList(MemberListCriteria criteria);

    /** 선택 (userId, grpId) MEMBER 행 삭제 */
    void deleteMembers(Long prjId, List<ProjectMemberRowVO> rows);
}

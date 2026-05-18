package com.example.demo.project.member.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.project.member.service.MemberListCriteria;
import com.example.demo.project.member.service.ProjectMemberRowVO;

/** 구성원 MyBatis — USERS + MEMBER + GRP 조인 */
@Mapper
public interface MemberMapper {

    /** 프로젝트 구성원 목록 */
    List<ProjectMemberRowVO> selectProjectMemberList(MemberListCriteria criteria);

    /** (userId, grpId) 쌍에 해당하는 MEMBER 삭제 — prjId로 범위 제한 */
    int deleteMemberRows(
            @Param("prjId") Long prjId,
            @Param("rows") List<ProjectMemberRowVO> rows);
}

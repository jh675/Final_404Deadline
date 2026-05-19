package com.example.demo.project.group.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.project.group.service.GroupDetailVO;
import com.example.demo.project.group.service.GroupInsertProcParam;
import com.example.demo.project.group.service.GroupListCriteria;
import com.example.demo.project.group.service.GroupMemberDetailRowVO;
import com.example.demo.project.group.service.GroupRoleDetailRowVO;
import com.example.demo.project.group.service.ProjectGroupRowVO;

/** 그룹 MyBatis — GRP 목록·연쇄 삭제 */
@Mapper
public interface GroupMapper {

    /** 프로젝트 그룹 목록 (멤버 수 집계) */
    List<ProjectGroupRowVO> selectProjectGroupList(GroupListCriteria criteria);

    GroupDetailVO selectGroupDetail(@Param("prjId") Long prjId, @Param("grpId") Long grpId);

    /** 그룹 등록 화면 — 프로젝트명만 조회 */
    String selectProjectNameByPrjId(@Param("prjId") Long prjId);

    /** 동일 프로젝트·그룹명 존재 여부 (PRJ_ID + NAME) */
    int countGrpByPrjIdAndName(
            @Param("prjId") Long prjId,
            @Param("grpName") String grpName);

    List<GroupMemberDetailRowVO> selectGroupMembers(@Param("prjId") Long prjId, @Param("grpId") Long grpId);

    List<GroupRoleDetailRowVO> selectGroupRoles(@Param("prjId") Long prjId, @Param("grpId") Long grpId);

    /** DB {@code PROC_GRP_DELETE} — GRP_ROLE, MEMBER, GRP 정리 후 COMMIT */
    void callProcGrpDelete(@Param("grpId") Long grpId);

    /** DB {@code PROC_GRP_INSERT} — memIds null이면 구성원 없이 그룹만 생성 */
    void callProcGrpInsert(GroupInsertProcParam param);
}

package com.example.demo.project.group.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.project.group.service.GroupListCriteria;
import com.example.demo.project.group.service.ProjectGroupRowVO;

/** 그룹 MyBatis — GRP 목록·연쇄 삭제 */
@Mapper
public interface GroupMapper {

    /** 프로젝트 그룹 목록 (멤버 수 집계) */
    List<ProjectGroupRowVO> selectProjectGroupList(GroupListCriteria criteria);

    /** 1단계: 그룹 소속 MEMBER 삭제 */
    int deleteGroupMembers(@Param("prjId") Long prjId, @Param("grpIds") List<Long> grpIds);

    /** 2단계: 그룹-권한 GRP_ROLE 삭제 */
    int deleteGroupRoles(@Param("prjId") Long prjId, @Param("grpIds") List<Long> grpIds);

    /** 3단계: GRP 본체 삭제 */
    int deleteGroupRows(@Param("prjId") Long prjId, @Param("grpIds") List<Long> grpIds);
}

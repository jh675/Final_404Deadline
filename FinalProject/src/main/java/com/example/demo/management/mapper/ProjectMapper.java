package com.example.demo.management.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.login.service.UserVO;
import com.example.demo.management.service.ModulesVO;
import com.example.demo.management.service.ProjectVO;
import com.example.demo.project.group.service.GroupDetailVO;
import com.example.demo.project.member.service.MemberDetailVO;
import com.example.demo.project.option.service.RoleVO;
import com.example.demo.project.wiki.service.WikiVO;

@Mapper
public interface ProjectMapper {
	List<ProjectVO> listProject(ProjectVO vo );
	List<ProjectVO> userProjectList(ProjectVO vo);
	int projectInsert(ProjectVO vo);
	Long findIdByIdentifier(ProjectVO vo);
	List<UserVO> searchUsersByBizNo(@Param("bizNo") String bizNo, @Param("term") String term);
	void groupInsert(GroupDetailVO gVo);
	void memberInsert(MemberDetailVO mvo);
	void wikiInsert(WikiVO wVo);
	void roleInsert(RoleVO rVo);
	void rolemenuInsert(RoleVO rVo);
	void grproleInsert(RoleVO rVo);
	int projectHide(ProjectVO vo);
	int projectDelete(ProjectVO vo);
	int countChildProject(Long id);
	ProjectVO gomain(ProjectVO vo);
	ProjectVO getprojectid(Long id);
	List<ModulesVO> listModules(Long projectId);
	List<GroupDetailVO> listGroup(GroupDetailVO gvo);
	List<MemberDetailVO> listMember(MemberDetailVO mvo);
	int projectUpdate(ProjectVO vo);
} 

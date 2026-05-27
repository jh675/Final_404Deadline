package com.example.demo.management.service;

import java.util.List;

import com.example.demo.login.service.UserVO;
import com.example.demo.project.group.service.GroupDetailVO;
import com.example.demo.project.member.service.MemberDetailVO;
import com.example.demo.project.option.service.RoleVO;
import com.example.demo.project.wiki.service.WikiVO;

public interface ProjectService {
	List<ProjectVO> listProject(ProjectVO vo);
	List<ProjectVO> userProjectList(ProjectVO vo);
	int projectInsert(ProjectVO vo );
	List<UserVO> searchUsersByBizNo(String bizNo, String term);
	void insertProjectWithModules(ProjectVO vo, List<String> moduleList,GroupDetailVO gVo,MemberDetailVO mVo,WikiVO wVo, RoleVO rVo);
	int projectHide(ProjectVO vo);
	int projectDelete(ProjectVO vo);
	boolean hasChildProject(Long id);
	ProjectVO getprojectid(Long id);
	List<ModulesVO> listModules(Long projectId);
	List<GroupDetailVO> listGroup(GroupDetailVO gvo);
	List<MemberDetailVO> listMember(MemberDetailVO mvo);
	int updateProject(ProjectVO vo, List<String> moduleList);
}

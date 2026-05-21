package com.example.demo.management.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.demo.login.service.UserVO;
import com.example.demo.management.mapper.ProjectMapper;
import com.example.demo.management.service.ModulesVO;
import com.example.demo.management.service.ProjectService;
import com.example.demo.management.service.ProjectVO;
import com.example.demo.project.group.service.GroupDetailVO;
import com.example.demo.project.member.service.MemberDetailVO;
import com.example.demo.project.option.service.RoleVO;
import com.example.demo.project.wiki.service.WikiVO;

import jakarta.transaction.Transactional;

@Service
@Primary
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	ProjectMapper projectMapper;

	@Override
	public List<ProjectVO> listProject(ProjectVO vo) {
		return projectMapper.listProject(vo);
	}
	
	@Override
	public List<ProjectVO> userProjectList(ProjectVO vo){
		return projectMapper.userProjectList(vo);
	}
	
	@Override
	public int projectInsert(ProjectVO vo) {
		return projectMapper.projectInsert(vo);
	}
	
	@Override
    public List<UserVO> searchUsersByBizNo(String bizNo, String term) {
        return projectMapper.searchUsersByBizNo(bizNo, term);
    }
	
	@Transactional
	public void insertProjectWithModules(ProjectVO vo, List<String> moduleList, GroupDetailVO gVo, MemberDetailVO mvo,WikiVO wVo,RoleVO rVo) {
	    // 1. 프로젝트 삽입 (XML의 selectKey 덕분에 실행 후 vo.getId()에 값이 담깁니다)
	    projectMapper.projectInsert(vo);

	    // 2. 모듈 리스트가 있다면 반복문으로 ModulesVO를 만들어 삽입
	    if (moduleList != null) {
	        for (String moduleName : moduleList) {
	            ModulesVO mVo = new ModulesVO();
	            mVo.setPrjId(vo.getId());       // 방금 들어간 프로젝트 PK
	            mVo.setEnaNameCd(moduleName);   // 모듈 코드 (GANTT, CALENDAR 등)
	            projectMapper.moduleInsert(mVo); // 이 부분 매퍼 인터페이스와 XML에 추가 필요!
	        }
	    }
	    if(gVo != null) {
	    	gVo.setPrjId(Long.valueOf(vo.getId()));
	    	projectMapper.groupInsert(gVo);
	    	Long groupId = gVo.getGrpId();
	    	if(mvo != null) {
	    		mvo.setGrpId(groupId);
	    		mvo.setUserId(vo.getUserId());
	    		projectMapper.memberInsert(mvo);
	    	}
	    }
	    if(wVo != null) {
	    	wVo.setPrjId(Long.valueOf(vo.getId()));
	    	projectMapper.wikiInsert(wVo);
	    }
	    if(rVo != null) {
	    	rVo.setPrjId(Long.valueOf(vo.getId()));
	    	projectMapper.roleInsert(rVo);
	    	
	    	Long roleCd = rVo.getRoleCd();
	    	
	    	rVo.setRoleCd(roleCd);
	    	rVo.setRoleId("ROLE_ISSUE_ALL");
	    	projectMapper.rolemenuInsert(rVo);
	    	
	    	rVo.setRoleId("ROLE_MEMBER_ALL");
	    	projectMapper.rolemenuInsert(rVo);
	    	
	    	rVo.setRoleId("ROLE_GROUP_ALL");
	    	projectMapper.rolemenuInsert(rVo);
	    	
	    	rVo.setRoleId("ROLE_HISTORY_VIEW");
	    	projectMapper.rolemenuInsert(rVo);
	    	
	    	rVo.setGrpId(gVo.getGrpId());
	    	projectMapper.grproleInsert(rVo);
	    }
	}
	
	@Override
	public int projectHide(ProjectVO vo) {
		return projectMapper.projectHide(vo);
	}
	
	@Override
	public int projectDelete(ProjectVO vo) {
		return projectMapper.projectDelete(vo);
	}
	
	@Override
    public boolean hasChildProject(Long id) {
		
        return projectMapper.countChildProject(id) > 0;
    }
	
	@Override
	public ProjectVO getprojectid(Long id) {
		return projectMapper.getprojectid(id);
	}
	
	@Override
	public List<ModulesVO> listModules(Long projectId){
		return projectMapper.listModules(projectId);
	}
	
	@Override
	public List<GroupDetailVO> listGroup(GroupDetailVO gvo){
		return projectMapper.listGroup(gvo);
	}
	
	@Override
	public List<MemberDetailVO> listMember(MemberDetailVO mvo){
		return projectMapper.listMember(mvo);
	}
	
	
}

package com.example.demo.management.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.demo.alarm.event.NotificationEvent;
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
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;

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
	    if (moduleList != null && !moduleList.isEmpty()) {
	        vo.setEnaId(String.join(",", moduleList));
	    }

	    projectMapper.projectInsert(vo);

	    if (vo.getResultStatus() != null && !"OK".equalsIgnoreCase(vo.getResultStatus().trim())) {
	        throw new IllegalStateException(
	            vo.getResultMsg() != null ? vo.getResultMsg() : "프로젝트 생성에 실패했습니다.");
	    }

	    Long projectId = projectMapper.findIdByIdentifier(vo);
	    if (projectId == null) {
	        throw new IllegalStateException("생성된 프로젝트 ID를 조회할 수 없습니다.");
	    }
	    vo.setId(projectId);
	    
	    eventPublisher.publishEvent(new NotificationEvent(
	            this,
	            "프로젝트가 생성되었습니다: " + vo.getPrjName()
	        ));
	    
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
	
	@Override
	@Transactional
	public int updateProject(ProjectVO vo, List<String> moduleList) {
	    if (moduleList != null && !moduleList.isEmpty()) {
	        vo.setEnaId(String.join(",", moduleList));
	    }
	    
	    if (vo.getPrjStatusCd() != null) {
	        eventPublisher.publishEvent(new NotificationEvent(
	            this,
	            "프로젝트 진행상태가 변경되었습니다: "
	            + vo.getPrjName() + " → " + vo.getPrjStatusCd()
	        ));
	    }
	    
	    return projectMapper.projectUpdate(vo);
	}
}

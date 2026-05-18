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
	public int projectInsert(ProjectVO vo) {
		return projectMapper.projectInsert(vo);
	}
	
	@Override
    public List<UserVO> searchUsersByBizNo(String bizNo, String term) {
        return projectMapper.searchUsersByBizNo(bizNo, term);
    }
	
	@Transactional
	public void insertProjectWithModules(ProjectVO vo, List<String> moduleList) {
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
	}
}

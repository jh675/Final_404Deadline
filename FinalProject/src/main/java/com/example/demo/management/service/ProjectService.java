package com.example.demo.management.service;

import java.util.List;

import com.example.demo.login.service.UserVO;

public interface ProjectService {
	List<ProjectVO> listProject(ProjectVO vo);
	int projectInsert(ProjectVO vo );
	List<UserVO> searchUsersByBizNo(String bizNo, String term);
	void insertProjectWithModules(ProjectVO vo, List<String> moduleList);
}

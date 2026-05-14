package com.example.demo.management.service;

import java.util.List;

public interface ProjectService {
	List<ProjectVO> listProject(ProjectVO vo);
	int projectInsert(ProjectVO vo );
}

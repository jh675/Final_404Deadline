package com.example.demo.management.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.demo.management.mapper.ProjectMapper;
import com.example.demo.management.service.ProjectService;
import com.example.demo.management.service.ProjectVO;
import com.example.demo.login.service.UserVO;

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
}

package com.example.demo.management.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.management.service.ProjectVO;
import com.example.demo.login.service.UserVO;

@Mapper
public interface ProjectMapper {
	List<ProjectVO> listProject(ProjectVO vo );
	int projectInsert(ProjectVO vo);
} 

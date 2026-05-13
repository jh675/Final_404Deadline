package com.example.demo.management.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.management.service.ProjectVO;

@Mapper
public interface ProjectMapper {
	int projectInsert(ProjectVO vo);
//	List<ProjectVO> selectProject(ProjectVO vo);
}

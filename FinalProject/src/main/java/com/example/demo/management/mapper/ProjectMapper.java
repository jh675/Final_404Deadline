package com.example.demo.management.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.login.service.UserVO;
import com.example.demo.management.service.ModulesVO;
import com.example.demo.management.service.ProjectVO;

@Mapper
public interface ProjectMapper {
	List<ProjectVO> listProject(ProjectVO vo );
	int projectInsert(ProjectVO vo);
	List<UserVO> searchUsersByBizNo(@Param("bizNo") String bizNo, @Param("term") String term);
	void moduleInsert(ModulesVO mVo);
} 

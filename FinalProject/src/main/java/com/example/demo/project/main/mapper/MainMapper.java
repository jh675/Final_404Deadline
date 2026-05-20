package com.example.demo.project.main.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.project.main.service.IssueCountVO;

@Mapper
public interface MainMapper {
	IssueCountVO issueCount(IssueCountVO vo);
}

package com.example.demo.project.main.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.project.main.service.MainIssueVO;
import com.example.demo.project.main.service.MainVO;

@Mapper
public interface MainMapper {

	int issueBugFinishCount(MainIssueVO vo);
	int issueBugOngoingCount(MainIssueVO vo);
	int issueFunctionFinishCount(MainIssueVO vo);
	int issueFunctionOngoingCount(MainIssueVO vo);
	int issueWorkFinishCount(MainIssueVO vo);
	int issueWorkOngoingCount(MainIssueVO vo);
	int issueImpFinishCount(MainIssueVO vo);
	int issueImpOngoingCount(MainIssueVO vo);
}

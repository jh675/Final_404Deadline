package com.example.demo.project.issue.service;

import java.util.List;

import com.example.demo.project.issue.vo.IssueInputVO;
import com.example.demo.project.issue.vo.IssueOutputVO;

public interface IssueService {
    List<IssueOutputVO> selectIssueList(IssueInputVO issueVO);

    IssueOutputVO selectIssue(Long id);

    int insertIssue(IssueInputVO issueVO);

    int updateIssue(IssueInputVO issueVO);

    int deleteIssue(Long id);
}

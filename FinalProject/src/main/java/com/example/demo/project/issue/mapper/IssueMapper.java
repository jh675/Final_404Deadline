package com.example.demo.project.issue.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.project.issue.vo.IssueInputVO;
import com.example.demo.project.issue.vo.IssueOutputVO;

@Mapper
public interface IssueMapper {

    List<IssueOutputVO> selectIssueList(IssueInputVO issueVO);

    IssueOutputVO selectIssue(Long id);

    int insertIssue(IssueInputVO issueVO);

    int updateIssue(IssueInputVO issueVO);

    int deleteIssue(Long id);
}

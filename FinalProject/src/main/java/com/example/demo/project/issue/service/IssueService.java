package com.example.demo.project.issue.service;

import java.util.List;
import java.util.Map;



public interface IssueService {
    List<IssueOutputVO> selectIssueList(IssueInputVO issueVO);

    IssueOutputVO selectIssue(Long id);

    IssueInputVO selectIssueForForm(Long id);

    /** 등록된 이슈 PK를 반환합니다. */
    Long insertIssue(IssueInputVO issueVO);

    int updateIssue(IssueInputVO issueVO);

    int deleteIssue(Long id);

    List<IssueOutputVO> selectChildIssueList(Long id);

    long countChildIssues(Long id);
    
    Map<Long, String> getParentIssue(Long id);
    
    List<CommentOutputVO> getComment(Long id);

    int insertComment(CommentInputVO vo);
    
    List<Map<Long, String>> getIssueIds();
}

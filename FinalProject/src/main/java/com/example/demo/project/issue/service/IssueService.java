package com.example.demo.project.issue.service;

import java.util.List;

public interface IssueService {
    List<IssueOutputVO> selectIssueList(IssueInputVO issueVO);

    IssueOutputVO selectIssue(Long id);

    IssueInputVO selectIssueForForm(Long id);

    /** 등록된 이슈 PK를 반환합니다. */
    Long insertIssue(IssueInputVO issueVO);

    int updateIssue(IssueInputVO issueVO);

    int updateIssueStartDate(Long id);

    int updateIssueClosedDate(Long id);

    int deleteIssue(Long id);

    List<IssueOutputVO> selectChildIssueList(Long id);

    long countChildIssues(Long id);
    
    IssueSummaryVO getParentIssue(Long id);
    
    List<CommentOutputVO> getComment(Long id);

    int insertComment(CommentInputVO vo);
    
	List<IssueSummaryVO> getIssueIds(Long prjId, Long issueId);
}

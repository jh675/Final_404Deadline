package com.example.demo.project.issue.service;

import java.util.List;
import java.util.Map;

import com.example.demo.project.history.service.HistoryVO;

public interface IssueService {
    List<IssueOutputVO> selectIssueList(IssueInputVO issueVO);

    IssueOutputVO selectIssue(Long id);

    IssueInputVO selectIssueForForm(Long id);

    /** 등록된 이슈 PK를 반환합니다. */
    Long insertIssue(IssueInputVO issueVO);

    Long updateIssue(IssueInputVO issueVO);

    /** 타임라인이 있어 마일스톤 해제(없음)가 불가한 이슈인지 */
    boolean isMilestoneUnlinkBlocked(Long issueId);

    List<IssueOutputVO> selectChildIssueList(Long id);

    long countChildIssues(Long id);
    
    IssueSummaryVO getParentIssue(Long id);
    
    List<CommentOutputVO> getComment(Long id);

    Long insertComment(CommentInputVO vo);
    
	List<IssueSummaryVO> getIssueIds(Long prjId, Long issueId);
	List<IssueOutputVO> getRelationIssue(Long id);
	List<HistoryVO> getHistory(Long id);
    List<Map<String, Object>> getPivotStatus(Long prjId);
    List<Map<String, Object>> getPivotPriority(Long prjId);
    List<Map<String, Object>> getPivotCategory(Long prjId);

    Long registerStartDate(IssueVulkVO vulkVO);
    Long registerClosedDate(IssueVulkVO vulkVO);
    Long updateVulk(IssueVulkVO vulkVO);

    List<IssueOutputVO> searchIssuesForLink(Long prjId, String q);
}

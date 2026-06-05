package com.example.demo.project.issue.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.alarm.service.AlarmIssueVO;
import com.example.demo.project.history.service.HistoryVO;
import com.example.demo.project.issue.service.CommentInputVO;
import com.example.demo.project.issue.service.CommentOutputVO;
import com.example.demo.project.issue.service.IssueInputVO;
import com.example.demo.project.issue.service.IssueOutputVO;
import com.example.demo.project.issue.service.IssueSummaryVO;
import com.example.demo.project.issue.service.IssueVulkVO;

@Mapper
public interface IssueMapper {

	//전체조회
    List<IssueOutputVO> selectIssueList(IssueInputVO issueVO);
    //단건조회
    IssueOutputVO selectIssue(Long id);
    /** 상세 표시용이 아닌 등록/수정 폼용(코드·MEM_ID 원본) */
    IssueInputVO selectIssueForForm(Long id);
    //입력
    Long insertIssue(IssueInputVO issueVO);
    //수정
    Long updateIssue(IssueInputVO issueVO);


    //하위 이슈 조회
    List<IssueOutputVO> selectChildIssueList(Long id);
    //하위 이슈 갯수
    Long countChildIssues(Long id);
    
    IssueSummaryVO getParentIssue(Long id);
    List<CommentOutputVO> getComment(Long id);

    Long insertComment(CommentInputVO vo);
	List<IssueSummaryVO> getIssueIds(@Param("prjId") Long prjId, @Param("issueId") Long issueId);
	List<IssueOutputVO> getRelationIssue(Long id);
	List<HistoryVO> getHistory(Long id);
    List<Map<String, Object>> getPivotStatus(@Param("prjId") Long prjId);
    List<Map<String, Object>> getPivotPriority(@Param("prjId") Long prjId);
    List<Map<String, Object>> getPivotCategory(@Param("prjId") Long prjId);

    Long registerStartDate(IssueVulkVO vulkVO);
    Long registerClosedDate(IssueVulkVO vulkVO);
    Long updateVulk(IssueVulkVO vulkVO);

    
 // 새 이슈 감지용
    List<AlarmIssueVO> findByCreatedOnAfter(Date date);

    // 이슈 상태 변경 감지용
    List<AlarmIssueVO> findByUpdatedOnAfter(Date date);

    List<IssueOutputVO> searchIssuesForLink(@Param("prjId") Long prjId, @Param("q") String q);
}

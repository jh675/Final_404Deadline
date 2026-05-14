package com.example.demo.project.issue.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.project.issue.service.CommentInputVO;
import com.example.demo.project.issue.service.CommentOutputVO;
import com.example.demo.project.issue.service.IssueInputVO;
import com.example.demo.project.issue.service.IssueOutputVO;

@Mapper
public interface IssueMapper {

	//전체조회
    List<IssueOutputVO> selectIssueList(IssueInputVO issueVO);
    //단건조회
    IssueOutputVO selectIssue(Long id);
    //입력
    int insertIssue(IssueInputVO issueVO);
    //수정
    int updateIssue(IssueInputVO issueVO);
    //삭제
    int deleteIssue(Long id);
    //하위 이슈 조회
    List<IssueOutputVO> selectChildIssueList(Long id);
    //하위 이슈 갯수
    long countChildIssues(Long id);
    
    Map<Long, String> getParentIssue(Long id);
    List<CommentOutputVO> getComment(Long id);

    int insertComment(CommentInputVO vo);
    List<Map<Long, String>> getIssueIds();
}

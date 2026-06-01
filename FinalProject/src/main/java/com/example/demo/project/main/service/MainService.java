package com.example.demo.project.main.service;

import java.util.List;

import com.example.demo.project.group.service.GroupDetailVO;
import com.example.demo.project.notice.service.NoticeVO;

public interface MainService {
	IssueCountVO issueCount(IssueCountVO vo);
	List<NoticeVO> selectNotice(NoticeVO nvo);
	List<GroupDetailVO> selectGroupMemberCount(GroupDetailVO gmvo);
}

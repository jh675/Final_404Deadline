package com.example.demo.project.main.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.management.service.ProjectVO;
import com.example.demo.project.calender.service.CalenderVO;
import com.example.demo.project.group.service.GroupDetailVO;
import com.example.demo.project.main.service.IssueCountVO;
import com.example.demo.project.notice.service.NoticeVO;

@Mapper
public interface MainMapper {
	IssueCountVO issueCount(IssueCountVO vo);
	List<CalenderVO> selectCalender(ProjectVO vo);
	List<NoticeVO> selectNotice(NoticeVO nvo);
	List<GroupDetailVO> selectgroup(GroupDetailVO gmvo);
	
}

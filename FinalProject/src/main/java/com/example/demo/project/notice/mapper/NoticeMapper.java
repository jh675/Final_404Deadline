package com.example.demo.project.notice.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

import com.example.demo.project.notice.service.NoticeVO;

@Mapper
public interface NoticeMapper {
	
	List<NoticeVO> selectAll(NoticeVO notice);
    NoticeVO selectOne(int id);
    int delete(int id);
    int update(NoticeVO notice);
    int insert(NoticeVO notice);
}

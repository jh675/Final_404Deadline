package com.example.demo.project.calender.service.impl;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example.demo.alarm.event.NotificationEvent;
import com.example.demo.project.calender.mapper.CalenderMapper;
import com.example.demo.project.calender.service.CalenderService;
import com.example.demo.project.calender.service.CalenderVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j

public class CalenderServiceImpl implements CalenderService{
  private final CalenderMapper calenderMapper;
  private final ApplicationEventPublisher eventPublisher;
  
  @Override
  public List<CalenderVO> selectAll(CalenderVO vo) {
	  return calenderMapper.selectAll(vo);
  }
  
  @Override
  public List<CalenderVO>getList(CalenderVO vo) {
	  return calenderMapper.getList(vo);
  }
  
  @Override
  public CalenderVO selectOne(int id) {
	  return calenderMapper.selectOne(id);
  }
  
  @Override
  public int insert(CalenderVO vo) {
	  
	  
	    if ("03CALTYPE".equals(vo.getTypeCd())) {
	        System.out.println("=== 회사일정 감지!");

	        String bizNo = calenderMapper.findBizNoByMemId(vo.getMemId().longValue());
	       
	       
	        if (bizNo != null) {
	            List<String> usernames = calenderMapper.findUsernamesByBizNo(bizNo);
	            System.out.println("=== 대상 usernames: " + usernames);

	            usernames.forEach(username -> {
	                System.out.println("=== 알림 전송 → " + username);
	                eventPublisher.publishEvent(new NotificationEvent(
	                    this,
	                    "회사 일정이 등록되었습니다: " + vo.getCalText(),
	                    username
	                ));
	            });
	        }
	    }
	  return calenderMapper.insert(vo);
  }
  
  @Override
  public int update(CalenderVO vo) {
	  return calenderMapper.update(vo);
  }
  
  @Override
  public int delete(int id) {
	  return calenderMapper.delete(id);
  }
  
}

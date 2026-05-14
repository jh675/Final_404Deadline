package com.example.demo.project.calender.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

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
  
  @Override
  public List<CalenderVO> selectAll(CalenderVO vo) {
	  return calenderMapper.selectAll(vo);
  }
  
  @Override
  public CalenderVO selectOne(int id) {
	  return calenderMapper.selectOne(id);
  }
  
  @Override
  public int insert(CalenderVO vo) {
	  return calenderMapper.insert(vo);
  }
  
  @Override
  public int updete(CalenderVO vo) {
	  return calenderMapper.updete(vo);
  }
  
  @Override
  public int delete(int id) {
	  return calenderMapper.delete(id);
  }
  
}

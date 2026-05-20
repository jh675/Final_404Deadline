package com.example.demo.project.main.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.project.main.mapper.MainMapper;
import com.example.demo.project.main.service.IssueCountVO;
import com.example.demo.project.main.service.MainService;




@Service
public class MainServiceImpl implements MainService {
	
	@Autowired
	MainMapper mainMapper;
	
	@Override
	public IssueCountVO issueCount(IssueCountVO vo) {
		return mainMapper.issueCount(vo);
	}
	
	
}

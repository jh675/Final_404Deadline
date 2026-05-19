package com.example.demo.company.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.company.mapper.CompanyMapper;
import com.example.demo.company.service.CompanyService;
import com.example.demo.company.service.CompanyVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CompanyServiceImpl implements CompanyService {

	private final CompanyMapper companyMapper;
	
	@Override
	public List<CompanyVO> selectAll(CompanyVO company) {
		
		return companyMapper.selectAll(company);
	}

	@Override
	public CompanyVO selectOne(String bizNo) {
		
		return companyMapper.selectOne(bizNo);
	}

	@Override
	public int delete(String bizNo) {
		
		return companyMapper.delete(bizNo);
	}

	@Override
	public int update(CompanyVO company) {
		
		return companyMapper.update(company);
	}

	@Override
	public int insert(CompanyVO company) {
		
		return companyMapper.insert(company);
	}

}

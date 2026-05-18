package com.example.demo.company.service.impl;

import org.springframework.stereotype.Service;

import com.example.demo.company.mapper.CompanyMapper;
import com.example.demo.company.service.CompanyService;
import com.example.demo.company.service.CompanyVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CompanyServiceImpl implements CompanyService {

	private final CompanyMapper companyMapper;
	
	@Override
	public PageInfo<CompanyVO> selectAll(CompanyVO company, int pageNum) {
		
		PageInfo<CompanyVO> page = PageHelper.startPage(pageNum, 5)
				.doSelectPageInfo(()->companyMapper.selectAll(company));
				
		return page;
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

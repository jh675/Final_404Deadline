package com.example.demo.company.service;

import java.util.List;

import com.github.pagehelper.PageInfo;

public interface CompanyService {
	PageInfo<CompanyVO> selectAll(CompanyVO company, int pageNum);
	CompanyVO selectOne(String bizNo);
	int delete(String bizNo);
	int update(CompanyVO company);
	int insert(CompanyVO company);
}

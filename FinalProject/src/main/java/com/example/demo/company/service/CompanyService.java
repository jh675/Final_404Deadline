package com.example.demo.company.service;

import java.util.List;

import com.github.pagehelper.PageInfo;

public interface CompanyService {
	int selectAll_COUNT(CompanyVO company);
	PageInfo<CompanyVO> selectAll(CompanyVO company, int pageNum);
	CompanyVO selectOne(String bizNo);
	int delete(String bizNo);
	int update(CompanyVO company);
	int insert(CompanyVO company);
}

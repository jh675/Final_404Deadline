package com.example.demo.company.service;

import java.util.List;

public interface CompanyService {
	List<CompanyVO> selectAll(CompanyVO company);
	CompanyVO selectOne(String bizNo);
	int delete(String bizNo);
	int update(CompanyVO company);
	int insert(CompanyVO company);
}

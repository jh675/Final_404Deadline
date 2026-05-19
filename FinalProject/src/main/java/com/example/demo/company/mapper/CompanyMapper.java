package com.example.demo.company.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.company.service.CompanyVO;

@Mapper

public interface CompanyMapper {
	List<CompanyVO> selectAll(CompanyVO company);
	CompanyVO selectOne(String bizNo);
	int delete(String bizNo);
	int update(CompanyVO company);
	int insert(CompanyVO company);

}

package com.example.demo.util.subCode.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.util.subCode.mapper.SubcodeMapper;
import com.example.demo.util.subCode.service.SubcodeService;
import com.example.demo.util.subCode.service.SubcodeVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubcodeServiceImpl implements SubcodeService {

	final private SubcodeMapper subCodeMapper;
	
	@Override
	public List<SubcodeVO> getSubCodeList(String mCode) {
		return subCodeMapper.getSubCodeList(mCode);
	}
	
	@Override
	public String selectScodeNm(String sCode) {
		return subCodeMapper.selectScodeNm(sCode);
	}
}

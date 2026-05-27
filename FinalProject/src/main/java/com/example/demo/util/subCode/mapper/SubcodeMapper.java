package com.example.demo.util.subCode.mapper;

import java.util.List;

import com.example.demo.util.subCode.service.SubcodeVO;

public interface SubcodeMapper {
	List<SubcodeVO> getSubCodeList(String mainCode);
}

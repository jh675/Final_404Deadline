package com.example.demo.util.subCode.service;

import java.util.List;

public interface SubcodeService {

	List<SubcodeVO> getSubCodeList(String mCode);
	String selectScodeNm(String sCode);
}

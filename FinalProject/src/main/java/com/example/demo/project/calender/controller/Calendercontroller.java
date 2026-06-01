package com.example.demo.project.calender.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.login.service.UserVO;
import com.example.demo.project.calender.service.CalenderService;
import com.example.demo.project.calender.service.CalenderVO;

@Controller
public class Calendercontroller {

	@Autowired
	CalenderService calenderService;

	// 로그인한 유저 정보 가져오기
	private UserVO getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return (UserVO) auth.getPrincipal();
	}

	// 캘린더 페이지접속
	@GetMapping({ "/calendar/list" })
	public String callenderlist() {
		return "project/calender/calender";
	}

	// 일정목록 가져오기
	@GetMapping("/calendar/listJson")
	@ResponseBody
	public List<CalenderVO> calenderListJson(@RequestParam(name = "typeCd", defaultValue = "") String typeCd) {
		CalenderVO vo = new CalenderVO();
		vo.setTypeCd(typeCd);
		vo.setMemId(getLoginUser().getId().intValue());
		return calenderService.getList(vo);
	}

	// 일정 등록
	@PostMapping("/calendar/insert")
	@ResponseBody
	public int post(CalenderVO vo) {
		vo.setMemId(getLoginUser().getId().intValue());
		return calenderService.insert(vo);
	}

	// 일정 수정
	@PutMapping("/calendar/update")
	@ResponseBody
	public int update(CalenderVO vo) {
		return calenderService.update(vo);
	}

	// 일정 수정에서 가져온 데이터 단건 조회 API
	@GetMapping("/calendar/detail")
	@ResponseBody
	public CalenderVO getEventDetail(@RequestParam("id") int id) {
		return calenderService.selectOne(id);
	}

	// 일정 삭제
	@DeleteMapping("/calendar/delete")
	@ResponseBody
	public int delete(@RequestParam(name = "id") int id) {
		int memId = getLoginUser().getId().intValue();
		return calenderService.delete(id, memId);
	}

	// 일정 검색
	@GetMapping("/calendar/search")
	@ResponseBody
	public List<CalenderVO> search(CalenderVO vo) {
		vo.setMemId(getLoginUser().getId().intValue());
		return calenderService.selectAll(vo);
	}

}

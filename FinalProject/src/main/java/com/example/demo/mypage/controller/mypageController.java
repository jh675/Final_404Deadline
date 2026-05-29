package com.example.demo.mypage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.login.service.UserVO;
import com.example.demo.management.service.ProjectService;
import com.example.demo.management.service.ProjectVO;
import com.example.demo.management.userManage.service.UserManageService;
import com.example.demo.management.userManage.service.UserManageVO;
import com.example.demo.mypage.service.MypageService;
import com.example.demo.project.calender.service.CalenderService;
import com.example.demo.project.calender.service.CalenderVO;
import com.example.demo.project.calender.service.HolidayService;
import com.example.demo.project.calender.service.HolidayVO;
import com.example.demo.util.attach.service.AttachService;
import com.example.demo.util.attach.service.AttachVO;


@Controller
public class mypageController {
	
	@Autowired
	ProjectService projectService; 
	
	@Autowired
	MypageService mypageService;
	
	@Autowired
	CalenderService calenderService;
	
	@Autowired
	HolidayService holidayService;
	
	// 프로필 이미지 처리를 위한 서비스 추가
	@Autowired
	AttachService attachService;
	
	// 비밀번호 암호화 및 검증을 위한 인코더 추가
	@Autowired
	PasswordEncoder passwordEncoder;
	
	// 유저 정보 업데이트를 위한 서비스 추가
	@Autowired
	UserManageService userManageService;

	// 로그인한 유저 정보 가져오기
	private UserVO getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return (UserVO) auth.getPrincipal();
	}
	
	// 마이페이지 접속 	
	@GetMapping("/mypage")
	public String mypage(Model model) {
		UserVO loginUser = getLoginUser();
		
		// 프로젝트 목록 
		ProjectVO vo = new ProjectVO();
		vo.setUserId(loginUser.getId());
		List<ProjectVO> list = projectService.userProjectList(vo);
		model.addAttribute("projectList", list);
		
		
		// 캘린더 목록 
		CalenderVO calvo = new CalenderVO();
		calvo.setMemId(loginUser.getId().intValue());
		List<CalenderVO> callist = calenderService.selectAll(calvo);
		model.addAttribute("calenderList", callist);
		
		// 공휴일 목록 
		int year = java.time.LocalDate.now().getYear();
		List<HolidayVO> holiday = holidayService.getHolidays(year);
		model.addAttribute("holidayList", holiday);

		return "mypage/myPage";
	}
	// 마이페이지 내 정보 수정 (사용 안함)
	@PostMapping("/mypage/update")
	@ResponseBody
	public String updateInfo(@RequestBody UserVO update) {
		UserVO loginUser = getLoginUser();
		update.setId(loginUser.getId());
		
		int result = mypageService.updateUser(update);
		return result > 0 ? "success" : "fail";
	}

	// 마이페이지 프로젝트 페이지 접속
	@GetMapping("/mypage/project")
	public String projectlist(

			@RequestParam(name = "prjName", required = false) String prjName,
			@RequestParam(name = "prjStatusCd", required = false) String prjStatusCd, Model model) {

		UserVO loginUser = getLoginUser();

		ProjectVO vo = new ProjectVO();
		vo.setUserId(loginUser.getId());
		vo.setPrjName(prjName);
		vo.setPrjStatusCd(prjStatusCd);

		List<ProjectVO> list = projectService.userProjectList(vo);

		model.addAttribute("list", list);
		model.addAttribute("prjName", prjName);
		model.addAttribute("prjStatusCd", prjStatusCd);

		return "mypage/mypageProject";
	}
	
	// 마이페이지 내 정보 수정 
	@PutMapping("/mypage/update")
	@ResponseBody
	public Map<String, String> updateInfo(@RequestBody Map<String, String> payload) {
		Map<String, String> result = new HashMap<>();
		UserVO loginUser = getLoginUser(); // 세션의 유저 정보

		// 1. 현재 비밀번호 검증
		if (payload.containsKey("currentPassword")) {
			String currentPassword = payload.get("currentPassword");
			if (!passwordEncoder.matches(currentPassword, loginUser.getPassword())) {
				result.put("status", "PWD_ERROR");
				return result;
			}
		}

		// UserManageVO에 데이터 셋팅
		UserManageVO updateVo = new UserManageVO();
		updateVo.setId(loginUser.getId());
		updateVo.setBizNo(loginUser.getBizNo()); // 아이디 중복 체크용
		updateVo.setLogin(payload.get("login"));
		updateVo.setName(payload.get("name"));
		updateVo.setEmail(payload.get("email"));
		updateVo.setTel(payload.get("tel"));

		// 새 비밀번호 암호화
		if (payload.containsKey("newPassword") && !payload.get("newPassword").isEmpty()) {
			updateVo.setPassword(passwordEncoder.encode(payload.get("newPassword")));
		}

		// UserManageService 호출
		String updateStatus = userManageService.updateMyInfo(updateVo);
		result.put("status", updateStatus); // SUCCESS, DUPLICATE_LOGIN, FAIL 반환

		// 성공 시 세션 정보 갱신 
		if ("SUCCESS".equals(updateStatus)) {
			loginUser.setLogin(updateVo.getLogin());
			loginUser.setName(updateVo.getName());
			loginUser.setEmail(updateVo.getEmail());
			loginUser.setTel(updateVo.getTel());
			if (updateVo.getPassword() != null) {
				loginUser.setPassword(updateVo.getPassword());
			}
		}

		return result;
	}

	// 마이페이지 전용 프로필 이미지 API 추가

	// 프로필 이미지 조회
	@GetMapping("/mypage/profile")
	@ResponseBody
	public ResponseEntity<AttachVO> getMyProfileImage() {
		UserVO loginUser = getLoginUser(); // 안전하게 세션에서 본인 ID 꺼내기
		List<AttachVO> list = attachService.selectAttachList("09MODULE", loginUser.getId());
		
		if (list != null && !list.isEmpty()) {
			return ResponseEntity.ok(list.get(list.size() - 1));
		}
		return ResponseEntity.ok().build();
	}

	// 프로필 이미지 업로드
	@PostMapping("/mypage/profile")
	@ResponseBody
	public ResponseEntity<?> uploadMyProfileImage(@RequestParam("file") MultipartFile file) {
		UserVO loginUser = getLoginUser();
		Long userId = loginUser.getId();

		// 기존 프로필 삭제
		List<AttachVO> existList = attachService.selectAttachList("09MODULE", userId);
		if (existList != null) {
			for (AttachVO attach : existList) {
				try {
					attachService.removeAttach(attach); // 물리 파일 삭제
					attachService.deleteAttach(attach.getId()); // DB 삭제
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// 새 프로필 저장
		attachService.saveAndInsertAttachments(userId, new MultipartFile[]{file}, "09MODULE", "users");
		
		return ResponseEntity.ok().body("SUCCESS");
	}

	// 프로필 이미지 삭제
	@DeleteMapping("/mypage/profile")
	@ResponseBody
	public ResponseEntity<?> deleteMyProfileImage() {
		UserVO loginUser = getLoginUser();
		Long userId = loginUser.getId();

		List<AttachVO> existList = attachService.selectAttachList("09MODULE", userId);
		if (existList != null) {
			for (AttachVO attach : existList) {
				try {
					attachService.removeAttach(attach);
					attachService.deleteAttach(attach.getId());
				} catch (Exception e) {
					e.printStackTrace();
					return ResponseEntity.internalServerError().build();
				}
			}
		}
		return ResponseEntity.ok().body("SUCCESS");
	}

}

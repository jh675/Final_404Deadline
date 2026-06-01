package com.example.demo.project.docs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.login.service.UserVO;
import com.example.demo.project.docs.service.DocsService;
import com.example.demo.project.docs.service.DocsVO;
import com.example.demo.util.attach.service.AttachService;
import com.example.demo.util.attach.service.AttachVO;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/project")
@Controller
public class DocsController {

	@Autowired
	DocsService docsService;

	@Autowired
	AttachService attactchservice;

	@GetMapping("/docs/list")
	public String noticeList(HttpSession session, Model model, DocsVO doc) {
		session.setAttribute("currentMenu", "docs");
		Long projectId = (Long) session.getAttribute("currentProjectId");

		doc.setPrjId(projectId);
		model.addAttribute("list", docsService.selectAll(doc));
		return "project/docs/docList";
	}

	@GetMapping("/docs/form")
	public String form(@RequestParam("id") Long id, Model model) {

		DocsVO docs = docsService.selectOne(id);

		model.addAttribute("docs", docs);

		List<AttachVO> files = attactchservice.selectAttachList("07MODULE", id);

		model.addAttribute("files", files);

		return "project/docs/docForm";
	}

	@GetMapping("/docs/register")
	public String registerForm(Model model) {

		model.addAttribute("docs", new DocsVO());

		return "project/docs/docForm";
	}

	@PostMapping("/docs/register")
	public String register(DocsVO vo, HttpSession session, @RequestParam("attachments") MultipartFile[] attachments,
			RedirectAttributes rttr, 
			Authentication authentication) { // ⭕ 스프링 시큐리티가 현재 로그인 유저 객체를 주입합니다.

		// 1. 프로젝트 ID 세션 추출
		Long projectId = (Long) session.getAttribute("currentProjectId");

		// 2. ⭕ [에러 해결] UserVO의 실제 고유 번호 필드인 'getId()'를 사용하여 회원 번호를 추출합니다.
		Long sessionMemId = null;
		if (authentication != null && authentication.getPrincipal() instanceof UserVO) {
			UserVO userVO = (UserVO) authentication.getPrincipal();
			sessionMemId = userVO.getId(); // 👈 getMemId() 대신 진짜 필드인 getId()를 호출합니다!
		}

		// 3. [안전장치] 시큐리티 정보가 없다면 차단
		if (sessionMemId == null) {
			rttr.addFlashAttribute("msg", "로그인 세션이 만료되었습니다. 다시 로그인해 주세요.");
			return "redirect:/project/docs/list";
		}

		// 4. 데이터를 문서 VO에 세팅 (vo.setMemId는 DB 저장용 필드명이므로 그대로 유지)
		vo.setPrjId(projectId);
		vo.setMemId(sessionMemId);

		// 5. 저장 로직 수행
		docsService.insert(vo);
		attactchservice.saveAndInsertAttachments(vo.getId(), attachments, "07MODULE", "DOCUMENT");

		rttr.addFlashAttribute("msg", "새로운 문서가 성공적으로 등록되었습니다.");

		return "redirect:/project/docs/list";
	}



	@PutMapping("/docs/update")
	public String update(DocsVO vo, HttpSession session, @RequestParam("attachments") MultipartFile[] attachments,
			RedirectAttributes rttr, // ⭕ 세션 만료 시 화면에 알림을 주기 위해 추가합니다.
			Authentication authentication) { // ⭕ 등록과 동일하게 시큐리티 인증 객체를 주입받습니다.

		// 1. 프로젝트 ID 세션 추출
		Long projectId = (Long) session.getAttribute("currentProjectId");

		// 2. [시큐리티 검증] 현재 수정 처리를 요청한 로그인 유저의 진짜 고유 번호(id)를 추출합니다.
		Long sessionMemId = null;
		if (authentication != null && authentication.getPrincipal() instanceof UserVO) {
			UserVO userVO = (UserVO) authentication.getPrincipal();
			sessionMemId = userVO.getId(); // 👈 완벽히 식별된 유저의 진짜 ID 번호
		}

		// 3. [안전장치] 만약 수정 도중 세션이 만료되었다면 수정을 차단하고 목록 화면으로 보냅니다.
		if (sessionMemId == null) {
			rttr.addFlashAttribute("msg", "로그인 세션이 만료되었습니다. 다시 로그인해 주세요.");
			return "redirect:/project/docs/list";
		}

		// 4. 추출한 데이터를 VO에 안전하게 채워줍니다.
		// (참고: 아까 MyBatis의 SET 절에서 prj_id와 mem_id 변경을 제외했으므로, 
		// 여기서 세팅된 값은 서버 내부 로그나 데이터 유효성 검증용으로 안전하게 활용됩니다.)
		vo.setPrjId(projectId);
		vo.setMemId(sessionMemId);

		// 5. 문서 수정 쿼리 실행 및 신규 첨부파일 저장
		docsService.update(vo);
		attactchservice.saveAndInsertAttachments(vo.getId(), attachments, "07MODULE", "DOCUMENT");

		rttr.addFlashAttribute("msg", "문서 내용이 성공적으로 수정되었습니다.");

		return "redirect:/project/docs/list";
	}


	@DeleteMapping("/docs/delete")
	public String delete(@RequestParam("id") Long id) {

		docsService.delete(id);

		return "redirect:/project/docs/list";
	}
	
	@GetMapping("/docs/files")
	@ResponseBody
	public List<AttachVO> getFiles(@RequestParam("id") Long id){

	    return attactchservice.selectAttachList("07MODULE", id);
	}
	
	@GetMapping("/attach/delete")
	@ResponseBody
	public String deleteAttach(@RequestParam("id") Long id) {

		try {

			AttachVO attachVO =
				attactchservice.selectAttach(id);

			attactchservice.deleteAttach(id);

			attactchservice.removeAttach(attachVO);

			return "success";

		} catch (Exception e) {

			e.printStackTrace();

			return "fail";
		}

		
	}
	
	@GetMapping("/docs/detail")
	@ResponseBody
	public DocsVO detail(@RequestParam("id") Long id) {
		return docsService.selectOne(id);
	}		
	

	

}

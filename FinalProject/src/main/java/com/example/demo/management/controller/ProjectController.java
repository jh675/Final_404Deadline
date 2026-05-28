package com.example.demo.management.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.company.service.CompanyService;
import com.example.demo.company.service.CompanyVO;
import com.example.demo.login.service.UserVO;
import com.example.demo.management.service.ModulesVO;
import com.example.demo.management.service.ProjectService;
import com.example.demo.management.service.ProjectVO;
import com.example.demo.project.calender.service.CalenderService;
import com.example.demo.project.calender.service.CalenderVO;
import com.example.demo.project.group.service.GroupDetailVO;
import com.example.demo.project.issue.service.IssueInputVO;
import com.example.demo.project.issue.service.IssueOutputVO;
import com.example.demo.project.issue.service.IssueService;
import com.example.demo.project.main.service.IssueCountVO;
import com.example.demo.project.main.service.MainService;
import com.example.demo.project.member.service.MemberDetailVO;
import com.example.demo.project.notice.service.NoticeService;
import com.example.demo.project.notice.service.NoticeVO;
import com.example.demo.project.option.service.RoleVO;
import com.example.demo.project.wiki.service.WikiVO;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProjectController {


	@Autowired
	ProjectService projectservice;
	@Autowired
    CompanyService companyService;
	@Autowired
	IssueService issueService;
	@Autowired
	MainService mainService;
	@Autowired
	CalenderService calenderService;
	@Autowired
	NoticeService noticeService;
	

	@GetMapping("management/project")
	public String listProject(ProjectVO vo, Model model, CompanyVO cvo, GroupDetailVO gvo,
			Authentication authentication) {

		List<ProjectVO> list;
		UserVO loginUser = null;
		if (authentication != null && authentication.getPrincipal() instanceof UserVO u) {
			loginUser = u;
		}

		boolean isEmployee = loginUser != null
				&& ("03ROLE".equals(loginUser.getAdminCd())
						|| "사원".equals(loginUser.getAdminNm())
						|| (loginUser.getRole() != null && loginUser.getRole().contains("ROLE_USER")));

		if (isEmployee) {
			vo.setUserId(loginUser.getId());
			list = projectservice.userProjectList(vo);
		} else {
			list = projectservice.listProject(vo);
		}
		
	    List<CompanyVO> companyList = companyService.selectAll(cvo);
	    
	    model.addAttribute("projectinfo", Map.of("list", list != null ? list : List.of()));
	    model.addAttribute("companyList", companyList != null ? companyList : List.of());
	    
	    return "management/projectlist";
	}

	
	@GetMapping("/management/projectcreate")
	public String projectCreate(Model model, HttpSession session,ProjectVO vo,
								@RequestParam(name = "copyFrom", required = false) Long copyFrom) {
		UserVO user = (UserVO) session.getAttribute("loginUser");
		if (user != null) {
	        // userId 필드가 String이라면 user.getUserId()를, 
	        // Integer라면 Integer.parseInt(user.getUserId()) 등을 사용하세요.
	        vo.setUserId(user.getId()); 
	        vo.setBizNo(user.getBizNo());
	    }
		List<ProjectVO> list = projectservice.listProject(null);
		model.addAttribute("projectList", list != null ? list : List.of());
		
		// 복사 기능 추가
	    if (copyFrom != null) {
	        ProjectVO copyProject = projectservice.getprojectid(copyFrom);
	        model.addAttribute("copyProject", copyProject);
	    }
	    
		return "management/projectcreate";
	}

	@GetMapping("/management/searchUsers")
	@ResponseBody
	public List<Map<String, Object>> searchUsers(@RequestParam("term") String term, Authentication authentication) {
	    
	    UserVO vo = (UserVO) authentication.getPrincipal();
	    List<UserVO> userList = projectservice.searchUsersByBizNo(vo.getBizNo(), term);

	    // 필요한 정보만 Map에 담아서 리스트로 반환 (UserVO 수정 불필요)
	    return userList.stream().map(user -> {
	        Map<String, Object> map = new HashMap<>();
	        map.put("id", user.getId());
	        map.put("name", user.getName());
	        map.put("login", user.getLogin());
	        map.put("email", user.getEmail());
	        return map;
	    }).collect(Collectors.toList());
	}
	
	@PostMapping("/management/projectcreate")
	public String projectInsert(ProjectVO vo, 
	                             @RequestParam(value="moduleList", required=false) List<String> moduleList, 
	                             Authentication authentication,GroupDetailVO gVo, MemberDetailVO mvo,WikiVO wVo,RoleVO rVo) {
	    
	    // 1. 인증 객체에서 로그인 유저 정보 가져오기 (가장 확실한 방법)
	    if (authentication != null && authentication.getPrincipal() instanceof UserVO loginUser) {
	        // 프로젝트를 생성하는 사람의 정보 세팅
	        vo.setUserId(loginUser.getId()); 
	        vo.setBizNo(loginUser.getBizNo());
	    }

	    // 2. 서비스 호출 (프로젝트 정보와 모듈 리스트를 함께 넘김)
	    // 기존의 projectservice.projectInsert(vo) 대신 새로운 메서드를 호출합니다.
	    projectservice.insertProjectWithModules(vo, moduleList, gVo, mvo, wVo, rVo);
	    
	    return "redirect:/management/project";
	}
	
	@GetMapping("/management/projectupdate")
	public String projectUpdate(@RequestParam("id") Long id, Model model, Authentication authentication) {

	    ProjectVO editProject = projectservice.getprojectid(id);

	    // 매니저 이름 조회
	    if (editProject.getUserId() != null) {
	        List<UserVO> userList = projectservice.searchUsersByBizNo(editProject.getBizNo(), "");
	        userList.stream()
	            .filter(u -> u.getId().equals(editProject.getUserId()))
	            .findFirst()
	            .ifPresent(u -> editProject.setManagerName(
	                u.getName() + "," + u.getLogin() + "," + u.getEmail()));
	    }

	    // 활성화된 모듈 조회 후 enaId에 세팅
	    List<ModulesVO> moduleList = projectservice.listModules(id);
	    if (moduleList != null && !moduleList.isEmpty()) {
	        String enaId = moduleList.stream()
	            .map(ModulesVO::getModuleCode)
	            .collect(Collectors.joining(","));
	        editProject.setEnaId(enaId);
	    }

	    List<ProjectVO> list = projectservice.listProject(null);
	    model.addAttribute("editProject", editProject);
	    model.addAttribute("projectList", list != null ? list : List.of());

	    return "management/projectupdate";
	}

	@PostMapping("/management/projectupdate")
	public String projectUpdatePost(ProjectVO vo,
	                                @RequestParam(value = "moduleList", required = false) List<String> moduleList,
	                                Authentication authentication) {
	    
	    if (authentication != null && authentication.getPrincipal() instanceof UserVO loginUser) {
	        vo.setBizNo(loginUser.getBizNo());
	    }
	    
	    projectservice.updateProject(vo, moduleList);
	    
	    return "redirect:/management/project";
	}
	
	
	@PostMapping("/management/hide")
	public String projectHide(ProjectVO vo ,RedirectAttributes rttr) {
		projectservice.projectHide(vo);
		rttr.addFlashAttribute("msg", "프로젝트가 성공적으로 삭제되었습니다.");
		return "redirect:/management/project";
	}
	
	@PostMapping("/management/delete")
	public String projectDelete(ProjectVO vo,RedirectAttributes rttr) {
		Long id = vo.getId();
		
		if (projectservice.hasChildProject(id)) {
	        rttr.addFlashAttribute("msg", "하위 프로젝트가 있어 삭제할 수 없습니다.");
	        return "redirect:/management/project";
	    }

	    projectservice.projectDelete(vo);
	    rttr.addFlashAttribute("msg", "프로젝트가 삭제되었습니다.");
	    return "redirect:/management/project";
	}
	
	//대시보드
	@GetMapping("/project/main")
	public String goMain( ProjectVO vo , Model model, 
			             IssueInputVO ivo, 
			             IssueCountVO icvo,
			             CalenderVO Cvo,
			             NoticeVO nvo,
			             GroupDetailVO gmvo,
			             HttpSession session) {
		Long projectid = (Long) session.getAttribute("currentProjectId");
		vo.setId(projectid);
		icvo.setPrjId(projectid);
		nvo.setPrjId(projectid);
		gmvo.setPrjId(projectid);
		List<IssueOutputVO> issuelist = issueService.selectIssueList(ivo);
		IssueCountVO count = mainService.issueCount(icvo);
		List<CalenderVO> Clist = mainService.selectCalender(vo);
		List<NoticeVO> Nlist = mainService.selectNotice(nvo);
		List<GroupDetailVO> Glist = mainService.selectGroupMemberCount(gmvo);
		if (count == null) {
	        count = new IssueCountVO();
	    }
		session.setAttribute("currentMenu", "dashboard");
		model.addAttribute("project", vo);
		model.addAttribute("issuelist",issuelist);
		model.addAttribute("count",count);
		model.addAttribute("calender",Clist);
		model.addAttribute("notice",Nlist);
		model.addAttribute("selectgroup",Glist);
		model.addAttribute("moduleList",projectservice.listModules(projectid));
		return "project/main/main";
	}
	
	@GetMapping("/project/{id}") 
    public String enterProject(@PathVariable("id") Long id, HttpSession session) {
        
		session.setAttribute("currentMenu", "dashboard");
        // 클릭한 프로젝트의 ID를 세션에 "currentProjectId"라는 이름으로 저장
        session.setAttribute("currentProjectId", id); //대소문자 주의
        session.setAttribute("moduleList",projectservice.listModules(id));
        session.setAttribute("project",projectservice.getprojectid(id));
        // 세션에 저장했으니, 해당 프로젝트의 개요화면으로 이동시킵니다.
        return "redirect:/project/main"; //(예시 url)
    }

}

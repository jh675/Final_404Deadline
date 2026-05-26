package com.example.demo.setting;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult; // ★ 추가됨
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.example.demo.login.service.UserVO;
import com.example.demo.project.option.mapper.RoleMapper;
import com.example.demo.project.option.service.RoleVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component("prjAuth")
@RequiredArgsConstructor
public class ProjectAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final RoleMapper roleMapper;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final HttpServletRequest request;
    
    @Override
    public AuthorizationResult authorize(Supplier<? extends Authentication> authentication, RequestAuthorizationContext context) {
        
        Authentication auth = authentication.get();
        HttpServletRequest request = context.getRequest();

        // 1. 로그인 여부 확인
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return new AuthorizationDecision(false);
        }
        
        UserVO user = (UserVO) auth.getPrincipal();

        // 2. 시스템/기업 관리자는 모든 권한 프리패스
        if (user.getRole().contains("ROLE_ADMIN") || user.getRole().contains("ROLE_CADMIN")) {
            return new AuthorizationDecision(true);
        }

        // 3. 현재 접속 중인 프로젝트 ID 확인 (세션)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentProjectId") == null) {
            return new AuthorizationDecision(false); 
        }
        Long prjId = (Long) session.getAttribute("currentProjectId");

        // 4. DB에서 해당 유저가 접근 가능한 URL/Method 리스트 가져오기
        List<RoleVO> allowedMenus = roleMapper.selectUserAllowedMenus(user.getId(), prjId);

        // 5. 사용자가 요청한 URL 및 Method 확인
        String requestUrl = request.getRequestURI();    
        String requestMethod = request.getMethod();     

        // 6. DB 권한과 요청 비교 
        for (RoleVO menu : allowedMenus) {
            // Method가 같고, URL 패턴이 매칭된다면 통과!
            if (requestMethod.equalsIgnoreCase(menu.getRoleMth()) && 
                antPathMatcher.match(menu.getRoleUrl(), requestUrl)) {
                return new AuthorizationDecision(true);
            }
        }

        // 매칭되는 권한이 하나도 없으면 차단 
        return new AuthorizationDecision(false);
    }
    
    // [2] 화면 단 버튼/메뉴 노출 제어 (Thymeleaf에서 직접 호출함)
    public boolean hasRole(String menuRoleId) {
        
        // 현재 인증 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return false;
        }
        
        UserVO user = (UserVO) auth.getPrincipal();

        // 관리자는 모든 버튼 노출
        if (user.getRole().contains("ROLE_ADMIN") || user.getRole().contains("ROLE_CADMIN")) {
            return true;
        }

        // 현재 프로젝트 ID 확인
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentProjectId") == null) {
            return false;
        }
        Long prjId = (Long) session.getAttribute("currentProjectId");

        // DB에서 해당 유저가 가진 메뉴 권한(ROLE_ID) 리스트만 문자열로 싹 가져오기
        List<String> myProjectRoles = roleMapper.selectUserProjectRoles(user.getId(), prjId);

        // 요청한 버튼 권한(예: 'ROLE_ISSUE_CREATE')을 가지고 있는지 확인 후 true/false 리턴
        return myProjectRoles != null && myProjectRoles.contains(menuRoleId);
    }
}
package com.example.demo.setting;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class ProjectLayoutInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                           Object handler, ModelAndView modelAndView) throws Exception {
        
        // redirect로 페이지를 이동시킬 때나, 뷰(HTML)가 없을 때는 동작하지 않음
        if (modelAndView == null || !modelAndView.hasView() || modelAndView.getViewName().startsWith("redirect:")) {
            return;
        }

        // 1. 세션에서 프로젝트 ID를 꺼내옵니다.
        HttpSession session = request.getSession();
        Long projectId = (Long) session.getAttribute("currentProjectId");
     

        // 2. 세션에 ID가 있다면? (즉, 프로젝트 내부 화면이라면)
        if (projectId != null) {
            
            // 3. DB에서 프로젝트 이름과 활성화된 모듈(메뉴) 리스트를 싹 다 조회합니다.
            // ProjectVO project = projectService.selectProject(projectId);
            // List<EnabledModuleVO> moduleList = projectService.selectEnabledModules(projectId);
            
            // 4. 모델(ModelAndView)에 슬쩍 끼워 넣습니다.
            // 팀원이 컨트롤러에서 안 넣었어도, 여기서 강제로 넣어주니까 레이아웃 HTML이 정상 작동합니다!
            // modelAndView.addObject("project",  session.getAttribute("project"));
            // modelAndView.addObject("moduleList",  session.getAttribute("moduleList"));
        }
    }
}

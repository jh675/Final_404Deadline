# 교육기관 내 최종프로젝트, 'Final_404Deadline'
<!-- 박상원 -->
<!-- 안형주 -->
<!-- 정찬우 -->
<!-- 김진환 -->
<!-- 장수연 -->
<!-- 이민호 -->
<!-- 양현규 -->
### 교육기관에서 팀 단위로 실시한 최종프로젝트 결과물입니다.
### 저희 팀은 **PMS(프로젝트 관리 시스템)** 주제로 회사·프로젝트·이슈·일정을 통합 관리하는 웹 애플리케이션을 개발했습니다.

## <a href="https://github.com/jh675/Final_404Deadline" target="_blank">전체 프로젝트 바로 보기 ▶️</a>

## 개요

### PMS란?

* PMS(Project Management System)는 **프로젝트 기획부터 이슈·일정·문서·협업까지** 한 곳에서 관리하는 시스템입니다.
* 본 프로젝트는 Spring Boot 기반으로 **역할별 권한**, **프로젝트별 모듈 ON/OFF**, **실시간 알림(SSE)**, **AI 어시스턴트** 등을 제공합니다.

## 설계의 주안점

1. <b>Lombok</b>을 이용해 VO·DTO를 간결하게 정의하고 반복 코드를 줄였습니다.
2. <b>Controller</b>와 Thymeleaf로 화면 이동·폼 처리를 담당하고, <b>RestController</b>로 이슈·마일스톤·캘린더 등 API·비동기 요청을 분리했습니다.
3. <b>Toast UI Grid / Calendar / Editor</b>로 이슈 목록, 캘린더, 위키 등 데이터 UI를 구현했습니다.
4. <b>AJAX</b>와 서버 페이징으로 목록 조회 성능과 사용성을 개선했습니다.
5. <b>Bootstrap</b>과 Thymeleaf Layout으로 공통 레이아웃·모달 UI를 통일했습니다.
6. <b>Spring Security</b>와 프로젝트 단위 <b>ProjectAuthorizationManager</b>로 메뉴·URL 접근을 제어했습니다.
7. Oracle <b>저장 프로시저</b>(프로젝트 생성, 그룹·회원 등록 등)로 복합 트랜잭션을 DB에서 일관되게 처리했습니다.
8. <b>SSE</b>로 로그인 사용자에게 실시간 알림을 push합니다.
9. <b>Gemini / OpenAI / Ollama</b> 연동 AI 검색·어시스턴트로 프로젝트·이슈 맥락 기반 답변을 지원합니다(선택 설정).

## Contents

```sh
* 회사·사용자 관리 (시스템/기업 관리자)
* 프로젝트 관리 (생성·수정·모듈 설정·대시보드)
* 이슈 관리 (댓글·연관 이슈·히스토리·첨부)
* 간트차트 · 캘린더(공휴일 API) · 마일스톤·타임라인
* 게시판 · 공지 · 위키 · 문서 · 메시지
* 그룹·멤버·역할(RBAC) · 변경 이력
* 마이페이지 · 실시간 알림(SSE) · AI 어시스턴트
```

## 사용기술 및 개발환경

<hr>

|Category|Detail|
|:---:|:---:|
|OS|Windows 10 / 11|
|개발언어|Java 21, Spring Boot 4.0.6, Spring MVC, Spring Security, MyBatis, HTML5, JavaScript, CSS|
|데이터베이스|Oracle Database|
|뷰|Thymeleaf, Thymeleaf Layout Dialect|
|UI 라이브러리|Bootstrap, Toast UI Grid / Calendar / Editor|
|IDE|IntelliJ IDEA, VS Code, Cursor|
|빌드|Gradle|
|형상관리|Git, GitHub|
|기타|Lombok, Spring Mail, PageHelper, Actuator|

### 로컬 실행 요약

1. `FinalProject/src/main/resources/application.properties` 생성 (`.gitignore` 제외 — DB·메일·API 키 등 팀 설정 참고)
2. `file.upload-dir` 업로드 경로 생성
3. `FinalProject` 폴더에서 `.\gradlew.bat bootRun` (Windows) 또는 `./gradlew bootRun`
4. 브라우저 `http://localhost:8080` 접속 → `/login`

추가 가이드: [SSE 알림](FinalProject/docs/sse-notification.md), [첨부파일](FinalProject/docs/attach-usage.md)

## 프로젝트 기능 구현

<ul>
  <li>공통·인증</li>
    <ul><li>로그인·이메일 인증·비밀번호 재설정</li></ul>
    <ul><li>역할: 시스템 관리자 / 기업 관리자 / 사원</li></ul>
    <ul><li>Remember Me, CSRF</li></ul>
  <li>관리(Management)</li>
    <ul><li>프로젝트 목록·생성·수정·숨김·재개</li></ul>
    <ul><li>프로젝트별 모듈 활성화 (간트·캘린더·이슈·위키 등)</li></ul>
    <ul><li>회사·사용자 관리 (/admin, /cadmin)</li></ul>
  <li>프로젝트 협업</li>
    <ul><li>대시보드: 이슈 요약·공지·그룹별 인원</li></ul>
    <ul><li>이슈: CRUD, 댓글·대댓글, 연관 이슈, 변경 이력, 첨부</li></ul>
    <ul><li>간트차트, 캘린더·공휴일, 마일스톤·타임라인</li></ul>
    <ul><li>게시판, 공지, 위키, 문서, 메시지</li></ul>
    <ul><li>구성원 관리 (목록·상세·참여 신청)</li></ul>
    <ul><li>그룹 관리 (구성원·역할·메뉴 매핑)</li></ul>
    <ul><li>권한 관리 (역할·메뉴 RBAC)</li></ul>
    <ul><li>작업내역 (변경 이력 조회·상세)</li></ul>
  <li>부가 기능</li>
    <ul><li>공통 첨부파일 모듈</li></ul>
    <ul><li>SSE 실시간 알림 (navbar 연동)</li></ul>
    <ul><li>AI 어시스턴트 (/ai/assistant)</li></ul>
    <ul><li>마이페이지 (프로필·참여 프로젝트·간트 요약)</li></ul>
</ul>

# 페이지 소개

## 1.1 로그인 및 메인

### 로그인·회원가입·비밀번호 재설정 화면입니다.
##### Spring Security 폼 로그인을 사용하며, 사업자번호·회사 검색 후 가입할 수 있습니다.
##### 이메일 인증·비밀번호 변경 플로우가 연동되어 있습니다.
##### 로그인 성공 시 역할에 따라 관리자 메뉴 또는 프로젝트 목록으로 이동합니다.

## 1.2 프로젝트 관리

### 시스템·기업 관리자 및 사원이 접근하는 프로젝트 목록·생성·수정 페이지입니다.
##### 프로젝트 생성 시 매니저·일정·상태·**활성 모듈**(간트, 이슈, 위키 등)을 선택합니다.
##### 프로젝트 클릭 시 세션에 `currentProjectId`가 저장되고 대시보드로 진입합니다.
##### Oracle `PROC_PROJECT_CREATE` 등 저장 프로시저로 프로젝트·기본 그룹·역할·위키 초기 데이터를 함께 생성합니다.

## 1.3 프로젝트 대시보드

### 선택한 프로젝트의 메인 화면(`/project/main`)입니다.
##### 이슈 상태별 건수, 최근 이슈 목록, 공지, 그룹별 멤버 수를 한눈에 확인합니다.
##### 좌측 메뉴는 프로젝트에 활성화된 모듈과 사용자 권한에 따라 표시됩니다.

## 1.4 구성원 관리

### 프로젝트에 참여하는 구성원을 조회·등록·수정하는 화면(`/project/member`)입니다.
##### 목록 화면에서 구성원명·소속 그룹명으로 검색할 수 있습니다.
##### 상세 화면(`/project/member/info`)에서 프로필·소속 그룹·역할·첨부(프로필 이미지)를 관리합니다.
##### 프로젝트 참여 신청(`/project/member/join`) 및 회사 구성원 초대·그룹 배정 API를 제공합니다.
##### Oracle `PROC_GRP_INSERT` 등과 연동해 그룹·멤버 관계를 DB에서 일관되게 처리합니다.

## 1.5 그룹 관리

### 프로젝트 내 작업 그룹을 관리하는 화면(`/project/group`)입니다.
##### 그룹 목록에서 그룹명·생성일 기간으로 검색합니다.
##### 상세·등록 화면(`/project/group/info`)에서 그룹 구성원·연결 역할을 지정하고, 그룹명 중복 검사 API를 사용합니다.
##### 그룹별 역할·메뉴 권한 매핑(`/project/group/groupRoleMenus`) 및 구성원·역할 선택 모달 API를 지원합니다.

## 1.6 권한 관리

### 프로젝트 단위 역할(Role)과 메뉴 접근 권한을 설정하는 화면(`/project/role`)입니다.
##### Toast UI Grid 기반 역할 목록·검색(권한 키·이름·생성일)을 제공합니다.
##### 역할 상세(`/project/role/info`)에서 `ROLE_MENU`에 연결된 메뉴를 섹션별 체크박스로 설정합니다.
##### 역할에 연결된 그룹 조회·배정 API(`/project/role/roleGroups`)로 RBAC를 구성합니다.
##### 역할 삭제 시 Oracle `PROC_ROLE_DELETE` 등 저장 프로시저를 호출합니다.

## 1.7 작업내역

### 프로젝트에서 발생한 데이터 변경 이력을 조회하는 화면(`/project/history/list`)입니다.
##### 수정일·수정자·대상 테이블·상세 키워드로 필터링할 수 있습니다.
##### 목록 행 선택 시 AJAX(`/project/history/detail`)로 변경 필드(이전값·이후값) 상세를 모달에 표시합니다.
##### 이슈·구성원·그룹 등 주요 모듈 작업이 `HISTORY` 테이블에 기록됩니다.

## 1.8 이슈 관리

### 프로젝트 이슈 목록·등록·상세·수정 화면입니다.
##### Toast UI Grid로 목록·필터·서버 페이징을 지원합니다.
##### 댓글·대댓글, 연관 이슈, 변경 히스토리, 첨부파일을 REST API(`/project/issue/api`)로 처리합니다.
##### 프로젝트 권한이 없는 메뉴 URL은 접근이 차단됩니다.

## 1.9 간트차트

### 일정·작업을 막대 형태로 표시하는 간트 화면입니다.
##### 프로젝트 모듈 `GANTT`가 활성화된 경우에만 메뉴가 노출됩니다.

## 1.10 위키

### 프로젝트 위키 목록·작성·조회·이력 화면입니다.
##### 트리 인덱스, 문서 링크 제안, 중복 제목 검사 API를 제공합니다.
##### 프로젝트 생성 시 기본 위키 페이지가 프로시저로 함께 생성됩니다.

## 1.11 마일스톤·타임라인

### 마일스톤 CRUD 및 타임라인 연동 화면입니다.
##### REST API(`/project/milestone/api`)로 목록·등록·수정·삭제 및 이슈 미연동 목록 조회를 지원합니다.

## 1.12 캘린더

### 일정·이벤트 캘린더와 공휴일 API 연동 화면입니다.
##### JSON API로 일정 목록·상세·검색을 제공합니다.

## 1.13 실시간 알림

### navbar에 연동된 SSE(Server-Sent Events) 알림입니다.
##### `GET /api/notifications/subscribe`로 구독하며, 이슈·멤버 등 이벤트 발생 시 push됩니다.
##### 상세 동작은 [FinalProject/docs/sse-notification.md](FinalProject/docs/sse-notification.md)를 참고하세요.

# 소스코드 소개

## 2.1 프로젝트 관리 — 프로젝트 생성 프로시저

```sql:proc_prj_create.sql
create or replace PROCEDURE PROC_PROJECT_CREATE
--  작성자: 김진환
--  내용: 프로젝트 생성 프로시저
(
  p_prj_name project.prj_name%TYPE,
  p_biz_no project.biz_no%TYPE,
  p_prj_desc project.prj_desc%TYPE,
  p_mem_id project.user_id%TYPE,
  p_prj_identifier project.prj_identifier%TYPE,
  p_modules VARCHAR2 DEFAULT NULL,
  p_start_date project.start_date%TYPE,
  p_closed_date project.closed_date%TYPE,
  p_result_status OUT VARCHAR2,
  p_result_msg OUT VARCHAR2
)
IS
  v_prj_id project.id%TYPE;
  v_module_token VARCHAR2(4000) := p_modules;
  E_DUP_PRJ EXCEPTION;
BEGIN
  -- 동명·식별자 중복 검사 후 PROJECT INSERT
  -- 선택 모듈(GANTT, ISSUE, WIKI 등) ENABLED_MODULES 등록
  -- 기본 그룹·역할·멤버·위키 초기화
  ...
END;
```

## 2.2 그룹 관리 — 그룹 등록 프로시저

```sql:proc_grp_insert.sql
create or replace PROCEDURE PROC_GRP_INSERT
--  작성자: 김진환
--  내용: 그룹 추가 및 구성원 등록
(
  p_prj_id     IN  grp.prj_id%TYPE,
  p_grp_nm     IN  grp.name%TYPE,
  p_mem_ids    IN  VARCHAR2 DEFAULT NULL,
  p_result_msg OUT VARCHAR2
)
IS
  v_grp_id   grp.id%TYPE;
BEGIN
  INSERT INTO grp (id, prj_id, name, created_on, status_cd)
  VALUES (grp_seq.NEXTVAL, p_prj_id, p_grp_nm, SYSDATE, '01ACTIVE')
  RETURNING id INTO v_grp_id;
  -- p_mem_ids ('1,2,3') 파싱 후 그룹 멤버 연결
  ...
END;
```

## 2.3 사용자 관리 — 회원 등록 프로시저

```sql:proc_user_insert.sql
create or replace PROCEDURE PROC_USER_INSERT
--  작성자: 박상원
--  내용: 회원 등록 (동일 회사 내 로그인 ID 중복 체크)
(
    p_biz_no           USERS.BIZ_NO%TYPE,
    p_login            USERS.LOGIN%TYPE,
    p_password         USERS.PASSWORD%TYPE,
    p_name             USERS.NAME%TYPE,
    p_email            USERS.EMAIL%TYPE,
    p_admin_cd         USERS.ADMIN_CD%TYPE,
    o_result           OUT VARCHAR2,
    o_new_id           OUT NUMBER
)
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM USERS
    WHERE BIZ_NO = p_biz_no AND LOGIN = p_login;

    IF v_count > 0 THEN
        o_result := 'DUPLICATE';
    ELSE
        INSERT INTO USERS (...) VALUES (...);
        o_result := 'SUCCESS';
    END IF;
END;
```

#### 감사합니다.

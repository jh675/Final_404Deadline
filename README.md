# 교육기관 내 최종프로젝트, 'yedamine' 
### 교육기관에서 팀 단위로 실시한 최종프로젝트 결과물입니다.
### 팀 이름 : Issue Guide
발생하는 이슈를 완료할 때까지 쉽게 안내하여 프로젝트에 도움이 되자라는 뜻으로 만들었습니다.
## 개요

### 1. 프로젝트 개요
프로젝트와 이슈, 마일스톤을 통합 관리하며 쉬운 사용법으로 업무에 도움이 되는 협업 도구 입니다.

### 2. 개발동기
기존의 프로젝트 협업 관리 도구는 직관적이지 않은 UI와 복잡한 설정으로 인해 사용자가 도구 적응에 피로감을 느끼고 협업에 방해가 되는 요소가 있었습니다. 이를 개선하여 쉬운 업무 환경을 제공하고 협업에 집중할 수 있는 프로그램을 만들어 사용자의 프로젝트에 도움이 되고자 개발을 하게 되었습니다. 

### 3. 기대효과
- <b>쉬운 도구 사용</b> : 부트스트랩 기반 친숙한 디자인과 직관적인 UI 배치로 사용자의 편의성을 증대하여 협업의 접근성을 높였습니다.
- <b>협업의 효율성 증대</b> : 일정 관리 및 공유를 통해 협업하는 사람들과 일정을 빠르게 조율할 수 있고 업무를 체계적으로 진행할 수 있는 환경을 만들었습니다.
- <b>업무 생산성 증대</b> : 실시간 알림과 AI 검색기능으로 담당한 이슈를 쉽게 파악할 수 있고 작업을 위한 설정을 간소화하여 이슈 해결이 쉬워저 프로젝트 생산성을 높였습니다.
## 팀원
 #### - 박상원(팀장)
 #### - 정찬우(부팀장)
 #### - 안형주(부팀장)
 #### - 김진환
 #### - 장수연
 #### - 양현규
 #### - 이민호

## 개발 기간
 #### - 전체 개발 기간 : 2026.05.04 ~ 2026.06.12
 #### - 설계 기간 : 2026.05.04 ~ 2026.05.12
 #### - 구현 기간 : 2026.05.13 ~ 2026.06.05 
 #### - 배포 및 테스트 : 2026.06.05 ~ 2026.06.08
 #### - 발표 준비 : 2026.06.09 ~ 2026.06.11
 #### - 프로젝트 발표 : 2026.06.13

## 커밋 메세지 형식
#### (YYMMDD / 이름 / 작업종류 / 작업내용)
#### EX) 260601 / 박상원 / Fix / 로그인시 Security 보안 필터 안되는 문제 수정

## 설계주안점
1. <b>Lombok</b>을 이용해 VO·DTO를 간결하게 정의하고 반복 코드를 줄였습니다.
2. <b>Controller</b>와 Thymeleaf로 화면 이동·폼 처리를 담당하고, <b>RestController</b>로 이슈·마일스톤·캘린더 등 API·비동기 요청을 분리했습니다.
3. <b>Toast UI Grid / Calendar / Editor</b>로 이슈 목록, 캘린더, 위키 등 데이터 UI를 구현했습니다.
4. <b>AJAX</b>와 서버 페이징으로 목록 조회 성능과 사용성을 개선했습니다.
5. <b>Bootstrap</b>과 Thymeleaf Layout으로 공통 레이아웃·모달 UI를 통일했습니다.
6. <b>Spring Security</b>와 프로젝트 단위 <b>ProjectAuthorizationManager</b>로 메뉴·URL 접근을 제어했습니다.
7. Oracle <b>저장 프로시저</b>(프로젝트 생성, 그룹·회원 등록 등)로 복합 트랜잭션을 DB에서 일관되게 처리했습니다.
8. <b>SSE</b>로 로그인 사용자에게 실시간 알림을 push합니다.
9. <b>Gemini / OpenAI / Ollama</b> 연동 AI 검색·어시스턴트로 프로젝트·이슈 맥락 기반 답변을 지원합니다

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
|OS|Windows 11|
|개발언어|Java 21, Spring Boot 4.0.6, Spring MVC, Spring Security, MyBatis, HTML5, JavaScript, CSS|
|데이터베이스|Oracle Database|
|뷰|Thymeleaf, Thymeleaf Layout Dialect|
|UI 라이브러리|Bootstrap, Toast UI Grid / Calendar / Editor|
|IDE|IntelliJ IDEA, Eclipse, VS Code, Cursor|
|빌드|Gradle|
|형상관리|Git, GitHub|
|기타|Lombok, Spring Mail, PageHelper, Actuator|

추가 가이드: [SSE 알림](FinalProject/docs/sse-notification.md), [첨부파일](FinalProject/docs/attach-usage.md)

## 프로젝트 기능 구현

<ul>
  <li>공통·인증</li>
    <ul><li>로그인·이메일 인증·비밀번호 재설정</li></ul>
    <ul><li>역할: 시스템 관리자 / 기업 관리자 / 사원</li></ul>
    <ul><li>Remember Me, CSRF</li></ul>
  <li>관리(Management)</li>
    <ul><li>프로젝트 목록·생성·수정·삭제·재개</li></ul>
    <ul><li>프로젝트별 모듈 활성화 (간트·캘린더·이슈·위키 등)</li></ul>
    <ul><li>기업·사용자 관리</li></ul>
  <li>프로젝트 협업</li>
    <ul><li>개요: 이슈 요약·공지·그룹별 인원</li></ul>
    <ul><li>이슈: CRUD, 댓글·대댓글, 연관 이슈, 변경 이력, 첨부</li></ul>
    <ul><li>간트차트, 마일스톤·타임라인</li></ul>
    <ul><li>게시판, 공지, 위키, 문서, 메시지</li></ul>
    <ul><li>구성원 관리 (목록·상세·참여 신청)</li></ul>
    <ul><li>그룹 관리 (구성원·역할·메뉴 매핑)</li></ul>
    <ul><li>권한 관리 (역할·메뉴 RBAC)</li></ul>
    <ul><li>작업내역 (변경 이력 조회·상세)</li></ul>
  <li>부가 기능</li>
    <ul><li>공통 첨부파일 모듈</li></ul>
    <ul><li>SSE 실시간 알림 (navbar 연동)</li></ul>
    <ul><li>일정 공유 및 관리</li></ul>
    <ul><li>AI 어시스턴트 </li></ul>
    <ul><li>마이페이지 (프로필·참여 프로젝트·간트 요약)</li></ul>
</ul>

# 페이지 소개

## 1.1 로그인 및 메인

### 로그인·회원가입·비밀번호 재설정 화면입니다.
##### Spring Security 폼 로그인을 사용하며, 회사 선택 후 로그인 가능합니다.
##### 이메일 인증·비밀번호 변경 플로우가 연동되어 있습니다.
##### 로그인 성공 시 역할에 따라 관리자 메뉴 또는 프로젝트 목록으로 이동합니다.

## 1.2 프로젝트 관리

### 시스템·기업 관리자 및 사원이 접근하는 프로젝트 목록·생성·수정 페이지입니다.
##### 프로젝트 생성 시 매니저·일정·상태·**활성 모듈**(간트, 이슈, 위키 등)을 선택합니다.
##### 프로젝트 클릭 시 세션에 `currentProjectId`가 저장되고 대시보드로 진입합니다.
##### Oracle `PROC_PROJECT_CREATE` 등 저장 프로시저로 프로젝트·기본 그룹·역할·위키 초기 데이터를 함께 생성합니다.

## 1.3 프로젝트 개요

### 선택한 프로젝트의 메인 화면입니다.
##### 이슈 상태별 건수, 최근 이슈 목록, 공지, 그룹별 멤버 수를 한눈에 확인합니다.
##### 좌측 메뉴는 프로젝트에 활성화된 모듈과 사용자 권한에 따라 표시됩니다.

## 1.4 구성원 관리

### 프로젝트에 참여하는 구성원을 조회·등록·수정하는 화면입니다.
##### 목록 화면에서 구성원명·소속 그룹명으로 검색할 수 있습니다.
##### 상세 화면에서 프로필·소속 그룹·역할·첨부(프로필 이미지)를 관리합니다.
##### 프로젝트 참여 신청 및 회사 구성원 초대·그룹 배정 API를 제공합니다.
##### Oracle `PROC_GRP_INSERT` 등과 연동해 그룹·멤버 관계를 DB에서 일관되게 처리합니다.

## 1.5 그룹 관리

### 프로젝트 내 작업 그룹을 관리하는 화면입니다.
##### 그룹 목록에서 그룹명·생성일 기간으로 검색합니다.
##### 상세·등록 화면에서 그룹 구성원·연결 역할을 지정하고, 그룹명 중복 검사 API를 사용합니다.
##### 그룹별 역할·메뉴 권한 매핑 및 구성원·역할 선택 모달 API를 지원합니다.

## 1.6 권한 관리

### 프로젝트 단위 역할(Role)과 메뉴 접근 권한을 설정하는 화면입니다.
##### Toast UI Grid 기반 역할 목록·검색(권한 키·이름·생성일)을 제공합니다.
##### 역할 상세에서 `ROLE_MENU`에 연결된 메뉴를 섹션별 체크박스로 설정합니다.
##### 역할에 연결된 그룹 조회·배정 API로 RBAC를 구성합니다.
##### 역할 삭제 시 Oracle `PROC_ROLE_DELETE` 등 저장 프로시저를 호출합니다.

## 1.7 작업내역

### 프로젝트에서 발생한 데이터 변경 이력을 조회하는 화면입니다.
##### 수정일·수정자·대상 테이블·상세 키워드로 필터링할 수 있습니다.
##### 목록 행 선택 시 AJAX로 변경 필드(이전값·이후값) 상세를 모달에 표시합니다.
##### 이슈·구성원·그룹 등 주요 모듈 작업이 `HISTORY` 테이블에 기록됩니다.

## 1.8 이슈 관리

### 프로젝트 이슈 목록·등록·상세·수정 화면입니다.
##### Toast UI Grid로 목록·필터·서버 페이징을 지원합니다.
##### 댓글·대댓글, 연관 이슈, 변경 히스토리, 첨부파일을 REST API로 처리합니다.
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
##### REST API로 목록·등록·수정·삭제 및 이슈 미연동 목록 조회를 지원합니다.

## 1.12 캘린더

### 일정·이벤트 캘린더와 공휴일 API 연동 화면입니다.
##### JSON API로 일정 목록·상세·검색을 제공합니다.

## 1.13 실시간 알림

### navbar에 연동된 SSE(Server-Sent Events) 알림입니다.
##### GET 방식으로 구독하며, 이슈·멤버 등 이벤트 발생 시 push됩니다.
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

```sql:proc_email_verify_insert.sql
create or replace PROCEDURE PROC_EMAIL_VERIFY_INSERT
--  작성자: 박상원
--  내용: 회원정보 조회, 이메일 확인, 인증번호 생성, 인증 테이블에 insert를 한번에 하는 프로시저
(
    p_biz_no            users.biz_no%TYPE,
    p_login             users.login%TYPE,
    p_email             users.email%TYPE,
    
    o_result        out     VARCHAR2, -- 진행이 잘 되었는지 확인하기 위한 출력
    o_verify_num    out     VARCHAR2  -- 이메일로 보낼 인증번호 출력
) 
IS
    v_user_id       users.id%TYPE;
    v_verify_num    email_verify.verify_num%TYPE;
BEGIN
  -- 사용자 조회
  SELECT id
  INTO   v_user_id
  FROM   users
  WHERE  biz_no = p_biz_no
  AND    login  = p_login
  AND    email  = p_email;
  
  -- 해당유저의 남아있는 기존 인증들 실패처리
  UPDATE email_verify
  SET    verify_status_cd = '03CERTSTAT'
  WHERE  user_id = v_user_id
  AND    verify_status_cd = '01CERTSTAT';
  
  -- 인증번호 생성
  v_verify_num := LPAD(TRUNC(DBMS_RANDOM.VALUE(0,999999)),6,'0');
  
  -- 인증 테이블에 insert
  INSERT INTO email_verify ( id
                            ,user_id
                            ,email
                            ,verify_num
                            ,created_on
                            ,closed_on
                            ,verify_status_cd )
  VALUES                   ( vfy_seq.NEXTVAL
                            ,v_user_id
                            ,p_email
                            ,v_verify_num
                            ,SYSDATE
                            ,SYSDATE + (3 / (24 * 60)) -- 생성시각 + 3분
                            ,'01CERTSTAT' );
  o_result := 'success';
  o_verify_num := v_verify_num;
  COMMIT;
  
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        o_result := 'no_user';
        
    WHEN OTHERS THEN
        o_result := 'error';

END PROC_EMAIL_VERIFY_INSERT;
```

#### 감사합니다.

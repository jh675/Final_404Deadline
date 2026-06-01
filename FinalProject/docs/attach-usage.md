# 첨부파일(Attach) 사용 가이드

공통 첨부 모듈은 **디스크 저장 + DB 메타데이터(`ATTACHMENTS` 테이블)** 로 동작합니다.  
이슈(Issue) 기능이 대표 구현 예시입니다.

---

## 1. 구성 요소

| 구분 | 경로 |
|------|------|
| Controller | `com.example.demo.util.attach.controller.AttachController` |
| Service | `com.example.demo.util.attach.service.AttachService` |
| Service 구현 | `com.example.demo.util.attach.service.impl.AttachServiceImpl` |
| Mapper | `com.example.demo.util.attach.mapper.AttachMapper` |
| MyBatis XML | `src/main/resources/mapper/util/AttachMapper.xml` |
| VO | `com.example.demo.util.attach.service.AttachVO` |

### 설정 (`application.properties`)

```properties
file.upload-dir=D:/uploads/
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=55MB
```

### 디스크 저장 경로 규칙

```
{file.upload-dir}/{containerType}/{containerId}/{yyyyMMddHHmmssSSS}_{원본파일명}
```

예: 이슈 ID `42` → `D:/uploads/ISSUE/42/20260518143052123_report.pdf`

### DB 테이블 `ATTACHMENTS`

| 컬럼 | VO 필드 | 설명 |
|------|---------|------|
| ID | `id` | PK (`att_seq`) |
| TABLE_NAME | `tableName` | 연결 대상 모듈 코드 (예: `04MODULE`) |
| CONTAINER_ID | `containerId` | 게시글/이슈 등 본문 PK |
| CONTAINER_TYPE | `containerType` | 저장 폴더 구분 (예: `ISSUE`) |
| FILENAME | `fileName` | 사용자에게 보이는 원본 파일명 |
| DISK_FILENAME | `diskFileName` | 실제 저장 파일명 |
| FILESIZE | `fileSize` | 바이트 크기 |
| CONTENT_TYPE | `contentType` | MIME |
| DISK_DIRECTORY | `diskDirectory` | 저장 디렉터리 절대 경로 |
| CREATED_ON | `createdOn` | 등록 일시 |

---

## 2. 첨부파일 등록

### 2-1. 권장: 한 번에 저장 + DB 등록

컨테이너(이슈, 공지 등) **PK가 확정된 뒤** 호출합니다.

```java
@Autowired
private AttachService attachService;

// issueId: 본문 PK
// attachments: multipart 파일 배열
// tableName: 모듈 구분 코드 (이슈 = "04MODULE")
// containerType: 디스크 폴더명 (이슈 = "ISSUE")
attachService.saveAndInsertAttachments(id, attachments, tableName, containerType);
```

내부 동작:

1. `saveAttach()` — 디스크에 파일 저장, `AttachVO` 리스트 생성
2. 각 VO에 `containerId`, `containerType`, `tableName` 설정
3. `insertAttach()` — `ATTACHMENTS` INSERT

### 2-2. 단계 분리 (필요 시)

```java
// 1) 디스크만 저장
List<AttachVO> saved = attachService.saveAttach(attachments, "ISSUE", containerId, "04MODULE");

// 2) containerId 등 보완 후 DB 등록
for (AttachVO a : saved) {
    a.setContainerId(containerId);
    a.setContainerType("ISSUE");
    a.setTableName("04MODULE");
}
attachService.insertAttach(saved);
```

### 2-3. Controller 예시 (이슈 등록/수정)

`IssueController` 참고:

```java
@PostMapping(value = "/issue/insert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public String issueInsert(
        @RequestPart("issue") IssueInputVO issueVO,
        @RequestPart(value = "attachments", required = false) MultipartFile[] attachments) {

    boolean hasFiles = hasAttachmentFiles(attachments);
    if (hasFiles) {
        issueVO.setIsAttachCd("01ISATTACH"); // 첨부 여부 플래그(도메인별)
    }
    Long issueId = service.insertIssue(issueVO);  // PK 먼저 발급
    if (hasFiles && issueId != null) {
        attachService.saveAndInsertAttachments(issueId, attachments, "04MODULE", "ISSUE");
    }
    return "redirect:/issue/list";
}
```

**주의**

- `insert`는 **본문 INSERT 후** `issueId`로 첨부를 붙입니다.
- `update`는 기존 `issueVO.getId()`로 추가 첨부만 저장합니다 (기존 첨부는 유지).

### 2-4. 프론트엔드 (multipart/form-data)

`issueRegist.html` 패턴:

- 파일 input의 `name`은 모두 **`attachments`** (배열로 전송)
- JSON 본문은 **`issue`** 파트로 분리

```javascript
var formData = new FormData();
formData.append('issue', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
attachRows.querySelectorAll('input[type="file"]').forEach(function (inp) {
    if (inp.files && inp.files[0]) {
        formData.append('attachments', inp.files[0]);
    }
});

fetch('/issue/insert', { method: 'POST', headers: csrfHeaders, body: formData });
```

클라이언트 제한 (이슈 등록 화면 기준):

- 최대 **5개** 파일
- 합계 **50MB** 이하

---

## 3. 첨부파일 조회

### 3-1. Service

```java
List<AttachVO> attachments = attachService.selectAttachList("04MODULE", issueId);
```

- `tableName`: 모듈 코드
- `containerId`: 본문 PK
- 없거나 null이면 빈 리스트 반환

### 3-2. Controller → 화면

```java
@GetMapping("/issue/detail")
public String issueDetail(Model model, @RequestParam("id") Long id) {
    // ...
    List<AttachVO> attachments = attachService.selectAttachList("04MODULE", id);
    model.addAttribute("attachments", attachments);
    return "project/issue/issueDetail";
}
```

### 3-3. Thymeleaf 목록 렌더링

```html
<span class="attach-item" th:each="a : ${attachments}">
    <span th:text="${a.fileName != null ? a.fileName : a.diskFileName}">파일명</span>
    <a th:href="@{/download/{id}(id=${a.id})}" title="다운로드">⬇</a>
    <button type="button" class="attach-delete-btn"
            th:attr="data-attach-id=${a.id}">&times;</button>
</span>
```

### 3-4. 단건 조회

```java
AttachVO attach = attachService.selectAttach(attachId);
```

다운로드/삭제 전 메타데이터 조회에 사용합니다.

---

## 4. 첨부파일 다운로드

### API

| 메서드 | URL | 설명 |
|--------|-----|------|
| GET | `/download/{id}` | `ATTACHMENTS.ID`로 파일 스트림 반환 |

### 동작 (`AttachController`)

1. `selectAttach(id)` 로 메타 조회
2. `diskDirectory` + `diskFileName` 경로에서 `Resource` 로드
3. `Content-Disposition: attachment` + 원본 `fileName`으로 다운로드

### 화면에서 사용

```html
<a th:href="@{/download/{id}(id=${a.id})}">다운로드</a>
```

브라우저 링크 클릭만으로 동작합니다 (별도 JavaScript 불필요).

---

## 5. 첨부파일 삭제

### API

| 메서드 | URL | 설명 |
|--------|-----|------|
| DELETE | `/delete/{id}` | DB 행 삭제 + 디스크 파일 삭제 |

### 동작 (`AttachController`)

1. `selectAttach(id)` — 삭제 전 경로 확보
2. `deleteAttach(id)` — DB DELETE
3. `removeAttach(attachVO)` — `Files.delete()` 로 물리 파일 삭제

### 프론트엔드 (CSRF + fetch)

```javascript
fetch('/delete/' + encodeURIComponent(id), {
    method: 'DELETE',
    headers: { [csrfHeaderName]: csrfToken }
})
.then(function (res) {
    if (!res.ok) throw new Error('delete failed');
    location.reload();
});
```

`issueDetail.html`의 `.attach-delete-btn` 클릭 시 위 패턴을 사용합니다.

---

## 6. 새 도메인에 붙이는 체크리스트

1. **`tableName`**, **`containerType`** 값을 팀에서 고정 (이슈: `04MODULE` / `ISSUE`)
2. 본문 **INSERT 후 PK**를 받아 `saveAndInsertAttachments` 호출
3. 상세 화면 Controller에서 `selectAttachList(tableName, containerId)` 로 목록 전달
4. 템플릿에 `/download/{id}`, `/delete/{id}` 링크/버튼 연결
5. multipart Controller는 `consumes = MediaType.MULTIPART_FORM_DATA_VALUE` + `@RequestPart` 사용
6. `application.properties`의 업로드 경로·용량 확인

### `hasAttachmentFiles` 유틸 (Controller private 메서드 예시)

```java
private boolean hasAttachmentFiles(MultipartFile[] attachments) {
    if (attachments == null) return false;
    for (MultipartFile f : attachments) {
        if (f != null && !f.isEmpty()) return true;
    }
    return false;
}
```

---

## 7. AttachService 메서드 요약

| 메서드 | 용도 |
|--------|------|
| `saveAndInsertAttachments(containerId, files, tableName, containerType)` | 저장 + DB 등록 (일반 등록용) |
| `saveAttach(files, containerType, containerId, tableName)` | 디스크 저장만 |
| `insertAttach(List<AttachVO>)` | DB 등록만 |
| `selectAttachList(tableName, containerId)` | 목록 조회 |
| `selectAttach(id)` | 단건 조회 |
| `deleteAttach(id)` | DB 삭제 |
| `removeAttach(AttachVO)` | 디스크 파일 삭제 |

---

## 8. 참고 구현 파일

- 이슈 등록/수정: `IssueController.java`, `issueRegist.html`
- 이슈 상세(목록·다운로드·삭제 UI): `IssueController.java` (`/issue/detail`), `issueDetail.html`
- 공통 API: `AttachController.java`

# TOAST UI 사용법 (CDN 기준)

이 문서는 **NHN TOAST UI** 계열 라이브러리를 브라우저에서 **CDN(`https://uicdn.toast.com`)** 으로 불러 쓸 때의 로드 순서, 대표 옵션, 그리고 **본 프로젝트에서 아직 쓰지 않는 컴포넌트**까지 한곳에 정리한 참고 자료입니다.

> **참고**: CDN 경로의 `latest`는 배포 시점에 따라 동작이 바뀔 수 있어, 운영에는 `latest` 대신 **특정 버전 번호**를 쓰는 것을 권장합니다.  
> 예: `.../grid/latest/...` → `.../grid/v4.21.22/...` (실제 배포 버전은 [각 GitHub 릴리스](https://github.com/nhn)에서 확인)

---

## 1. 이 프로젝트에서 사용 중인 것

| 구분 | 용도 | 템플릿 예시 |
|------|------|-------------|
| **TOAST UI Grid** | 이슈 목록 표·정렬·클라이언트 페이징 | `src/main/resources/templates/project/issue/issueList.html` |
| **tui-pagination** | Grid 하단 페이지 번호 UI (Grid 페이징의 **의존 라이브러리**) | 동일 (Grid보다 **먼저** 로드) |

Grid 페이징을 쓸 때는 공식 문서대로 **`tui-pagination` 스크립트를 `tui-grid.js`보다 앞에** 두어야 하단 네비게이션이 생성됩니다.

### 1.1 Grid + Pagination CDN (권장 순서)

```html
  <link rel="stylesheet" href="https://uicdn.toast.com/tui.pagination/latest/tui-pagination.css" />
  <link rel="stylesheet" href="https://uicdn.toast.com/grid/latest/tui-grid.css" />
  <script src="https://uicdn.toast.com/tui.pagination/latest/tui-pagination.js"></script>
  <script src="https://uicdn.toast.com/grid/latest/tui-grid.js"></script>
```

### 1.2 Grid 생성 시 자주 쓰는 옵션

| 옵션 | 설명 |
|------|------|
| `el` | 그리드를 넣을 DOM 요소 (필수) |
| `data` | 행 데이터 배열 (`[{ 컬럼명: 값 }, ...]`) 또는 DataSource 객체 |
| `columns` | 컬럼 정의 배열 (`header`, `name`, `width`, `align`, `sortable`, `formatter`, `comparator` 등) |
| `scrollX` / `scrollY` | 가로·세로 스크롤 사용 여부 |
| `bodyHeight` | 본문 높이. `'auto'` \| `'fitToParent'` \| 픽셀 숫자 |
| `rowHeight` | 행 높이(px) |
| `minBodyHeight` | 본문 최소 높이 |
| `pageOptions` | 페이징 설정 (아래 표) |

### 1.3 `pageOptions` (Grid 페이징)

클라이언트에서 전체 데이터를 받은 뒤 페이지만 나눠 보여줄 때:

```javascript
pageOptions: {
  useClient: true,
  perPage: 15,
  // page: 1,
  // visiblePages: 10,
  // position: 'bottom',
}
```

| 필드 | 설명 |
|------|------|
| `useClient` | `true`면 브라우저에서만 페이징 (서버 재요청 없음) |
| `perPage` | 페이지당 행 수 |
| `page` | 초기 페이지(1부터). 생략 시 1 |
| `visiblePages` | 페이지 번호 버튼 최대 개수 (Grid **v4.14.0+**) |
| `position` | `'bottom'` \| `'top'` — 페이징 바 위치 |

서버 페이징·`DataSource` 연동은 `useClient: false` 와 `dataSource`·`api.readData` 등 별도 설정이 필요합니다. ([Data Source 문서](https://github.com/nhn/tui.grid/blob/master/packages/toast-ui.grid/docs/en/data-source.md))

### 1.4 페이징 관련 Grid API (선택)

| 메서드 | 설명 |
|--------|------|
| `grid.getPagination()` | 내부 `tui-pagination` 인스턴스 (없으면 `null`) |
| `grid.setPerPage(n)` | 페이지당 행 수 변경 |
| `grid.setTotalCount(n)` | 서버 페이징 등에서 전체 건수 동기화 시 |

이벤트: `beforePageMove`, `afterPageMove` (`grid.on('beforePageMove', ...)`)

---

### 1.5 데이터 변환

	<!-- ====================================================
	     서버 데이터를 JS 로 주입 (Thymeleaf inline javascript)
	     - th:inline="javascript" : Thymeleaf 표현식을 JS 안에서 사용 가능하게 함
	     - /*[[${issueList}]]*/ [] : 서버에서 내려준 List<IssueVO>를
	       JS 배열(JSON)로 변환해서 issueData 변수에 담음
	     - /*<![CDATA[*/ ... /*]]>*/ : XML 파싱 오류 방지용 (HTML5 에선 권장 패턴)
	==================================================== -->
	<script th:inline="javascript">
		/*<![CDATA[*/
		const issueData = /*[[${issueList}]]*/ [];
		/*]]>*/
	</script>


### 1.6 정렬 커스텀

			function priorityRank(value) {
				if (value == null || value === '') return 999;
				const key = String(value).trim(); //value에서 공백 제거
				const order = {
					최상: 1,//값 우선순위 지정
					상: 2,
					중: 3,
					하: 4,

				};
				// order에 직접 정의된 키인지(hasOwnProperty)만 순위를 부여한다.
				// → 예: 키가 "toString" 등 Object.prototype 이름과 겹치면 잘못된 순위가 되는 것을 방지.
				// 매핑에 없는 값은 999로 두어 오름차순 정렬 시 맨 뒤로 보낸다.
				return order.hasOwnProperty(key) ? order[key] : 999;//키가 있으면 그대로 사용 없으면 뒤로 보낸다다
			}
      //정렬에 사용하는 메소드드
			function priorityComparator(valueA, valueB) {
				return priorityRank(valueA) - priorityRank(valueB);
			}
---
### 1.7 헤더 
					{
						header: '우선순위',
						name: 'priorityCd',
						width: 100,
						align: 'center',
						sortable: true,
						comparator: priorityComparator
					},

## 2. 이 프로젝트에서 사용하지 않는 TOAST UI — CDN 및 대표 옵션

아래는 **현재 이슈 목록 화면에는 포함되어 있지 않은** 컴포넌트입니다. 필요 시 공식 문서와 버전을 맞춰 적용하세요.

---

### 2.1 TOAST UI Editor (마크다운 / WYSIWYG 에디터)

**용도**: 게시글·위키·이슈 본문 등 리치 텍스트 입력.

```html
<link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
<script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
```

```javascript
const Editor = toastui.Editor;
const editor = new Editor({
  el: document.querySelector('#editor'),
  height: '500px',
  initialEditType: 'markdown', // 'markdown' | 'wysiwyg'
  previewStyle: 'vertical',    // 'tab' | 'vertical'
  initialValue: 'hello world',
  language: 'ko',
});
```

| 옵션 | 설명 |
|------|------|
| `el` | 에디터 컨테이너 (필수) |
| `height` | 높이 |
| `initialEditType` | 최초 모드 |
| `previewStyle` | 미리보기 배치 |
| `initialValue` | 초기 내용 |
| `hooks`, `plugins` | 플러그인·훅 확장 |

문서: [tui.editor](https://github.com/nhn/tui.editor)

---

### 2.2 TOAST UI Chart

**용도**: 막대·선·파이 등 통계 차트.

```html
<link rel="stylesheet" href="https://uicdn.toast.com/chart/latest/tui-chart.min.css" />
<script src="https://uicdn.toast.com/chart/latest/tui-chart.min.js"></script>
```

```javascript
const container = document.getElementById('chart');
const data = {
  categories: ['A', 'B', 'C'],
  series: [{ name: '값', data: [10, 20, 30] }],
};
const options = { chart: { width: 500, height: 400, title: '제목' } };
const chart = tui.chart.barChart(container, data, options);
```

| 옵션 영역 | 설명 |
|-----------|------|
| `chart` | 너비·높이·제목·포맷 등 |
| `series` / `categories` | 데이터 형식은 차트 종류별로 상이 |
| 생성 함수 | `barChart`, `columnChart`, `lineChart`, `pieChart` 등 |

문서: [tui.chart](https://github.com/nhn/tui.chart)  
※ 최신 패키지가 `@toast-ui/chart` 로 분리된 버전도 있으므로, 프로젝트에 맞는 버전 문서를 확인하세요.

---

### 2.3 TOAST UI Calendar

**용도**: 일정·캘린더 UI (월간·주간·일간 뷰, 드래그, 타임존, 다중 캘린더 색상 등).

공식 옵션 설명: [Options · tui.calendar](https://github.com/nhn/tui.calendar/blob/main/docs/en/apis/options.md) (아래 내용은 해당 문서를 요약·번역한 참고용입니다. 버전에 따라 기본값·동작이 다를 수 있습니다.)

#### CDN (예시)

```html
<link rel="stylesheet" href="https://uicdn.toast.com/calendar/latest/toastui-calendar.min.css" />
<script src="https://uicdn.toast.com/calendar/latest/toastui-calendar.min.js"></script>
```

```javascript
// 전역 객체 이름은 번들·버전에 따라 다를 수 있음 → 공식 Plain JS 예제 확인
const Calendar = toastui.Calendar;
const calendar = new Calendar('#calendar', {
  defaultView: 'month',
  usageStatistics: false,
});

// 생성 후에도 옵션 변경 가능
calendar.setOptions({ defaultView: 'week' });
```

#### 최상위 옵션 (`Calendar` 생성자 / `setOptions` 공통)

인스턴스 생성 시 넘기는 객체와, 이후 `calendar.setOptions({ ... })` 로 바꿀 수 있는 옵션입니다.

| 옵션 | 기본값(문서 기준) | 무엇을 관리하는지 |
|------|------------------|-------------------|
| `defaultView` | `'week'` | 처음에 보여 줄 뷰 종류. `'month'`(월) · `'week'`(주) · `'day'`(일) 중 하나. |
| `useFormPopup` | `false` | **내장 일정 생성/수정 폼 팝업**을 쓸지 여부. `true`면 기본 제공 폼으로 이벤트를 추가·편집할 수 있음. |
| `useDetailPopup` | `false` | **내장 일정 상세 팝업**을 쓸지 여부. `true`면 셀을 눌렀을 때 상세 정보를 기본 UI로 표시. |
| `isReadOnly` | `false` | 캘린더 **전체를 읽기 전용**으로 둘지. `true`면 사용자가 일정을 새로 만들거나 편집하는 동작을 막음. |
| `usageStatistics` | `true` | NHN이 **Google Analytics로 호스트명(`location.hostname`)만** 수집해도 되는지. 내부망·프라이버시면 `false` 권장. |
| `eventFilter` | `(e) => !!e.isVisible` | 화면에 **어떤 이벤트를 그릴지 필터링**하는 함수. 기본은 `isVisible === true` 인 일정만 표시. 커스텀 시 `isVisible: false` 일정이 다시 나타날 수 있으므로 조건을 신중히 작성. |
| `week` | (내장 기본값) | **주간·일간 뷰**에서만 쓰는 세부 설정(요일 시작, 시간축 범위, 종일/시간 패널, 타임존 접기 등). 아래 `week` 항목 참고. |
| `month` | (내장 기본값) | **월간 뷰**에서만 쓰는 세부 설정(주 수, 주말 너비, 날짜 칸에 보일 일정 개수 등). 아래 `month` 항목 참고. |
| `gridSelection` | `true` | 날짜·시간 **그리드를 클릭/더블클릭해 선택**할 수 있는지. `false`면 둘 다 끔. 객체 `{ enableClick, enableDblClick }` 로 각각 제어 가능. |
| `timezone` | `{ zones: [] }` | 표시·계산에 쓸 **타임존 목록**. `zones` 배열의 **첫 번째가 기본 타임존**. 여러 개면 주/일 뷰에서 열이 나뉘어 표시. IE 등 구형 브라우저는 `Intl` 지원·폴리필 또는 `customOffsetCalculator` 필요. |
| `theme` | 기본 테마 객체 | **색·폰트·선 두께 등 스타일**. `setTheme` 으로도 변경 가능. 세부 키는 [theme 문서](https://github.com/nhn/tui.calendar/blob/main/docs/en/apis/theme.md) 참고. |
| `template` | 기본 템플릿 객체 | **날짜 칸·타임 슬롯·팝업 등 HTML 마크업을 함수로 덮어쓰기**. 이벤트 바·헤더 등을 커스터마이즈할 때 사용. [template 문서](https://github.com/nhn/tui.calendar/blob/main/docs/en/apis/template.md) 참고. |
| `calendars` | `[]` | **여러 개의 “캘린더(그룹)”** 정의. 각 항목에 `id`, `name`, `color`, `backgroundColor`, `borderColor` 등을 두고, 일정(`EventObject`)의 `calendarId` 와 연결해 색을 구분. |

#### `useFormPopup: true` 일 때 추가로 필요한 스타일

기본 이벤트 폼 팝업에는 **날짜·시간 선택 UI**가 포함되므로, 공식 문서에서는 아래 CSS를 함께 로드할 것을 안내합니다.

- `tui-date-picker` CSS  
- `tui-time-picker` CSS  

(CDN으로 각각 `uicdn.toast.com` 의 date-picker / time-picker CSS 경로를 추가하면 됩니다.)

#### `week` — 주간·일간 뷰 전용 옵션

| 옵션 | 기본값(문서) | 무엇을 관리하는지 |
|------|-------------|-------------------|
| `week.startDayOfWeek` | `0` | 한 주의 **시작 요일**. `0` = 일요일 … `6` = 토요일 (`Date.prototype.getDay` 와 동일 규칙). |
| `week.dayNames` | `[]` | 주/일 뷰에서 **요일 헤더에 붙일 이름** 7개 배열. **반드시 일~토 순서(인덱스 0=일)** 로 채움. 비어 있으면 로케일 기본 표기 사용에 가깝게 동작. |
| `week.narrowWeekend` | `false` | **주말 열 너비를 절반**으로 줄일지. 좁은 화면에서 평일 위주로 보여 줄 때 사용. |
| `week.workweek` | `false` | **`true`면 주말 열을 아예 숨김** (월~금만 표시). |
| `week.showNowIndicator` | `true` | 주/일 뷰에서 **현재 시각 선(지금 표시)** 을 그릴지. |
| `week.showTimezoneCollapseButton` | `false` | **타임존이 여러 개**일 때, 보조 타임존 열을 접었다 펼치는 **버튼을 표시할지**. |
| `week.timezonesCollapsed` | `false` | 위와 함께 쓰며, 처음부터 **보조 타임존을 접힌 상태로** 둘지. |
| `week.hourStart` | `0` | 시간 그리드의 **시작 시각(시)**. 예: 업무시간만 보려면 `9`. |
| `week.hourEnd` | `24` | 시간 그리드의 **종료 시각(시)**. 예: 18시까지면 `18`. |
| `week.eventView` | `true` | 주/일 뷰에서 **종일(allday) 패널**과 **시간(time) 패널**을 어떻게 둘지. `false`면 둘 다 숨김, `['allday']` / `['time']` 처럼 배열로 한쪽만 표시 가능. |
| `week.taskView` | `true` | **마일스톤(milestone)·태스크(task) 패널** 표시 여부. `false`면 둘 다 숨김, `['milestone']` 등으로 부분 표시. |
| `week.collapseDuplicateEvents` | `false` | 같은 시간대에 **제목·시작·종료가 같은 일정**을 겹쳐 보일 때 **하나로 접어 표시**할지. `true` 또는 `{ getDuplicateEvents, getMainEvent }` 로 묶을 규칙과 대표 일정을 직접 지정 가능. |

#### `month` — 월간 뷰 전용 옵션

| 옵션 | 기본값(문서) | 무엇을 관리하는지 |
|------|-------------|-------------------|
| `month.dayNames` | 영문 약어 7개 | 월 뷰 **요일 헤더 텍스트**(일~토 순 7요소). 한글 예: `['일','월','화','수','목','금','토']`. |
| `month.startDayOfWeek` | `0` | 월 그리드에서 **한 주가 시작하는 요일** (`0`=일 … `6`=토). |
| `month.narrowWeekend` | `false` | 월 뷰에서 **주말 열 너비를 절반**으로 할지. |
| `month.visibleWeeksCount` | `0` | 월 영역에 **몇 주 줄을 보여 줄지**. `0`이면 보통 6주에 해당하는 표시. `1`~`6`으로 고정 주 수 가능. ⚠️ 이 값을 바꾸면 **현재 날짜가 항상 첫 주에 오도록** 동작이 바뀜(문서 주의사항). |
| `month.isAlways6Weeks` | `true` | **항상 6주 높이**로 그릴지. `false`면 해당 달에 따라 **4~6주**로 높이가 달라짐. |
| `month.workweek` | `false` | **`true`면 주말 열 숨김** (월 뷰에서도 평일만). |
| `month.visibleEventCount` | `6` | 날짜 칸 하나에 **일정을 최대 몇 개까지** 펼쳐 보여 줄지. 칸 높이가 부족하면 이 값이 무시될 수 있음(문서). 테마의 `month.gridCell` 등과도 연관. |

#### `gridSelection` — 날짜·시간 선택 동작

| 형태 | 의미 |
|------|------|
| `true` / `false` | 클릭·더블클릭으로 그리드 선택 **전체 허용 또는 전체 금지**. |
| `{ enableClick, enableDblClick }` | **클릭만** 또는 **더블클릭만** 따로 켜고 끔. |

#### `timezone` — 타임존

| 하위 옵션 | 무엇을 관리하는지 |
|-----------|-------------------|
| `zones` | `{ timezoneName, displayLabel?, tooltip? }` 객체 배열. **IANA 이름**(예: `'Asia/Seoul'`). 여러 개면 주/일 뷰에서 **열이 추가**되며, 첫 번째가 기준 타임존. |
| `customOffsetCalculator` | `(timezoneName, timestamp) => 분 단위 오프셋` 형태로 **직접 UTC 대비 차이를 계산**. 기본은 브라우저 `Intl` 사용. IE 지원·외부 라이브러리(moment 등)로 오프셋을 줄 때만 권장. |

#### `theme` / `template`

- **`theme`**: 배경색, 그리드 선, 이벤트 막대 색 등 **전반적인 룩앤필**을 객체로 지정. 세부 키는 공식 theme 문서가 길므로 필요할 때만 열람.  
- **`template`**: 특정 구역의 HTML을 **함수로 재정의** (예: 월 셀 안 내용, 이벤트 블록 모양).

#### `calendars` 항목 한 줄 구조

각 원소는 하나의 **캘린더 레이어(구분)** 를 나타냅니다.

| 필드 | 역할 |
|------|------|
| `id` | 일정 데이터에서 참조할 **식별자** (`calendarId` 와 매칭). |
| `name` | UI에 보여 줄 **이름** (범례·필터 등). |
| `color` / `backgroundColor` / `borderColor` / `dragBackgroundColor` | 그 캘린더 일정의 **글자색·배경·테두리·드래그 시 배경** 등. |

#### 일정 데이터 넣는 방법 (`EventObject` + 인스턴스 메서드)

캘린더 옵션만으로 **일정 목록을 통째로 넣는 필드는 없습니다.** 인스턴스를 만든 뒤 **`createEvents([...])`** 등 메서드로 **일정 객체(`EventObject`) 배열**을 넘깁니다. 상세 필드는 공식 [EventObject](https://github.com/nhn/tui.calendar/blob/main/docs/en/apis/event-object.md), 메서드는 [Calendar 클래스](https://github.com/nhn/tui.calendar/blob/main/docs/en/apis/calendar.md) 문서를 참고하세요.

##### 1) 권장 순서

1. **`calendars`로 그룹 정의** (또는 나중에 `setCalendars([...])`). 각 일정은 반드시 하나의 `calendarId`에 속합니다.
2. **`createEvents([일정, 일정, ...])`** 로 화면에 반영. 한 건이어도 **항상 배열**로 넘깁니다.
3. 서버에서 목록을 다시 받았다면 **`clear()` 후 `createEvents(...)`** 로 갈아끼우거나, 건별로 **`updateEvent` / `deleteEvent`** 를 호출합니다.

##### 2) 컨테이너 높이

공식 문서에서는 캘린더가 들어갈 **DOM 요소에 충분한 높이**를 줄 것을 권장합니다(예: **600px 이상**).

##### 3) 일정을 넣고/바꾸고/지우는 대표 API

| 메서드 | 하는 일 |
|--------|---------|
| `createEvents(events: EventObject[])` | 일정을 **추가**. 여러 건을 한 번에 등록. |
| `getEvent(eventId, calendarId)` | 해당 일정 하나 조회. 없으면 `null`. |
| `updateEvent(eventId, calendarId, changes)` | 일정 **일부 필드만** 수정 (`changes`에 바꿀 키만 넣음). |
| `deleteEvent(eventId, calendarId)` | 일정 **삭제**. |
| `clear()` | 인스턴스에 들어 있는 **모든 일정 제거**. |
| `setCalendars([...])` | 캘린더 그룹(범례·색) 정보 갱신. |

##### 4) `EventObject` — 넣을 때 자주 쓰는 필드

일정 하나는 **평범한 JS 객체**이며, 대표적으로 아래를 채웁니다.

| 필드 | 설명 |
|------|------|
| `id` | 일정 고유 ID (문자열 권장). `getEvent`·수정·삭제 시 함께 사용. |
| `calendarId` | 위에서 정한 **캘린더 그룹 id**와 반드시 일치. |
| `title` | 제목. |
| `body` | 본문(설명). |
| `start` / `end` | 시작·종료 시각. **`Date`**, **ISO 문자열** (`'2022-06-01T10:00:00'`), **타임스탬프 숫자** 등 허용. API 반환값에는 내부 타입(`TZDate` 등)일 수 있음. |
| `isAllday` | 종일 일정 여부. |
| `category` | 공식 타입은 **`'milestone'` \| `'task'` \| `'allday'` \| `'time'`** 네 가지(패널 구분용). 임의 문자열 비권장. |
| `location`, `attendees` | 장소, 참석자 목록. |
| `state` | 공식 타입은 **`'Busy'` \| `'Free'`** 만. (다른 상태 표현은 `raw`·`template` 등으로 확장) |
| `isVisible` | `false`면 필터 기본 동작상 안 보일 수 있음 (`eventFilter` 참고). |
| `isReadOnly` | `true`면 드래그·리사이즈·편집 UI 제한 등. |
| `color`, `backgroundColor`, `dragBackgroundColor`, `borderColor` | 일정 막대 스타일. |
| `raw` | 서버 원본 JSON 등 **임의 데이터**를 실어 두고, 클릭 이벤트에서 활용 가능. |

##### `category` / `state` 는 고정값인가, 커스텀 가능한가?

- **`category`**: 공식 `EventObject` 타입에서는 **`'milestone'` \| `'task'` \| `'allday'` \| `'time'` 네 가지**만 의미가 보장됩니다. 이 값이 **마일스톤·할 일·종일·시간 등 어느 패널에 그릴지**를 나눕니다. 임의 문자열을 넣으면 원하는 영역에 안 나올 수 있으므로, 서버 코드가 다르면 **매핑해서 위 네 값 중 하나로 변환**하는 편이 안전합니다.
- **`state`**: 타입 정의는 **`'Busy'` \| `'Free'`** 두 가지입니다. “진행중·보류” 같은 **업무 전용 상태**를 넣으려면 `state`를 늘리기보다 **`raw`**에 저장하거나 **`template`** 으로 표시 문구만 바꾸는 방식을 권장합니다.
- **자유 문자열이 되는 필드 예**: **`dueDateClass`**(태스크 관련 분류, 문서상 임의 문자열 허용), **`raw`**(완전 자유·서버 DTO 보관용).

**종일 표시**는 `isAllday: true` 이거나 `category: 'allday'` 이거나, 기간이 **24시간 초과**인 경우 등 조건에 따라 종일 영역에 나타납니다(공식 문서 기준).

##### 5) 색이 적용되는 순서

일정에 직접 준 색 **>** 해당 `calendarId`의 캘린더 색 **>** 기본값.

##### 6) 서버(Spring 등)에서 받아서 넣는 예시 흐름

```javascript
// 예: GET /api/schedules → [{ id, calendarKey, title, startAt, endAt, ... }, ...]
fetch('/api/schedules')
  .then((r) => r.json())
  .then((rows) => {
    calendar.clear();
    calendar.createEvents(
      rows.map((row) => ({
        id: String(row.id),
        calendarId: row.calendarKey, // 미리 calendars / setCalendars 로 등록한 id
        title: row.title,
        start: row.startAt, // ISO 문자열이면 그대로 넘겨도 됨
        end: row.endAt,
        category: 'time',
        raw: row,
      }))
    );
  });
```

사용자가 팝업에서 일정을 만들 때는 인스턴스 이벤트 **`beforeCreateEvent`** 등에서 넘어온 객체에 `id`만 만들어 **`createEvents`** 에 넘기는 패턴이 문서에 예시로 나옵니다.

---

문서 저장소: [tui.calendar](https://github.com/nhn/tui.calendar) · [EventObject](https://github.com/nhn/tui.calendar/blob/main/docs/en/apis/event-object.md) · [Calendar API](https://github.com/nhn/tui.calendar/blob/main/docs/en/apis/calendar.md)

---

### 2.4 TOAST UI Image Editor

**용도**: 이미지 크롭·필터·그리기 등 편집기.

```html
<link rel="stylesheet" href="https://uicdn.toast.com/tui-image-editor/latest/tui-image-editor.css" />
<script src="https://uicdn.toast.com/tui-image-editor/latest/tui-image-editor.js"></script>
```

```javascript
const instance = new tui.ImageEditor(document.querySelector('#image-editor'), {
  includeUI: {
    loadImage: { path: 'img/sample.jpg', name: 'sample' },
    theme: {},
  },
  cssMaxWidth: 700,
  cssMaxHeight: 500,
  usageStatistics: false,
});
```

| 옵션 | 설명 |
|------|------|
| `includeUI` | UI 포함 여부 및 초기 이미지 |
| `cssMaxWidth` / `cssMaxHeight` | 표시 크기 제한 |
| `usageStatistics` | 통계 수집 여부 |

문서: [tui.image-editor](https://github.com/nhn/tui.image-editor)

---

### 2.5 TOAST UI Color Picker

**용도**: 색 선택 팝업.

```html
<link rel="stylesheet" href="https://uicdn.toast.com/tui.color-picker/latest/tui-color-picker.css" />
<script src="https://uicdn.toast.com/tui.color-picker/latest/tui-color-picker.js"></script>
```

```javascript
const picker = tui.colorPicker.create({
  container: document.getElementById('picker'),
  usageStatistics: false,
});
```

| 옵션 | 설명 |
|------|------|
| `container` | 붙일 DOM |
| `color` | 초기 색 |
| `usageStatistics` | 통계 수집 여부 |

문서: [tui.color-picker](https://github.com/nhn/tui.color-picker)

---

### 2.6 TOAST UI Context Menu

**용도**: 우클릭 메뉴.

```html
<link rel="stylesheet" href="https://uicdn.toast.com/tui.context-menu/latest/tui-context-menu.css" />
<script src="https://uicdn.toast.com/tui.context-menu/latest/tui-context-menu.js"></script>
```

```javascript
const menu = new tui.ContextMenu(document.body, {
  menuItems: [
    { title: '복사', command: 'copy' },
    { title: '붙여넣기', command: 'paste' },
  ],
});
```

| 옵션 | 설명 |
|------|------|
| `menuItems` | 항목 배열 (`title`, `command` 등) |

문서: [tui.context-menu](https://github.com/nhn/tui.context-menu)

---

### 2.7 TOAST UI Date Picker / Time Picker

**용도**: 날짜·시간 입력 (다른 컴포넌트나 폼과 조합).

```html
<link rel="stylesheet" href="https://uicdn.toast.com/tui.date-picker/latest/tui-date-picker.css" />
<link rel="stylesheet" href="https://uicdn.toast.com/tui.time-picker/latest/tui-time-picker.css" />
<script src="https://uicdn.toast.com/tui.date-picker/latest/tui-date-picker.js"></script>
<script src="https://uicdn.toast.com/tui.time-picker/latest/tui-time-picker.js"></script>
```

```javascript
const picker = new tui.DatePicker('#wrapper', {
  date: new Date(),
  language: 'ko',
  input: {
    element: '#datepicker-input',
    format: 'yyyy-MM-dd',
  },
});
```

| 옵션 | 설명 |
|------|------|
| `date` | 초기 날짜 |
| `language` | 로케일 |
| `input.element` / `input.format` | 입력 필드 연동 |

문서: [tui.date-picker](https://github.com/nhn/tui.date-picker)

---

## 3. 관련 링크

| 제품 | GitHub |
|------|--------|
| Grid | https://github.com/nhn/tui.grid |
| Pagination (단독·Grid 연동) | https://github.com/nhn/tui.pagination |
| Editor | https://github.com/nhn/tui.editor |
| Chart | https://github.com/nhn/tui.chart |
| Calendar | https://github.com/nhn/tui.calendar |
| Image Editor | https://github.com/nhn/tui.image-editor |
| Color Picker | https://github.com/nhn/tui.color-picker |
| Context Menu | https://github.com/nhn/tui.context-menu |
| Date Picker | https://github.com/nhn/tui.date-picker |

---

## 4. 요약

- **이 프로젝트**: `tui-grid` + `tui-pagination` (페이징 사용 시 **pagination JS를 grid JS보다 먼저**).
- **그 외 TOAST UI**: Editor, Chart, Calendar, Image Editor, Color Picker, Context Menu, Date/Time Picker 등은 **CDN 경로·전역 객체(`tui.*`, `toastui.*`)가 제품별로 다름** → 적용 전 해당 버전 공식 **Getting Started**를 확인하는 것이 안전합니다.

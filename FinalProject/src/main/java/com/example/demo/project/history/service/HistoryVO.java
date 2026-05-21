package com.example.demo.project.history.service;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

/** 히스토리 VO. 
 * <ul>
 *   <li>목록({@code selectHistoryList}): {@link #id}, {@link #tableName}, {@link #tableId}, {@link #userId}, {@link #updatedOn}, {@link #detail}</li>
 * </ul>
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryVO {
  /** 히스토리 ID({@code HISTORY.ID}) */
  private Long id;
  /** 테이블 이름({@code HISTORY.TABLE_NAME}) */
  private String tableName;
  /** 대상 PK — 이슈 ID 또는 프로젝트 ID({@code HISTORY.TABLE_ID}) */
  private Long tableId;
  /** 수정자({@code HISTORY.MEM_ID}) */
  private Long userId;
  /** 히스토리 기록 일시({@code HISTORY.UPDATED_ON}) */
  private LocalDateTime updatedOn;
  /** 설명 정보({@code HISTORY.DETAIL}) */
  private String detail;

  /** 수정자 이름 — USERS.NAME (MEM_ID 조인) */
  private String modifierName;

  /** 상세 정보({@code HISTORY.DETAIL}) */
  private List<HistoryDetailVO> historyDetailList;
}
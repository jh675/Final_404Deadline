package com.example.demo.project.history.service;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/** 히스토리 상세 VO. 
 * <ul>
 *   <li>목록({@code selectHistoryDetailList}): {@link #id}, {@link #historyId}, {@link #fieldName}, {@link #oldValues}, {@link #newValues}</li>
 * </ul>
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryDetailVO {
  /** 히스토리 상세 ID({@code HISTORY_DETAIL.ID}) */
  private Long id;
  /** 히스토리 ID({@code HISTORY.ID}) */
  private Long historyId;
  /** 필드 이름({@code HISTORY_DETAIL.FIELD_NAME}) */
  private String fieldName;
  /** 이전 값({@code HISTORY_DETAIL.OLD_VALUES}) */
  private String oldValues;
  /** 새 값({@code HISTORY_DETAIL.NEW_VALUES}) */
  private String newValues;
}

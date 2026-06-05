package com.example.demo.project.wiki.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WikiLinkSuggestVO {
	/** wiki | issue */
	private String type;
	/** 드롭다운 표시용 */
	private String label;
	/** 본문에 삽입할 텍스트 (예: [[제목]], [[#123 이슈 제목]]) */
	private String insert;
}

package com.example.demo.project.issue.service;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentOutputVO {

	private Long id;
	private Long parentId;
	private Long issueId;
	private String content;
	private Date writeDate;
	private String memId;
	/** Oracle 계층 쿼리 LEVEL (1=댓글, 2=대댓글 …) */
	private Integer depth;
}

package com.example.demo.project.issue.service;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentInputVO {

	private Long id;
	private Long parentId;
	private Long issueId;
	private String content;
	private Date writeDate;
	private Long memId;
}

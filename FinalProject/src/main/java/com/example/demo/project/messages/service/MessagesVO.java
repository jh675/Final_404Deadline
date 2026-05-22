package com.example.demo.project.messages.service;

import java.util.Date;

import lombok.Data;
@Data
public class MessagesVO {
	private	Long id;
	private Long boardId;
	private Long fieldparentId;
	private String title;
	private String content;
	private Long userId;
	private Long repliesCount;
	private Long lastReplyId;
	private Date createdOn;
	private Date updatedOn;
}

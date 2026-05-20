package com.example.demo.project.messages.service;

import java.util.Date;

import lombok.Data;
@Data
public class MessagesVO {
	private	Integer id;
	private Integer boardId;
	private Integer fieldparentId;
	private String content;
	private Integer userId;
	private Integer repliesCount;
	private Integer lastReplyId;
	private Date createdOn;
	private Date updatedOn;
}

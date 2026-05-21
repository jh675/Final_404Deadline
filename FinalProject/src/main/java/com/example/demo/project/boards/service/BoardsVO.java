package com.example.demo.project.boards.service;

import lombok.Data;
@Data
public class BoardsVO {
	private Integer id;
	private Long prjId;
	private String name;
	private String description;
	private Integer topicsCount;
}

package com.example.demo.project.boards.service;

import lombok.Data;
@Data
public class BoardsVO {
    private Long id;
    private Long prjId;
    private String name;
    private String description;
    private Long topicsCount;
}
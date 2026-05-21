package com.example.demo.project.main.service;

import lombok.Data;

@Data
public class IssueCountVO {
	private int bGoCount;
    private int bEndCount;
    private int fGoCount;
    private int fEndCount;
    private int wGoCount;
    private int wEndCount;
    private int iGoCount;
    private int iEndCount;
    private Long prjId;
}

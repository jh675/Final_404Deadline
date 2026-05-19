package com.example.demo.project.milestone.service;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneVO {

	private Long id;
	private Long prjId;
	private String name;
	private Date startDate;
	private Date endDate;
}

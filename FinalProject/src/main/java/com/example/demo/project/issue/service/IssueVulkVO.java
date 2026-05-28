package com.example.demo.project.issue.service;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueVulkVO {
	private List<Long> ids;
    private String statusCd;
    private String priorityCd;
    private String categoryCd;
    private Long updater;
}

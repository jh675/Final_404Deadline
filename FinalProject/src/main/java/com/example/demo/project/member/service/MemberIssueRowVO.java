package com.example.demo.project.member.service;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 구성원 상세 — 담당 이슈 행 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberIssueRowVO {

    private String priorityCd;
    private String subject;
    private String categoryCd;
    private String statusCd;
    private String requesterName;
    private String assigneeName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date estStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dueDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date closedDate;
    private Long doneRatio;
}

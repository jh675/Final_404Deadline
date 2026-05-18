package com.example.demo.project.member.service;

import java.time.LocalDateTime;
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
    private LocalDateTime estStartDate;
    private LocalDateTime dueDate;
    private LocalDateTime startDate;
    private LocalDateTime closedDate;
    private Long doneRatio;
}

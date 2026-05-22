package com.example.demo.project.group.service;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 그룹 상세 — 기본정보 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDetailVO {

    private Long prjId;
    private Long grpId;
    private String prjName;
    private String grpName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdOn;
}

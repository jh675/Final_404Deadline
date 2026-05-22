package com.example.demo.project.group.service;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 프로젝트 그룹 목록 한 행 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectGroupRowVO {

    private Long id;
    private String grpName;
    private Integer cntMem;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdOn;
}

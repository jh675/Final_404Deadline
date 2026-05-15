package com.example.demo.util.attach.service;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachVO {

	private Long id;//ID
	private String tableName;//테이블명
	private Long containerId;//컨테이너ID
	private String containerType;//컨테이너타입
	private String fileName;//파일명
	private String diskFileName;//디스크파일명
	private Long fileSize;//파일크기
	private String contentType;//컨텐트타입
	private Date createdOn;//생성일시
	private String diskDirectory;//디스크디렉토리
}

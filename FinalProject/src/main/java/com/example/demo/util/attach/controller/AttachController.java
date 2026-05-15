package com.example.demo.util.attach.controller;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.util.attach.service.AttachService;
import com.example.demo.util.attach.service.AttachVO;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class AttachController {
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@Autowired
	AttachService service;
	private Path uploadRoot() throws IOException {
		//업로드 경로를 가져온다
		Path root = Paths.get(uploadDir.trim()).toAbsolutePath().normalize();
		//업로드 경로가 없으면 만든다
		Files.createDirectories(root);
		//업로드 경로를 반환한다
		return root;
	}
	@GetMapping("/download/{id}")
	public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws MalformedURLException {
		// 키값을 통해 아이디 받아옴
		AttachVO attachVO = service.selectAttach(id);
		//업로드 경로에 저장된이름을 더한다
		Path filePath = Paths.get(uploadDir).resolve(attachVO.getDiskFileName()).normalize();
		//위의 경로에서 파일을 받아온다
		Resource resource = new UrlResource(filePath.toUri());
		//만약 없으면 없다고 한다
		if (!resource.exists()) {
			return ResponseEntity.notFound().build();
		}
		//다운로드 이름은 원본이름으로 바꾼다
		String downloadName = attachVO.getFileName() != null && !attachVO.getFileName().isBlank()
				? attachVO.getFileName()
				: resource.getFilename();
		//헤더에 넣을 파일명을 기재
		ContentDisposition contentDisposition = ContentDisposition.attachment()
				.filename(downloadName, StandardCharsets.UTF_8)
				.build();
		//헤더에 파일명을 기재하여 파일을 반환한다
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM) // 이진 파일
				.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString()).body(resource);
	}
}

package com.example.demo.util.attach.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.util.attach.mapper.AttachMapper;
import com.example.demo.util.attach.service.AttachService;
import com.example.demo.util.attach.service.AttachVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AttachServiceImpl implements AttachService {

	// 경로 받아오기(application.properties에있음)
	@Value("${file.upload-dir}")
	private String uploadDir;

	@Autowired
	AttachMapper mapper;

	// 업로드경로 설정
	private Path uploadRoot() throws IOException {
		Path root = Paths.get(uploadDir.trim()).toAbsolutePath().normalize();
		Files.createDirectories(root);
		return root;
	}

	// db에 파일정보 등록
	@Override
	public int insertAttach(List<AttachVO> attachList) {
		// TODO Auto-generated method stub
		try {
			for (AttachVO attachVO : attachList) {
				mapper.insertAttach(attachVO);
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e);
			return 0;
		}
		return 1;
	}

	@Override
	public List<AttachVO> selectAttachList(String tableName, Long containerId) {
		if (tableName == null || tableName.isBlank() || containerId == null) {
			return Collections.emptyList();
		}
		List<AttachVO> list = mapper.selectAttachList(tableName, containerId);
		System.out.println(list);
		return list != null ? list : Collections.emptyList();
	}

	@Override
	public int deleteAttach(Long id) {
		// TODO Auto-generated method stub
		return mapper.deleteAttach(id);
	}

	// 파일을 저장하고,정보를 추출
	@Override
	public List<AttachVO> saveAttach(MultipartFile[] attachments, String tableName) {
		// 없으면 그냥 그대로 종료
		if (attachments == null || attachments.length == 0) {
			return Collections.emptyList();
		}
		// 정보담을박스
		List<AttachVO> attachmentList = new ArrayList<>();
		// 경로
		Path root;
		// 경로설정
		try {
			root = uploadRoot();
		} catch (Exception e) {
			log.error("Failed to prepare upload directory {}: {}", uploadDir, e.getMessage());
			return attachmentList;
		}
		String diskDir = root.toString();
		// 각 첨부파일에 대해 실행
		for (MultipartFile file : attachments) {
			// 첨부파일이 비어있으면 스킵
			if (file == null || file.isEmpty()) {
				continue;
			}
			try {
				// 원본이름
				String original = file.getOriginalFilename();
				// 이름이 없는경우 file로 대체
				String baseName = (original != null && !original.isBlank()) ? new File(original).getName() : "file";
				// 중복 방지를 위해 원본이름에 밀리세컨드 단위로 붙임
				String diskFileName = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now()) + "_"
						+ baseName;
				// 정보저장
				AttachVO attachmentVO = new AttachVO();
				attachmentVO.setFileName(original != null ? original : baseName);
				attachmentVO.setFileSize(file.getSize());
				attachmentVO.setContentType(file.getContentType());
				attachmentVO.setDiskDirectory(diskDir);
				attachmentVO.setDiskFileName(diskFileName);
				attachmentVO.setTableName(tableName);
				Path destination = root.resolve(diskFileName);
				attachmentList.add(attachmentVO);
				// 파일저장
				file.transferTo(destination.toFile());
			} catch (Exception e) {
				log.error("Failed to store file {}: {}", file.getOriginalFilename(), e.getMessage());
			}
		}
		// 파일정보들 리턴
		return attachmentList;
	}

	@Override
	public AttachVO selectAttach(Long id) {
		// TODO Auto-generated method stub
		return mapper.selectAttach(id);
	}



}

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
	private Path uploadRoot(String containerType, Long containerId) throws IOException {
		Path root = Paths.get(uploadDir.trim() + "/" + containerType + "/" + containerId).toAbsolutePath().normalize();
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

	// 첨부파일 목록 조회
	@Override
	public List<AttachVO> selectAttachList(String tableName, Long containerId) {
		if (tableName == null || tableName.isBlank() || containerId == null) {
			return Collections.emptyList();
		}
		List<AttachVO> list = mapper.selectAttachList(tableName, containerId);
		return list != null ? list : Collections.emptyList();
	}
	// 첨부파일 삭제
	@Override
	public int deleteAttach(Long id) {
		// TODO Auto-generated method stub
		AttachVO attachVO= selectAttach(id);
		
		return mapper.deleteAttach(id);
	}

	// 파일을 저장하고,정보를 추출
	@Override
	public List<AttachVO> saveAttach(MultipartFile[] attachments, String containerType, Long containerId,String tableName) {
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
			root = uploadRoot(containerType, containerId);
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

	/**
	 * @param  containerId 번호
	 * @param  attachments 첨부파일(배열) 
	 * @param tableName 모듈 공통코드
	 * @param containerType 컨테이너 타입(issue,user,notice,board 등등)
	 */
	// 첨부파일 저장 및 등록
	@Override
	public void saveAndInsertAttachments(Long containerId, MultipartFile[] attachments, String tableName,
			String containerType) {
		List<AttachVO> saved = saveAttach(attachments, containerType, containerId,tableName);
		if (saved.isEmpty()) {
			return;
		}
		for (AttachVO a : saved) {
			a.setContainerId(containerId);
			a.setContainerType(containerType);
			a.setTableName(tableName);
		}
		insertAttach(saved);
	}

	// 첨부파일 조회
	@Override
	public AttachVO selectAttach(Long id) {
		// TODO Auto-generated method stub
		return mapper.selectAttach(id);
	}

	@Override
	public void removeAttach(AttachVO attachVO) throws IOException {
		// TODO Auto-generated method stub
		Path filePath = Paths.get(attachVO.getDiskDirectory()).resolve(attachVO.getDiskFileName()).normalize();
		//파일을 삭제한다
		Files.delete(filePath);
	}


	@Override
	public boolean hasAttachmentFiles(MultipartFile[] attachments) {
		if (attachments == null) {
			return false;
		}
		for (MultipartFile f : attachments) {
			if (f != null && !f.isEmpty()) {
				return true;
			}
		}
		return false;
	}

}

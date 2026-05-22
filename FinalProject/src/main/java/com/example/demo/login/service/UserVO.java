package com.example.demo.login.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVO implements UserDetails{

	private Long id;
	private String bizNo;
	private String login;
	private String password;
	private String name;
	private String tel;
	private String email;
	@JsonFormat(pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date hireDate;
	private String genderCd;
	private String genderNm;
	private String adminCd;
	private String adminNm;
	private String statusCd;
	private String statusNm;
	@JsonFormat(pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdOn;
	@JsonFormat(pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date pwUpdatedOn;
	private String mcpCd;
	private String mcpNm;
	private String prjManagerCd;
	private String prjManagerNm;
	private Date lastLogOn;
	
	private List<String> role;
	
	private String verifyNum;
	private String result;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return role.stream()
				   .map(a -> new SimpleGrantedAuthority(a))
				   .collect(Collectors.toList());
	}
	
	@Override
	public String getUsername() {
	    // 쿠키에 "123-45-67890_cadmin" 형태로 저장되도록 복합 문자열을 리턴(자동로그인 기능을 위함)
	    return this.bizNo + "_" + this.login;
	}
	
	
	
}

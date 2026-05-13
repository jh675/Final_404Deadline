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
	private String adminCd;
	private String statusCd;
	@JsonFormat(pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdOn;
	@JsonFormat(pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date pwUpdatedOn;
	private String mcpCd;
	private String prjManagerCd;
	
	private List<String> role;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return role.stream()
				   .map(a -> new SimpleGrantedAuthority(a))
				   .collect(Collectors.toList());
	}
	
	@Override
	public String getUsername() {
		return login;
	}
	
}

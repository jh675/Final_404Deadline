package com.example.demo.company.service;

import java.util.Date;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class CompanyVO {
	
	private String bizNo;
	private String companyName;
	private String tel;
	private String address;
	private String isActiveCd;
	private Date createdOn; 

}

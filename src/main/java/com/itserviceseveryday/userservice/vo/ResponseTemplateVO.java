package com.itserviceseveryday.userservice.vo;

import com.itserviceseveryday.userservice.entity.MyUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTemplateVO {
	
	private MyUser user;
	private Department department;
	

}

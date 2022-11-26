package com.itserviceseveryday.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.itserviceseveryday.userservice.entity.MyUser;
import com.itserviceseveryday.userservice.repository.UserRepository;
import com.itserviceseveryday.userservice.vo.Department;
import com.itserviceseveryday.userservice.vo.ResponseTemplateVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
//	@Autowired
//	private RestTemplate restTemplate;
	
	public MyUser saveUser(MyUser user) {
		log.info("calling saveUser of userRepository");
		return userRepository.save(user);
	}
	
	public MyUser getUserWithDepartmentById(Long userId) {
		log.info("calling getUserWithDepartmentById of userRepository");
		ResponseTemplateVO responseTemplateVO  = new ResponseTemplateVO();
		return userRepository.findByUserId(userId);
		
//		Department department = restTemplate.getForObject("http://DEPARTMENT-SERVICE/departments/"+user.getDepartmentId(), Department.class);
//		
//		responseTemplateVO.setDepartment(department);
//		responseTemplateVO.setUser(user);
//		
//		return responseTemplateVO;
	}

}

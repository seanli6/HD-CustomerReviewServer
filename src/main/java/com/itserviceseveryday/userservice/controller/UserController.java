package com.itserviceseveryday.userservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itserviceseveryday.userservice.entity.MyUser;
import com.itserviceseveryday.userservice.service.UserService;
import com.itserviceseveryday.userservice.vo.ResponseTemplateVO;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/")
	public MyUser saveUser(@RequestBody  MyUser user) {
		log.info("calling saveUser of  UserController");
		return userService.saveUser(user);
	}
	
	@GetMapping("/{id}")
	public MyUser getUserById(@PathVariable("id") Long userId) {
		log.info("calling getUserById of  UserController");
		return userService.getUserWithDepartmentById(userId);
	}

}

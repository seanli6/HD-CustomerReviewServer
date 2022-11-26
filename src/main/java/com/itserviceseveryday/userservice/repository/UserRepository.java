package com.itserviceseveryday.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itserviceseveryday.userservice.entity.MyUser;

@Repository
public interface UserRepository extends JpaRepository<MyUser, Long> {
	MyUser findByUserId(Long userId);
	
	
}

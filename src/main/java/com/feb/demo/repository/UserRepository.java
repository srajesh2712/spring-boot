package com.feb.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.feb.demo.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // That's it! Spring writes the Save, Delete, and Find methods for you.
	List<User> findByStatusAndAgeGreaterThan(String status, int age);
	
}
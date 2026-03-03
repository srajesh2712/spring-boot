package com.feb.demo.service;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feb.demo.User;
import com.feb.demo.exception.UserNotFoundException;
import com.feb.demo.repository.UserRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;

@Service
public class UserService {

	@Autowired
    private UserRepository userRepository;
	
    public User getUserDetails() {
        // Imagine this logic is more complex, like checking a database
        return null;
    }
    public User saveUser(User user) {
        return userRepository.save(user); // Saves to H2 Database
    }

    public List<User> getAllUsers() {
        return userRepository.findAll(); // Fetches all from Database
    }
    
 // Add these methods to UserService
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User updateUser(Long id, User userDetails) {
        // Find the user, change their details, and save them back
        return userRepository.findById(id).map(user -> {
            user.setName(userDetails.getName());
            user.setStatus(userDetails.getStatus());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
    }
    @Transactional
    public User registerUser(User user) {
    	User savedUser = userRepository.save(user);
    	sendWelcomeEmail(savedUser);
    	return savedUser;
    }
    
    private void sendWelcomeEmail(User user) {
        // Imagine this throws an error because the email server is down
        if (user.getName().equalsIgnoreCase("error")) {
            throw new RuntimeException("Email service failed!");
        }
        System.out.println("Email sent to: " + user.getName());
    }
    
    @CircuitBreaker(name = "userServiceBreaker", fallbackMethod = "fallbackRegister")
    public String callExternalService() {
        // Simulate a slow or failing service
        if (Math.random() > 0.5) {
            throw new RuntimeException("External Service is DOWN!");
        }
        return "Success: Data received from external service!";
    }

    // The Fallback method: This runs when the service is down or the circuit is OPEN
    public String fallbackRegister(Exception e) {
        return "System is currently busy. Please try again in 10 seconds. (Reason: " + e.getMessage() + ")";
    }
    
}
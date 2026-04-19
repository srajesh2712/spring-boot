package com.feb.demo.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.feb.demo.User;
import com.feb.demo.customannotations.LogExecutionTime;
import com.feb.demo.kafka.KafkaProducer;
import com.feb.demo.service.UserService;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
public class HelloController {

	@Autowired
	private UserService userService;

	// @Autowired
	private KafkaProducer kafkaProducer;

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/machine-learning")
	public String returnName(@RequestParam(value = "name", defaultValue = "Machine Learning") String name) {
		return String.format("Hello there , Machine Learning Engineer %s !", name);
	}

	@GetMapping("/users/{id}")
	public User getOne(@PathVariable Long id) {
		System.out.println(" Inside Get one method ");
		return userService.getUserById(id);
	}

	@GetMapping("/user")
	public User getUser() {
		// We are returning an actual Java Object here, not a String!
		return userService.getUserDetails();
	}

	@PostMapping("/add-user")
	public User add(@RequestBody User user) {

		return userService.saveUser(user);
	}

	@PostMapping("/register")
	public User registerUser(@RequestBody User user) {

		return userService.registerUser(user);
	}

	@GetMapping("/circuit-breaker")
	public String externalService() {

		return userService.callExternalService();
	}

	@GetMapping("/all-users")
	@LogExecutionTime
	@RateLimiter(name = "userApiLimiter", fallbackMethod = "rateLimiterFallback")
	public List<User> all() {
		return userService.getAllUsers();
	}

	// This runs when the user exceeds the 3-request limit
	public List<User> rateLimiterFallback(RequestNotPermitted exception) {
		// In a real app, you might throw a custom error or return a "dummy" object
		System.out.println("Rate limit exceeded ");
		ArrayList<User> ar = new ArrayList<User>();
		ar.add(new User(null, "Rate Limit Exceeded", 0, "Blocked"));
		return ar;
	}

	// DELETE a user: http://localhost:8080/users/1
	@DeleteMapping("/users/{id}")
	public String delete(@PathVariable Long id) {
		userService.deleteUser(id);
		return "User " + id + " deleted successfully!";
	}

	// UPDATE a user: http://localhost:8080/users/1
	@PutMapping("/users/{id}")
	public User update(@PathVariable Long id, @RequestBody User user) {
		return userService.updateUser(id, user);
	}

	private final List<String> teams = Arrays.asList("Manchester-United", "Liverpool");
	private final Random random = new Random();

	@GetMapping("/sendmessage")
	public void sendMessage() {
		String teamName = teams.get(random.nextInt(teams.size()));

		// 2. Create a "Goal" message (the value doesn't matter much for counting)
		String messageValue = "Goal Scored!";
		kafkaProducer.sendMessage(teamName, messageValue);
	}

}

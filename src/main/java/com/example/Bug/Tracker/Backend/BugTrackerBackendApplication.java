package com.example.Bug.Tracker.Backend;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;


import com.example.Bug.Tracker.Backend.User.User;
import com.example.Bug.Tracker.Backend.User.UserJPARepository;


@SpringBootApplication
public class BugTrackerBackendApplication {
	
	@Autowired 
	private UserJPARepository UJR;
	
	@Autowired
	private PasswordEncoder encoder;
	
	
	
	public static void main(String[] args) {
		SpringApplication.run(BugTrackerBackendApplication.class, args);
	}
	
	
	
	
	//Placeholder for default Admin Account.
	/*
	 * Use it if no Admin exist
	@Bean
	CommandLineRunner commandLineRunner() {
		
		
		
		return args -> {
			
			
			
			UJR.save(new User(0, "Morenight",encoder.encode("Abreu"),"Admin"));
			
			
			
		};
	}
	*/
	
	
	
	
	
	

}

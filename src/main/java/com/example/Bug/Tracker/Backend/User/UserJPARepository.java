package com.example.Bug.Tracker.Backend.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserJPARepository extends JpaRepository<User, Integer> {
	public Optional<User> findByusername(String username);
	public List<User> findByroles(String roles);

	
}

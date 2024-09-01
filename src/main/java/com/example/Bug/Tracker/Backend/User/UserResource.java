package com.example.Bug.Tracker.Backend.User;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.Bug.Tracker.Backend.Projects.Project;
import com.example.Bug.Tracker.Backend.Projects.ProjectJPARepository;






@RestController
public class UserResource {
	
	
	@Autowired
	UserJPARepository UJR;
	
	@Autowired
	ProjectJPARepository PJR;
	
	@Autowired
	private PasswordEncoder encoder;
	
	
	
	

	
	
	
	@GetMapping("/users")
	public List<User> ListUsers() {
		return UJR.findAll();
	}
	
	@GetMapping("/currentUser")
	public Optional<User> getCurrentUser(){
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return UJR.findByusername(authentication.getName());
	}
	
	@GetMapping("/user/{id}")
	public Optional<User> getUser(@PathVariable int id) {
		return UJR.findById(id);
	}
	
	@GetMapping("/user/admins")
	public List<User> getAdmins(){
		return UJR.findByroles("Admin");
	}
	
	
	
	
	
	
	
	
	@PostMapping("/register")
	public ResponseEntity<Object> register(@Validated @RequestBody User newUser){
		
		 
		
		if(!UJR.findByusername(newUser.getUsername()).isEmpty()) {
			
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		
		newUser.setPassword(encoder.encode(newUser.getPassword()));
		newUser.setRoles("User");
		UJR.save(newUser);
		
		
		
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/user/{id}").buildAndExpand(newUser.getId()).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@PutMapping("/updateUser")
	public ResponseEntity<Object> update(@Validated @RequestBody User updateUser){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = UJR.findByusername(authentication.getName());
		
		
		if(user.isEmpty() || !UJR.findByusername(updateUser.getUsername()).isEmpty() || !authentication.isAuthenticated()) {
			
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		
		user.get().setUsername(updateUser.getUsername());
		user.get().setPassword(encoder.encode(updateUser.getPassword()));
		
		
		Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                updateUser.getUsername(),authentication.getCredentials(), authentication.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(newAuthentication);
		
		UJR.save(user.get());
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/user/{id}").buildAndExpand(user.get().getId()).toUri();
		return ResponseEntity.created(location).build();
		
	}
	
	
	@PutMapping("/AssignRole/{id}")
	@PreAuthorize("hasRole('ROLE_Admin')")
	public ResponseEntity<Object> assignUserRole(@PathVariable int id, @Validated @RequestBody String role){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = UJR.findByusername(authentication.getName());
		Optional<User> userAssigned = UJR.findById(id);
		if(user.isEmpty() || userAssigned.isEmpty()){
			
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		if(!user.get().getRoles().equals("Admin") || !isValidRole(role)) {
			
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		
	
		
	
		userAssigned.get().setRoles(role);
		UJR.save(userAssigned.get());
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@DeleteMapping("/deleteUser/{id}")
	@PreAuthorize("hasRole('ROLE_Admin')")
	public ResponseEntity<Object> deleteUser(@PathVariable int id){
		Optional<User> user = UJR.findById(id);
		List<Project> project = PJR.findByUserRoleKey(id);
		
		
		
		if(!user.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		for(Project projects : project) {
			if(projects.getProjectOwnerId() == id) {
				//Giving ownership to main owner
				projects.setProjectOwnerId(0);
			}
			projects.getUserRole().remove(id);
			PJR.save(projects);
		}
		
		UJR.delete(user.get());
		
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	public boolean isValidRole(String role) {
		return role.equals("Admin") || role.equals("Mod") 
				|| role.equals("Developer") || role.equals("User");
	}

}

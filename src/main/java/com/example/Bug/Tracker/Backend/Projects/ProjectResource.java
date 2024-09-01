package com.example.Bug.Tracker.Backend.Projects;



import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.Bug.Tracker.Backend.Tickets.TicketCommentJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.TicketFile;
import com.example.Bug.Tracker.Backend.Tickets.TicketHistoryJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.TicketJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.Tickets;
import com.example.Bug.Tracker.Backend.User.User;
import com.example.Bug.Tracker.Backend.User.UserJPARepository;
import com.example.Bug.Tracker.Backend.User.UserRetrieval;
import com.example.Bug.Tracker.Backend.helper.MethodsHelper;

import jakarta.validation.constraints.Size;

@RestController
public class ProjectResource {
	
	@Autowired
	ProjectJPARepository PJR;
	
	@Autowired
	TicketCommentJPARepository TCR;
	
	@Autowired
	TicketHistoryJPARepository THR;
	
	@Autowired
	TicketJPARepository TJR;
	
	@Autowired
	UserJPARepository UJR;
	
	
	
	
	@GetMapping("/getProjects")
	@PreAuthorize("hasAnyRole('ROLE_Admin', 'ROLE_Mod')")
	public List<Project> getProjects(){
		return PJR.findAll();
	}
	
	@GetMapping("/getProject/{projectid}")
	public Project getProject(@PathVariable int projectid){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = UJR.findByusername(authentication.getName());
		Optional<Project> project = PJR.findById(projectid);
		
		if(MethodsHelper.hasAuthorizedRoles(user.get().getRoles())) {
			return project.get();
		}
		
		if(user.isEmpty() || !MethodsHelper.isUserInProject(project.get(), user.get().getId()) ) {
			return null;
		}
		
		return project.get();
	}
	
	@GetMapping("/projectTickets/{projectid}")
	public List<Tickets> projectTickets(@PathVariable int projectid){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = UJR.findByusername(authentication.getName());
		Optional<Project> project = PJR.findById(projectid);
		List<Tickets> tickets = new LinkedList<Tickets>();
		
		
		
		for(Integer ticketId : project.get().getTickets()){
			Optional<Tickets> ticket = TJR.findById(ticketId);
			tickets.add(ticket.get());
			
		}
		
		if(MethodsHelper.hasAuthorizedRoles(user.get().getRoles())) {
			return tickets;
		}
		
		if(!MethodsHelper.isUserInProject(project.get(), user.get().getId())) {
			return null;
		}
		
		return tickets;
	}
	
	@GetMapping("/getUserProjects/{userid}")
	public List<Project> getUserProjects(@PathVariable int userid){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> u = UJR.findByusername(authentication.getName());
		
		
		if(!MethodsHelper.hasAuthorizedRoles(u.get().getRoles()) || u.get().getId() != userid) {
			return null;
		}
		
		
		List<Project> userProjects = PJR.findByUserRoleKey(userid);
		
	
		
		return userProjects;
	}
	
	
	
	@GetMapping("/getMembers/{projectid}")
	public List<UserRetrieval> getMembers(@PathVariable int projectid){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = UJR.findByusername(authentication.getName());
		Optional<Project> project = PJR.findById(projectid);
		List<UserRetrieval> members = new LinkedList<UserRetrieval>();
		
		for(Integer userId : project.get().getUserRole().keySet()) {
			Optional<User> userRetrieved = UJR.findById(userId);
			UserRetrieval UR = new UserRetrieval(userRetrieved.get().getId()
					, userRetrieved.get().getUsername()
					, project.get().getUserRole().get(userRetrieved.get().getId()));
			members.add(UR);
		}
		
		if(MethodsHelper.hasAuthorizedRoles(user.get().getRoles())) {
			return members;
		}
		
		if(!MethodsHelper.isUserInProject(project.get(), user.get().getId())) {
			return null;
		}
		
		return members;	
		
	}
	
	@GetMapping("/getMember/{userid}/{projectid}")
	public UserRetrieval getMembers(@PathVariable int userid, @PathVariable int projectid){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = UJR.findByusername(authentication.getName());
		Optional<Project> project = PJR.findById(projectid);
		Optional<User> foundUser = UJR.findById(userid);
		
		
		
		
		if(!MethodsHelper.isUserInProject(project.get(), foundUser.get().getId())) {
			return null;
		}
		
		UserRetrieval UR = new UserRetrieval(foundUser.get().getId()
				, foundUser.get().getUsername()
				, project.get().getUserRole().get(foundUser.get().getId()));
				
		
		
		if(MethodsHelper.hasAuthorizedRoles(user.get().getRoles())) {
			return UR;
		}
		
		if(!MethodsHelper.isUserInProject(project.get(), user.get().getId())) {
			return null;
		}
		
		return UR;	
		
	}
	
	@GetMapping("/authorizedMember/{projectid}")
	public List<UserRetrieval> getAuthorizedMembers(@PathVariable int projectid){
		Optional<Project> project = PJR.findById(projectid);
		List<UserRetrieval> users = new LinkedList<UserRetrieval>();
		
		for(Integer userId : project.get().getUserRole().keySet()) {
			Optional<User> user = UJR.findById(userId);
			
			if(MethodsHelper.hasAuthorizedRoles(project.get().getUserRole().get(userId))) {
				UserRetrieval UR = new UserRetrieval(user.get().getId(), user.get().getUsername()
						, user.get().getRoles());
				users.add(UR);
			}
			
		}
		
		return users;
		
	}
	
	@GetMapping("/developers/{projectid}")
	public List<UserRetrieval> getDevelopers(@PathVariable int projectid){
		Optional<Project> project = PJR.findById(projectid);
		List<UserRetrieval> users = new LinkedList<UserRetrieval>();
		
		for(Integer userId : project.get().getUserRole().keySet()) {
			Optional<User> user = UJR.findById(userId);
			
			if(MethodsHelper.isDeveloper(project.get().getUserRole().get(userId))) {
				UserRetrieval UR = new UserRetrieval(user.get().getId(), user.get().getUsername()
						, user.get().getRoles());
				users.add(UR);
			}
			
		}
		
		return users;
	}
	
	
	

	
	
	@PostMapping("/createProject")
	@PreAuthorize("hasAnyRole('ROLE_Admin', 'ROLE_Mod')")
	public ResponseEntity<Object> createProject(@Validated @RequestBody Project project){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> u = UJR.findByusername(authentication.getName());
		
		if(u.isEmpty() || !u.get().getRoles().equals("Admin")) {
			
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		
		project.getUserRole().put(u.get().getId(), "Admin");
		
		project.setProjectOwnerId(u.get().getId());
		
		PJR.save(project);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	
	
	@PutMapping("/changeProjectName/{projectid}")
	public ResponseEntity<Object> changeProjectName(@PathVariable int projectid, @Validated @RequestBody 
		@Size(min = 5, message = "The parameter must be at least 5 characters long") String newName){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> u = UJR.findByusername(authentication.getName());
		Optional<Project> project = PJR.findById(projectid);
		
		if(u.isEmpty() || project.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		
		if(project.get().getProjectOwnerId() != u.get().getId() && !u.get().getRoles().equals("Admin")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		
		project.get().setProjectName(newName);
		PJR.save(project.get());
		
		
		return ResponseEntity.status(HttpStatus.OK).build();
		
		
	}
	
	@PutMapping("/addUser/{userid}/{projectid}")
	public ResponseEntity<Object> addUser(@PathVariable int userid, @PathVariable int projectid){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> u = UJR.findByusername(authentication.getName());
		Optional<User> u2 = UJR.findById(userid);
		Optional<Project> project = PJR.findById(projectid);
		
		if(u.isEmpty() || project.isEmpty() || u2.isEmpty()) {
			
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		if(MethodsHelper.isUserInProject(project.get(), userid)) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
		}
		
		
		if(!MethodsHelper.hasAuthorizedRoles(u.get().getRoles()) 
				&& !MethodsHelper.hasAuthorizedRoles(project.get().getUserRole().get(u.get().getId()))) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			
		}
		
		if(project.get().getUserRole().containsKey(userid)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		
		
	
		project.get().getUserRole().put(u.get().getId(), "User");
		PJR.save(project.get());
		
		return ResponseEntity.status(HttpStatus.OK).build();
		
		
	}
	
	@PutMapping("/kickUser/{userid}/{projectid}")
	public ResponseEntity<Object> kickUser(@PathVariable int userid, @PathVariable int projectid){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> u = UJR.findByusername(authentication.getName());
		Optional<Project> project = PJR.findById(projectid);
		
		if(!project.get().getUserRole().containsKey(u.get().getId()) ||
				project.isEmpty() || u.isEmpty() || !MethodsHelper.isUserInProject(project.get(), userid)) {
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		
		if(userid == project.get().getProjectOwnerId()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if((!MethodsHelper.hasAuthorizedRoles(project.get().getUserRole().get(u.get().getId())) 
				&& !MethodsHelper.hasAuthorizedRoles(u.get().getRoles()))) {
			
			
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		project.get().setTickets(new HashSet<Integer>());
		project.get().setUserRole(new HashMap<Integer, String>());
		project.get().getUserRole().remove(u.get().getId());
		PJR.save(project.get());
		
		
		return ResponseEntity.status(HttpStatus.OK).build();
		
		
	}
	
	@PutMapping("/changeRole/{userid}/{projectid}")
	public ResponseEntity<Object> changeRole(@PathVariable int userid, @PathVariable int projectid, @Validated @RequestBody String newRole){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> u = UJR.findByusername(authentication.getName());
		Optional<Project> project = PJR.findById(projectid);
		
		if(u.isEmpty() || userid == project.get().getProjectOwnerId()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if(project.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		if(!MethodsHelper.isUserInProject(project.get(), u.get().getId())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if(!MethodsHelper.hasAuthorizedRoles(u.get().getRoles()) &&
				!MethodsHelper.hasAuthorizedRoles(project.get().getUserRole().get(u.get().getId()))) {
			
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		
		if(!MethodsHelper.isValidRole(newRole)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		project.get().getUserRole().put(userid, newRole);
		PJR.save(project.get());
		
		
		return ResponseEntity.status(HttpStatus.OK).build();
		
		
	}
	
	@PutMapping("/changeOwner/{userid}/{projectid}")
	@PreAuthorize("hasRole('ROLE_Admin')")
	public ResponseEntity<Object> changeOwner(@PathVariable int userid, @PathVariable int projectid){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> u = UJR.findByusername(authentication.getName());
		Optional<Project> project = PJR.findById(projectid);
		
		if(u.isEmpty()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if(project.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		if(!MethodsHelper.hasAuthorizedRoles(u.get().getRoles())) {
			
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		
		project.get().setProjectOwnerId(userid);
		project.get().getUserRole().put(userid, "Admin");
		PJR.save(project.get());
		
		
		return ResponseEntity.status(HttpStatus.OK).build();
		
		
	}
	
	
	@DeleteMapping("/deleteProject/{projectid}")
	public ResponseEntity<Object> deleteProject(@PathVariable int projectid){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> u = UJR.findByusername(authentication.getName());
		Optional<Project> project = PJR.findById(projectid);
		
		if(u.isEmpty() || project.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		
		if(!MethodsHelper.hasAdminRole(u.get().getRoles()) 
				&& project.get().getProjectOwnerId() != u.get().getId()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		
		for(Integer t : project.get().getTickets()) {
			Optional<Tickets> ticket = TJR.findById(t);
			for(Integer th : ticket.get().getTicketHistory()) {
				THR.deleteById(th);
			}
			
			for(Integer tc : ticket.get().getTicketComments()) {
				TCR.deleteById(tc);
			}
			for(TicketFile tf : ticket.get().getFiles()) {
				tf.deleteFile();
			}
			
			TJR.deleteById(t);
		}
		
		return ResponseEntity.status(HttpStatus.OK).build();
		
	}
	
	
	
	

}

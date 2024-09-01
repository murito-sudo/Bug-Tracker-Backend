package com.example.Bug.Tracker.Backend.Tickets;

import java.net.URI;
import java.time.ZonedDateTime;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.Bug.Tracker.Backend.Projects.Project;
import com.example.Bug.Tracker.Backend.Projects.ProjectJPARepository;
import com.example.Bug.Tracker.Backend.User.User;
import com.example.Bug.Tracker.Backend.User.UserJPARepository;

@RestController
public class TicketCommentResource {
	
	@Autowired
	TicketCommentJPARepository TCR;
	
	@Autowired
	TicketHistoryJPARepository THR;
	
	@Autowired
	TicketJPARepository TJR;
	
	@Autowired
	UserJPARepository UJR;
	
	@Autowired
	ProjectJPARepository PJR;
	
	

	
	@GetMapping("/getComment/{commentid}")
	public TicketComment getComment(@PathVariable int commentid) {
		
		Optional<TicketComment> comment = TCR.findById(commentid);
		
		return comment.get();
	}

	
	@PostMapping("/addComment/{ticketid}")
	public ResponseEntity<Object> addComment(@PathVariable int ticketid, @Validated @RequestBody TicketComment tc){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication == null) {
			return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		Optional<User> user = UJR.findByusername(authentication.getName());
		Optional<Tickets> ticket = TJR.findById(ticketid);
		
		
		
		
		if(user.isEmpty() || ticket.isEmpty()){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		Optional<Project> project = PJR.findById(ticket.get().getProject());
		
		if(project.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
			
		
		if(!isUserInProject(project, user.get().getId())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		
		
		tc.setCommenterId(user.get().getId());
		tc.setCreated(ZonedDateTime.now());
		
		TCR.save(tc);
		ticket.get().getTicketComments().add(tc.getCommentId());
		TJR.save(ticket.get());
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/getComment/{commentid}").buildAndExpand(tc.getCommentId()).toUri();
		return ResponseEntity.created(location).build();
		
	}
	
	
	
	public boolean isUserInProject(Optional<Project> project, int id) {
		return project.get().getUserRole().containsKey(id);
	}
	
	
}

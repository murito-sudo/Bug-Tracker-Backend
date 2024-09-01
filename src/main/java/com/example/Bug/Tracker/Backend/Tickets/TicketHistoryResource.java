package com.example.Bug.Tracker.Backend.Tickets;


import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.example.Bug.Tracker.Backend.Projects.Project;
import com.example.Bug.Tracker.Backend.Projects.ProjectJPARepository;
import com.example.Bug.Tracker.Backend.User.User;
import com.example.Bug.Tracker.Backend.User.UserJPARepository;

@RestController
public class TicketHistoryResource {
	@Autowired
	TicketHistoryJPARepository THR;
	
	@Autowired
	TicketJPARepository TJR;
	
	@Autowired
	UserJPARepository UJR;
	
	@Autowired
	ProjectJPARepository PJR;
	
	
	
	
	//This method will not be used for the current version.
	@DeleteMapping("/deleteHistory/{tHistoryid}")
	public ResponseEntity<Object> deleteTicketHistory(@PathVariable int tHistoryid){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = UJR.findByusername(authentication.getName());
		
		if(user.isEmpty()){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		Optional<TicketHistory> ticketHistory = THR.findById(tHistoryid);
		Optional<Tickets> ticket = TJR.findById(ticketHistory.get().getTicketId());
		Optional<Project> project = PJR.findById(ticket.get().getProject());
		
		if(ticketHistory.isEmpty() || ticket.isEmpty() || project.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		if(!project.get().getTickets().contains(tHistoryid)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if(!user.get().getRoles().equals("Admin") 
				|| !project.get().getUserRole().containsKey(user.get().getId())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if(!project.get().getUserRole().get(user.get().getId()).equals("Admin") 
				|| project.get().getProjectOwnerId() != user.get().getId()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		THR.delete(ticketHistory.get());
		ticket.get().getTicketHistory().remove(tHistoryid);
		TJR.save(ticket.get());
		
		
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}

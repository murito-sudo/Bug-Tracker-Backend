package com.example.Bug.Tracker.Backend.Tickets;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Bug.Tracker.Backend.Projects.Project;
import com.example.Bug.Tracker.Backend.Projects.ProjectJPARepository;
import com.example.Bug.Tracker.Backend.User.User;
import com.example.Bug.Tracker.Backend.User.UserJPARepository;
import com.example.Bug.Tracker.Backend.helper.MethodsHelper;

@RestController
public class TicketResource {
	
	@Autowired
	TicketJPARepository TJR;
	
	
	@Autowired
	UserJPARepository UJR;
	
	@Autowired
	ProjectJPARepository PJR;
	
	@Autowired
	TicketHistoryJPARepository THR;
	
	@Autowired
	TicketCommentJPARepository TCR;
	
	 private static String UPLOAD_DIR = "uploads/";
	
	@GetMapping("/ticket/{ticketid}")
	public Tickets seeTicket(@PathVariable int ticketid) {
		Optional<Tickets> t = TJR.findById(ticketid);
		return t.get();
	}
	
	
	
	@GetMapping("/ticketComment/{ticketid}")
	public List<TicketComment> ticketComments(@PathVariable int ticketid) {
		Optional<Tickets> ticket = TJR.findById(ticketid);
		List<TicketComment> ticketComments = new LinkedList<TicketComment>();
		
		for(Integer ticketCommentId : ticket.get().getTicketComments()) {
			Optional<TicketComment> ticketComment = TCR.findById(ticketCommentId); 
			ticketComments.add(ticketComment.get());
		}
		
		return ticketComments;
	}
	
	@GetMapping("/ticketHistories/{ticketid}")
	public List<TicketHistory> ticketHistories(@PathVariable int ticketid) {
		Optional<Tickets> ticket = TJR.findById(ticketid);
		List<TicketHistory> ticketHistories = new LinkedList<TicketHistory>();
		
		for(Integer ticketHistoryId : ticket.get().getTicketHistory()) {
			Optional<TicketHistory> ticketHistory = THR.findById(ticketHistoryId); 
			ticketHistories.add(ticketHistory.get());
		}
		
		return ticketHistories;
	}
	
	@GetMapping("/ticketFiles/{ticketid}")
	public List<TicketFile> ticketFiles(@PathVariable int ticketid) {
		Optional<Tickets> ticket = TJR.findById(ticketid);
	
		return ticket.get().getFiles();
	}
	
	
	
	@PostMapping("/createTicket/{projectid}")
	public ResponseEntity<Object> createTicket(@Validated @RequestBody Tickets ticket, @PathVariable int projectid){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> uDeveloper = UJR.findById(ticket.getDeveloperId());
		Optional<User> uSubmitter = UJR.findByusername(authentication.getName());
		Optional<Project> project = PJR.findById(ticket.getProject());
		
		
		
		if(uDeveloper.isEmpty() || uSubmitter.isEmpty() || project.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		
		if(!MethodsHelper.isUserInProject(project.get(), uDeveloper.get().getId())  
				|| !project.get().getUserRole().get(uDeveloper.get().getId()).equals("Developer") 
				|| !MethodsHelper.isUserInProject(project.get(), uSubmitter.get().getId()) 
				|| authentication == null) {
			
			
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		ticket.setProject(projectid);
		ticket.setSubmitterId(uSubmitter.get().getId());
		ticket.setDeveloperId(uDeveloper.get().getId());
		ticket.setStatus(1);
		ticket.setCreatedDate(LocalDateTime.now());
		ticket.setTicketHistory(new LinkedList<Integer>());
		ticket.setTicketComments(new LinkedList<Integer>());
		ticket.setUploadedFiles(new LinkedList<TicketFile>());
		
		
		
		TJR.save(ticket);
		
		project.get().getTickets().add(ticket.getId());
		PJR.save(project.get());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	
	
	
	@PutMapping("/updateTicket/{ticketid}")
	public ResponseEntity<Object> updateTicket(@PathVariable int ticketid, @Validated @RequestBody TicketUpdateRequest updateRequest) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    Optional<User> userOpt = UJR.findByusername(authentication.getName());
	    Optional<Tickets> ticketOpt = TJR.findById(ticketid);
	    Optional<User> devOpt = UJR.findById(updateRequest.getDeveloperId());
	    
	    // Check if required values are present
	    if (userOpt.isEmpty() || ticketOpt.isEmpty() || devOpt.isEmpty() ) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }

	    User user = userOpt.get();
	    Tickets ticket = ticketOpt.get();
	    
	    // Check if project is present
	    Optional<Project> projectOpt = PJR.findById(ticket.getProject());
	    if (projectOpt.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }

	    Project project = projectOpt.get();
	    
	    // Check authorization
	    if (ticket.getSubmitterId() != user.getId() 
	        && project.getProjectOwnerId() != user.getId()
	        && !MethodsHelper.hasAuthorizedRoles(user.getRoles())) {
	        
	        if (!MethodsHelper.isUserInProject(project, user.getId())
	            || !MethodsHelper.hasAuthorizedRoles(project.getUserRole().get(user.getId()))) {
	            
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	        }
	    }

	    // Check developer authorization
	    if (!MethodsHelper.isUserInProject(project, updateRequest.getDeveloperId()) 
	        || !project.getUserRole().get(updateRequest.getDeveloperId()).equals("Developer")) {
	        
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	    }

	    // Process changes
	    
	    checkChanges(ticketOpt.get(), updateRequest); 
	    
	    ticket.setDeveloperId(updateRequest.getDeveloperId()); 
	    ticket.setTicketTitle(updateRequest.getTitle());
	    ticket.setTicketDescription(updateRequest.getDesc());
	    ticket.setPriority(updateRequest.getPriority());
	    ticket.setStatus(updateRequest.getStatus());
	    ticket.setType(updateRequest.getBug());

	    TJR.save(ticket);
	    return ResponseEntity.status(HttpStatus.OK).build();
	}

     
	
	
	
	@PutMapping("/openTicket/{ticketid}")
	public ResponseEntity<Object> openTicket(@PathVariable int ticketid){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = UJR.findByusername(authentication.getName());
		Optional<Tickets> ticket = TJR.findById(ticketid);
		Optional<Project> project = PJR.findById(ticket.get().getProject());
		
		if(authentication == null || user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if(ticket.isEmpty() || project.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		if(isTicketOpen(ticket.get().getStatus())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		
		if(MethodsHelper.hasAuthorizedRoles(user.get().getRoles()) 
				|| user.get().getId() == project.get().getProjectOwnerId() 
				|| MethodsHelper.isUserInProject(project.get(), ticket.get().getSubmitterId()) 
						&& MethodsHelper.hasAuthorizedRoles(project.get().getUserRole().get(user.get().getId()))) {
			
			createTicketHistory(ticket.get(), 
					"ReopenTicket", String.valueOf(ticket.get().getStatus())
			, String.valueOf(1));
			ticket.get().setStatus(1);
			TJR.save(ticket.get());
			
		}else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			
		}
		
		return ResponseEntity.status(HttpStatus.OK).build();
		
		
	}
	
	
	@PutMapping("/addFile/{ticketid}")
	public ResponseEntity<Object> addFile(@RequestParam("file") MultipartFile multipartFile, @PathVariable int ticketid){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = UJR.findByusername(authentication.getName());
		Optional<Tickets> ticket = TJR.findById(ticketid);
		Optional<Project> project = PJR.findById(ticket.get().getProject());
		
		UPLOAD_DIR = String.valueOf(ticket.get().getId()) + "/";
		
		if(user.isEmpty() || ticket.isEmpty() || project.isEmpty()) {
			return new ResponseEntity<>("404 not found", HttpStatus.FORBIDDEN);
			
		}
		
		
		
		if((ticket.get().getSubmitterId() != user.get().getId() 
				&& ticket.get().getDeveloperId() != user.get().getId()) 
				|| !MethodsHelper.isUserInProject(project.get(), user.get().getId())) {
			if(!MethodsHelper.hasAuthorizedRoles(user.get().getRoles()) 
					&& !MethodsHelper.hasAuthorizedRoles(project.get().getUserRole().get(user.get().getId()))) {
				 return new ResponseEntity<>("User not authorized", HttpStatus.FORBIDDEN);
			}
		}
		
		File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

      

        try {
        	TicketFile newFile = new TicketFile();
        	newFile.setUploader(user.get().getUsername());
        	newFile.setNote("NotES");
        	newFile.setCreated(ZonedDateTime.now());
        	newFile.saveUploadedFile(multipartFile, UPLOAD_DIR + user.get().getId() + "/");
        
            return new ResponseEntity<>("File uploaded and saved successfully: " + newFile.getUploadedFile().getName(), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload file: " + multipartFile.getOriginalFilename(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
		
		
	}
	
	
	
	@DeleteMapping("/deleteTicket/{ticketid}")
	public ResponseEntity<Object> deleteTicket(@PathVariable int ticketid){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = UJR.findByusername(authentication.getName());
		Optional<Tickets> ticket = TJR.findById(ticketid);
		Optional<Project> project = PJR.findById(ticket.get().getProject());
		
		if(authentication == null || user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if(ticket.isEmpty() || project.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		
		if(ticket.get().getSubmitterId() == user.get().getId() || project.get().getProjectOwnerId() == user.get().getId() || 
				MethodsHelper.hasAuthorizedRoles(user.get().getRoles()) || 
				(project.get().getUserRole().containsKey(user.get().getId()) 
				&& MethodsHelper.hasAuthorizedRoles(project.get().getUserRole().get(user.get().getId())))) {
			project.get().getTickets().remove(ticketid);
			
		
				
			for(Integer th : ticket.get().getTicketHistory()) {
				THR.deleteById(th);
			}
				
			for(Integer tc : ticket.get().getTicketComments()) {
				TCR.deleteById(tc);
			}
			
			for(TicketFile tf : ticket.get().getUploadedFiles()) {
				tf.deleteFile();
			}
		
			TJR.delete(ticket.get());
			
			
		}else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		
		
		
		return ResponseEntity.status(HttpStatus.OK).build();
		
	}

	
	public boolean isTicketOpen(int x) {
		return x == 1;
	}
	
	
	public void createTicketHistory(Tickets ticket, String property, String oldVal, String newVal) {
		TicketHistory th = new TicketHistory();
		th.setProperty(property);
		th.setOldVal(oldVal);
		th.setNewVal(newVal);
		th.setUpdateDate(ZonedDateTime.now());
		THR.save(th);
		ticket.getTicketHistory().add(th.getTicketHistoryId());
		TJR.save(ticket);
		
	}
	
	public void checkChanges(Tickets ticket, TicketUpdateRequest tur) {
		if(ticket.getDeveloperId() != tur.getDeveloperId()) {
			createTicketHistory(ticket, "AssignedNewDeveloper", 
					MethodsHelper.getUserName(ticket.getDeveloperId()), MethodsHelper.getUserName(tur.getDeveloperId()));
			
		}
		
		if(ticket.getTicketTitle() != tur.getTitle()) {
			createTicketHistory(ticket, "AssignedNewTitle", ticket.getTicketTitle()
					,tur.getTitle());
		}
		
		if(ticket.getTicketDescription() != tur.getDesc()) {
			createTicketHistory(ticket, "AssignedNewDescription", ticket.getTicketDescription()
					,tur.getDesc());
		}
		
		if(ticket.getPriority() != tur.getPriority()) {
			createTicketHistory(ticket, "AssignedNewPriority", String.valueOf(ticket.getPriority())
					, String.valueOf(tur.getPriority()));
		}
		
		if(ticket.getStatus() != tur.getStatus()) {
			createTicketHistory(ticket, "AssignedNewStatus", String.valueOf(ticket.getStatus())
					, String.valueOf(tur.getStatus()));
		}
		
		if(!ticket.getType().equals(tur.getBug())) {
			createTicketHistory(ticket, "AssignedNewType", ticket.getType()
					, tur.getBug());
		}
		
	}
	
	

}

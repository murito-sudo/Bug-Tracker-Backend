package com.example.Bug.Tracker.Backend;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.Bug.Tracker.Backend.Projects.Project;
import com.example.Bug.Tracker.Backend.Projects.ProjectJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.TicketComment;
import com.example.Bug.Tracker.Backend.Tickets.TicketCommentJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.TicketFile;
import com.example.Bug.Tracker.Backend.Tickets.TicketHistory;
import com.example.Bug.Tracker.Backend.Tickets.TicketHistoryJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.TicketJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.TicketResource;
import com.example.Bug.Tracker.Backend.Tickets.TicketUpdateRequest;
import com.example.Bug.Tracker.Backend.Tickets.Tickets;
import com.example.Bug.Tracker.Backend.User.User;
import com.example.Bug.Tracker.Backend.User.UserJPARepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = TicketResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@EnableMethodSecurity(prePostEnabled = true)
@ActiveProfiles("test")
public class TicketResourceTest {
	
	@Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PasswordEncoder encoder;
    
    @MockBean
    private UserJPARepository UJR;
    
    @MockBean
    private TicketJPARepository TJR;
    
    @MockBean
    private TicketHistoryJPARepository THR;
    
    @MockBean
    private TicketCommentJPARepository TCR;
    
    @MockBean
    private ProjectJPARepository PJR;
    
    
    
    
    @Mock
    private Tickets ticket;
    
    @Mock
    private TicketUpdateRequest updateRequest;
    
    @Mock
    private User admin;
    
    @Mock
    private User user;
    
    @Mock
    private User dev;
    
    @Mock
    private Project project;
    
    @Mock
    private TicketFile ticketFile; // Replace with your actual controller name
    
    
    @BeforeEach
    public void init() {
    	MockitoAnnotations.openMocks(this);
    	
    	admin = User.builder().id(0)
                .username("Admin")
                .password("perfect")
                .roles("Admin")
                .build();
    	
    	user = User.builder().id(1)
                .username("User")
                .password("perfect")
                .roles("User")
                .build();
    	
    	dev = User.builder().id(2)
                .username("Developer")
                .password("perfect")
                .roles("Developer")
                .build();

    	 ticket = Tickets.builder().id(0)
    	            .ticketTitle("Test ticket")
    	            .ticketDescription("Desc")
    	            .developerId(2)
    	            .submitterId(1)
    	            .projectId(0)
    	            .priority(1)
    	            .status(1)
    	            .type("Bug")
    	            .createdDate(LocalDateTime.now())
    	            .ticketHistory(new LinkedList<>())
    	            .ticketComments(new LinkedList<>())
    	            .build();
    	 
    	 updateRequest = TicketUpdateRequest.builder().developerId(2)
    			 .title("new title")
    			 .desc("new desc")
    			 .priority(2)
    			 .status(1)
    			 .bug("Glitch").build();
    	
    	project = Project.builder().projectId(0).projectName("Test Project").
    			tickets(new HashSet<Integer>()).projectOwnerId(0)
    			.userRole(new HashMap<Integer, String>()).build();
      
    	
    }
    
	
	@Test
	@WithMockUser(username="User", roles={"User"})
    public void TicketResource_CreateTicket() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
		dev = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		
		
		
		when(dev.getId()).thenReturn(2);
		when(user.getId()).thenReturn(1);
		
		HashMap<Integer, String> h = new HashMap<>();
		h.put(dev.getId(), "Developer");
		h.put(user.getId(), "User");
		
		when(project.getUserRole()).thenReturn(h);
		
		
		when(ticket.getDeveloperId()).thenReturn(2);
	
		when(ticket.getTicketTitle()).thenReturn("Test ticket");
		
		
		when(UJR.findById(ticket.getDeveloperId())).thenReturn(Optional.of(dev));
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		
		
		
		// Params for ticket created
		when(ticket.getTicketDescription()).thenReturn("Test Desc");
		when(ticket.getType()).thenReturn("Bug");
		
		
		
        // Convert user object to JSON string
        String userJson = objectMapper.writeValueAsString(ticket);
        System.out.println(userJson);
        
        
        
        
        // Perform POST request
        ResultActions response = mockMvc.perform(post("/createTicket/{projectid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
        
       
       

        
        response.andExpect(status().isCreated());
        
    }
	
	@Test
	@WithMockUser(username="User", roles={"User"})
    public void TicketResource_CreateTicket_NonDeveloper() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
		dev = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		
		
		when(dev.getId()).thenReturn(2);
		when(user.getId()).thenReturn(1);
		
		HashMap<Integer, String> h = new HashMap<>();
		h.put(dev.getId(), "User");
		h.put(user.getId(), "User");
		
		when(project.getUserRole()).thenReturn(h);
		
		
		when(ticket.getDeveloperId()).thenReturn(2);
	
		when(ticket.getTicketTitle()).thenReturn("Test ticket");
		
		
		when(UJR.findById(ticket.getDeveloperId())).thenReturn(Optional.of(dev));
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		
		
		
		// Params for ticket created
		when(ticket.getTicketDescription()).thenReturn("Test Desc");
		when(ticket.getType()).thenReturn("Bug");
		
		
		
        // Convert user object to JSON string
        String userJson = objectMapper.writeValueAsString(ticket);
        System.out.println(userJson);
        
        
        
        
        // Perform POST request
        ResultActions response = mockMvc.perform(post("/createTicket/{projectid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
        
       
       

        
        response.andExpect(status().isForbidden());
        
    }
	
	@Test
	@WithMockUser(username="User", roles={"User"})
    public void TicketResource_CreateTicket_NonDeveloperMember() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
		dev = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		
		
		
		
		when(dev.getId()).thenReturn(2);
		when(user.getId()).thenReturn(1);
		
		HashMap<Integer, String> h = new HashMap<>();
		h.put(user.getId(), "User");
		
		when(project.getUserRole()).thenReturn(h);
		
		
		when(ticket.getDeveloperId()).thenReturn(2);
	
		when(ticket.getTicketTitle()).thenReturn("Test ticket");
		
		
		when(UJR.findById(ticket.getDeveloperId())).thenReturn(Optional.of(dev));
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		
		
		
		// Params for ticket created
		when(ticket.getTicketDescription()).thenReturn("Test Desc");
		when(ticket.getType()).thenReturn("Bug");
		
		
		
        // Convert user object to JSON string
        String userJson = objectMapper.writeValueAsString(ticket);
        System.out.println(userJson);
        
        
        
        
        // Perform POST request
        ResultActions response = mockMvc.perform(post("/createTicket/{projectid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
        
       
       

        
        response.andExpect(status().isForbidden());
        
    }
	
	@Test
	@WithMockUser(username="User", roles={"User"})
    public void TicketResource_CreateTicket_NonUserMember() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
		dev = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		
		
		
		
		when(dev.getId()).thenReturn(2);
		when(user.getId()).thenReturn(1);
		
		HashMap<Integer, String> h = new HashMap<>();
		h.put(dev.getId(), "Developer");
		
		when(project.getUserRole()).thenReturn(h);
		
		
		when(ticket.getDeveloperId()).thenReturn(2);
	
		when(ticket.getTicketTitle()).thenReturn("Test ticket");
		
		when(ticket.getStatus()).thenReturn(1);
		
		
		when(UJR.findById(ticket.getDeveloperId())).thenReturn(Optional.of(dev));
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		
		
		
		// Params for ticket created
		when(ticket.getTicketDescription()).thenReturn("Test Desc");
		when(ticket.getType()).thenReturn("Bug");
		
		
		
        // Convert user object to JSON string
        String userJson = objectMapper.writeValueAsString(ticket);
        System.out.println(userJson);
        
        
        
        
        // Perform POST request
        ResultActions response = mockMvc.perform(post("/createTicket/{projectid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
        
       
       

        
        response.andExpect(status().isForbidden());
        
    }
	
	@Test
	@WithMockUser(username="User", roles={"User"})
    public void TicketResource_CreateTicket_NoProjectFound() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
		dev = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		
		
		
		
		when(ticket.getDeveloperId()).thenReturn(2);
	
		when(ticket.getTicketTitle()).thenReturn("Test ticket");
		
		
		when(UJR.findById(ticket.getDeveloperId())).thenReturn(Optional.of(dev));
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(PJR.findById(0)).thenReturn(Optional.empty());
		
		
		
		// Params for ticket created
		when(ticket.getTicketDescription()).thenReturn("Test Desc");
		when(ticket.getType()).thenReturn("Bug");
		
		
		
        // Convert user object to JSON string
        String userJson = objectMapper.writeValueAsString(ticket);
        System.out.println(userJson);
        
        
        
        
        // Perform POST request
        ResultActions response = mockMvc.perform(post("/createTicket/{projectid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
        
       
        response.andExpect(status().isNotFound());
        
    }
	
	@Test
	@WithMockUser(username="User", roles={"User"})
	public void TicketResource_UpdateTicket() throws Exception {
	    ticket = Mockito.mock(Tickets.class);
	    user = Mockito.mock(User.class);
	    dev = Mockito.mock(User.class);
	    project = Mockito.mock(Project.class);
	    updateRequest = Mockito.mock(TicketUpdateRequest.class);

	    when(UJR.findByusername("User")).thenReturn(Optional.of(user));
	    when(TJR.findById(0)).thenReturn(Optional.of(ticket));
	    when(UJR.findById(2)).thenReturn(Optional.of(dev));
	    when(PJR.findById(0)).thenReturn(Optional.of(project));

	    when(dev.getId()).thenReturn(2);
	    when(user.getId()).thenReturn(1);
	    
	    when(ticket.getTicketTitle()).thenReturn("Old Title");
	    when(ticket.getTicketDescription()).thenReturn("Old Description");
	    when(ticket.getSubmitterId()).thenReturn(1);
	    when(ticket.getDeveloperId()).thenReturn(2);
	    when(ticket.getPriority()).thenReturn(1);
	    when(ticket.getStatus()).thenReturn(1);
	    when(ticket.getType()).thenReturn("Bug");

	    // Update Request mock
	    when(updateRequest.getDeveloperId()).thenReturn(2);
	    when(updateRequest.getTitle()).thenReturn("New Title");
	    when(updateRequest.getDesc()).thenReturn("New Desc");
	    when(updateRequest.getPriority()).thenReturn(2);
	    when(updateRequest.getStatus()).thenReturn(1);
	    when(updateRequest.getBug()).thenReturn("Glitch");

	    HashMap<Integer, String> h = new HashMap<>();
	    h.put(user.getId(), "User");
	    h.put(dev.getId(), "Developer");
	    
	    when(project.getUserRole()).thenReturn(h);

	    // Convert user object to JSON string
	    String userJson = objectMapper.writeValueAsString(updateRequest);
	    System.out.println(userJson);

	    ResultActions response = mockMvc.perform(put("/updateTicket/{ticketid}", 0)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(userJson));

	    verify(TJR, times(5)).save(ticket);
	    
	    
	    response.andExpect(status().isOk());
	    
	    
	}

	
	@Test
	@WithMockUser(username="User", roles={"User"})
    public void TicketResource_UpdateTicket_Status() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
		dev = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		updateRequest = Mockito.mock(TicketUpdateRequest.class);
		
		
		
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(TJR.findById(0)).thenReturn(Optional.of(ticket));
		when(UJR.findById(2)).thenReturn(Optional.of(dev));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		
		when(dev.getId()).thenReturn(2);
		when(user.getId()).thenReturn(1);
		
		when(ticket.getSubmitterId()).thenReturn(1);
		when(ticket.getTicketTitle()).thenReturn("Old Title");
	    when(ticket.getTicketDescription()).thenReturn("Old Description");
	    when(ticket.getSubmitterId()).thenReturn(1);
	    when(ticket.getDeveloperId()).thenReturn(2);
	    when(ticket.getPriority()).thenReturn(1);
	    when(ticket.getStatus()).thenReturn(1);
	    when(ticket.getType()).thenReturn("Bug");
		
		//Update Request mock
		when(updateRequest.getDeveloperId()).thenReturn(2);
		when(updateRequest.getTitle()).thenReturn("New Title");
		when(updateRequest.getDesc()).thenReturn("New Desc");
		when(updateRequest.getPriority()).thenReturn(2);
		when(updateRequest.getStatus()).thenReturn(2);
		when(updateRequest.getBug()).thenReturn("Glitch");
	
		
		
		HashMap<Integer, String> h = new HashMap<>();
		h.put(user.getId(), "User");
		h.put(dev.getId(), "Developer");
		
		when(project.getUserRole()).thenReturn(h);
		
		
		// Convert user object to JSON string
		String userJson = objectMapper.writeValueAsString(updateRequest);
        System.out.println(userJson);
		
		
		ResultActions response = mockMvc.perform(put("/updateTicket/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
		
		verify(TJR, times(6)).save(ticket);
		
		response.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username="User", roles={"User"})
    public void TicketResource_UpdateTicket_InvalidStatus() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
		dev = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		updateRequest = Mockito.mock(TicketUpdateRequest.class);
		
		
		
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(TJR.findById(0)).thenReturn(Optional.of(ticket));
		when(UJR.findById(2)).thenReturn(Optional.of(dev));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		
	
		
		
		//Update Request mock
		when(updateRequest.getDeveloperId()).thenReturn(2);
		when(updateRequest.getTitle()).thenReturn("New Title");
		when(updateRequest.getDesc()).thenReturn("New Desc");
		when(updateRequest.getPriority()).thenReturn(2);
		when(updateRequest.getStatus()).thenReturn(4);
		when(updateRequest.getBug()).thenReturn("Glitch");
	
		
		
	
		
		
		// Convert user object to JSON string
		String userJson = objectMapper.writeValueAsString(updateRequest);
        System.out.println(userJson);
		
		
		ResultActions response = mockMvc.perform(put("/updateTicket/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
		
		verify(TJR, never()).save(ticket);
		
		response.andExpect(status().isBadRequest());
	}
	
	@Test
	@WithMockUser(username="User", roles={"User"})
    public void TicketResource_UpdateTicket_NonDeveloper() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
		dev = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		updateRequest = Mockito.mock(TicketUpdateRequest.class);
		
		
		
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(TJR.findById(0)).thenReturn(Optional.of(ticket));
		when(UJR.findById(2)).thenReturn(Optional.of(dev));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		
		when(dev.getId()).thenReturn(2);
		when(user.getId()).thenReturn(1);
		when(ticket.getSubmitterId()).thenReturn(1);
		
		//Update Request mock
		when(updateRequest.getDeveloperId()).thenReturn(2);
		when(updateRequest.getTitle()).thenReturn("New Title");
		when(updateRequest.getDesc()).thenReturn("New Desc");
		when(updateRequest.getPriority()).thenReturn(2);
		when(updateRequest.getStatus()).thenReturn(1);
		when(updateRequest.getBug()).thenReturn("Glitch");
	
		
		
		HashMap<Integer, String> h = new HashMap<>();
		h.put(user.getId(), "User");
		h.put(dev.getId(), "User");
		
		when(project.getUserRole()).thenReturn(h);
		
		
		// Convert user object to JSON string
		String userJson = objectMapper.writeValueAsString(updateRequest);
        System.out.println(userJson);
		
		
		ResultActions response = mockMvc.perform(put("/updateTicket/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
		
		verify(TJR, never()).save(ticket);
		
		response.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(username="User", roles={"User"})
    public void TicketResource_UpdateTicket_NonSubmitter() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
		dev = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		updateRequest = Mockito.mock(TicketUpdateRequest.class);
		
		
		
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(TJR.findById(0)).thenReturn(Optional.of(ticket));
		when(UJR.findById(2)).thenReturn(Optional.of(dev));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		
		when(dev.getId()).thenReturn(2);
		when(user.getId()).thenReturn(1);
		when(ticket.getSubmitterId()).thenReturn(0);
		when(user.getRoles()).thenReturn("User");
		
		//Update Request mock
		when(updateRequest.getDeveloperId()).thenReturn(2);
		when(updateRequest.getTitle()).thenReturn("New Title");
		when(updateRequest.getDesc()).thenReturn("New Desc");
		when(updateRequest.getPriority()).thenReturn(2);
		when(updateRequest.getStatus()).thenReturn(1);
		when(updateRequest.getBug()).thenReturn("Glitch");
	
		
		
		HashMap<Integer, String> h = new HashMap<>();
		h.put(user.getId(), "User");
		h.put(dev.getId(), "Developer");
		
		when(project.getUserRole()).thenReturn(h);
		
		
		// Convert user object to JSON string
		String userJson = objectMapper.writeValueAsString(updateRequest);
        System.out.println(userJson);
		
		
		ResultActions response = mockMvc.perform(put("/updateTicket/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
		
		verify(TJR, never()).save(ticket);
		
		response.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(username="User", roles={"User"})
    public void TicketResource_UpdateTicket_Admin() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
		dev = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		updateRequest = Mockito.mock(TicketUpdateRequest.class);
		
		
		
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(TJR.findById(0)).thenReturn(Optional.of(ticket));
		when(UJR.findById(2)).thenReturn(Optional.of(dev));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		
		when(dev.getId()).thenReturn(2);
		when(user.getId()).thenReturn(1);
		
		when(user.getRoles()).thenReturn("Admin");
		
		
		
		when(ticket.getSubmitterId()).thenReturn(0);
		when(ticket.getDeveloperId()).thenReturn(2);
		when(ticket.getTicketTitle()).thenReturn("Old Title");
	    when(ticket.getTicketDescription()).thenReturn("Old Description");
	    when(ticket.getPriority()).thenReturn(1);
	    when(ticket.getStatus()).thenReturn(1);
	    when(ticket.getType()).thenReturn("Bug");
		
		
		//Update Request mock
	    when(updateRequest.getDeveloperId()).thenReturn(2);
		when(updateRequest.getTitle()).thenReturn("New Title");
		when(updateRequest.getDesc()).thenReturn("New Desc");
		when(updateRequest.getPriority()).thenReturn(2);
		when(updateRequest.getStatus()).thenReturn(1);
		when(updateRequest.getBug()).thenReturn("Glitch");
	
		
		
		HashMap<Integer, String> h = new HashMap<>();
		h.put(user.getId(), "User");
		h.put(dev.getId(), "Developer");
		
		when(project.getUserRole()).thenReturn(h);
		
		
		// Convert user object to JSON string
		String userJson = objectMapper.writeValueAsString(updateRequest);
        System.out.println(userJson);
		
		
		ResultActions response = mockMvc.perform(put("/updateTicket/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
		
		verify(TJR, times(5)).save(ticket);
		verify(THR, times(4)).save(any(TicketHistory.class));
		
		
		response.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username="User", roles={"User"})
    public void TicketResource_UpdateTicket_GroupAdmin() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
		dev = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		updateRequest = Mockito.mock(TicketUpdateRequest.class);
		
		
		
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(TJR.findById(0)).thenReturn(Optional.of(ticket));
		when(UJR.findById(2)).thenReturn(Optional.of(dev));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		
		
		
		when(dev.getId()).thenReturn(2);
		when(user.getId()).thenReturn(1);
		when(user.getRoles()).thenReturn("User");
		
		
		
		when(ticket.getSubmitterId()).thenReturn(0);
		when(ticket.getTicketTitle()).thenReturn("Old Title");
	    when(ticket.getTicketDescription()).thenReturn("Old Description");
	    when(ticket.getDeveloperId()).thenReturn(2);
	    when(ticket.getPriority()).thenReturn(1);
	    when(ticket.getStatus()).thenReturn(1);
	    when(ticket.getType()).thenReturn("Bug");
		
		
		
		//Update Request mock
		when(updateRequest.getDeveloperId()).thenReturn(2);
		when(updateRequest.getTitle()).thenReturn("New Title");
		when(updateRequest.getDesc()).thenReturn("New desc");
		when(updateRequest.getPriority()).thenReturn(2);
		when(updateRequest.getStatus()).thenReturn(1);
		when(updateRequest.getBug()).thenReturn("Glitch");
	
		
		
		HashMap<Integer, String> h = new HashMap<>();
		h.put(user.getId(), "Admin");
		h.put(dev.getId(), "Developer");
		
		when(project.getUserRole()).thenReturn(h);
		
		
		
		// Convert user object to JSON string
		String userJson = objectMapper.writeValueAsString(updateRequest);
        System.out.println(userJson);
		
		
		ResultActions response = mockMvc.perform(put("/updateTicket/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
		
		verify(TJR, times(5)).save(ticket);
		
		response.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username="User", roles={"User"})
    public void TicketResource_UpdateTicket_LongTitle() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
		dev = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		updateRequest = Mockito.mock(TicketUpdateRequest.class);
		
		
		
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(TJR.findById(0)).thenReturn(Optional.of(ticket));
		when(UJR.findById(2)).thenReturn(Optional.of(dev));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		
	
		
		//Update Request mock
		when(updateRequest.getDeveloperId()).thenReturn(2);
		when(updateRequest.getTitle()).thenReturn("qoeihoqrnkafojwabfonbaofjqofnbqwfqwjb foabfoWBFowbffnqwfqwofnqofbqwogbqo");
		when(updateRequest.getDesc()).thenReturn("New desc");
		when(updateRequest.getPriority()).thenReturn(2);
		when(updateRequest.getStatus()).thenReturn(1);
		when(updateRequest.getBug()).thenReturn("Glitch");
	
	
		
		
		// Convert user object to JSON string
		String userJson = objectMapper.writeValueAsString(updateRequest);
        System.out.println(userJson);
		
		
		ResultActions response = mockMvc.perform(put("/updateTicket/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
		
		verify(TJR, never()).save(ticket);
		
		response.andExpect(status().isBadRequest());
	}
	
	
	
	@Test
	@WithMockUser(username="User", roles={"User"})
	public void TicketResource_OpenTicket() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		
		
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(TJR.findById(0)).thenReturn(Optional.of(ticket));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
	
		when(user.getId()).thenReturn(1);
		//when(project.getProjectId()).thenReturn(0);
		//when(ticket.getId()).thenReturn(0);
		when(ticket.getSubmitterId()).thenReturn(1);
		when(ticket.getDeveloperId()).thenReturn(0);
		when(ticket.getPriority()).thenReturn(2);
		
		when(project.getProjectOwnerId()).thenReturn(0);
		
		when(user.getRoles()).thenReturn("User");
		
		
		when(ticket.getStatus()).thenReturn(3);
		
		HashMap<Integer, String> h = new HashMap<>();
		h.put(user.getId(), "Mod");
		when(project.getUserRole()).thenReturn(h);
		
		String userJson = objectMapper.writeValueAsString(ticket);
        System.out.println(userJson);
		
		
		ResultActions response = mockMvc.perform(put("/openTicket/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
		
		
		verify(ticket, times(1)).setStatus(anyInt());
		verify(TJR, times(2)).save(ticket);
		response.andExpect(status().isOk());
		
	}
	
	@Test
	@WithMockUser(username="User", roles={"User"})
	public void TicketResource_OpenTicket_AlreadyInProgess() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		
		
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(TJR.findById(0)).thenReturn(Optional.of(ticket));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		
		
		when(user.getId()).thenReturn(1);
		//when(project.getProjectId()).thenReturn(0);
		//when(ticket.getId()).thenReturn(0);
		//when(project.getProjectOwnerId()).thenReturn(0);
		
		//when(user.getRoles()).thenReturn("User");
	
		
		when(ticket.getStatus()).thenReturn(1);
		
		HashMap<Integer, String> h = new HashMap<>();
		h.put(user.getId(), "Mod");
		//when(project.getUserRole()).thenReturn(h);
		
		String userJson = objectMapper.writeValueAsString(ticket);
        System.out.println(userJson);
		
		
		ResultActions response = mockMvc.perform(put("/openTicket/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
		
		verify(TJR, never()).save(ticket);
		
		response.andExpect(status().isForbidden());
		
	}
	
	@Test
	@WithMockUser(username="User", roles={"User"})
	public void TicketResource_OpenTicket_NonAdmin() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
	
		project = Mockito.mock(Project.class);
		
		
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(TJR.findById(0)).thenReturn(Optional.of(ticket));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		
		
		when(user.getId()).thenReturn(1);
		//when(project.getProjectId()).thenReturn(0);
		//when(ticket.getId()).thenReturn(0);
		when(project.getProjectOwnerId()).thenReturn(0);
		
		when(user.getRoles()).thenReturn("User");
		
		when(ticket.getStatus()).thenReturn(4);
		
		HashMap<Integer, String> h = new HashMap<>();
		h.put(user.getId(), "User");
		when(project.getUserRole()).thenReturn(h);
		
		String userJson = objectMapper.writeValueAsString(ticket);
        System.out.println(userJson);
		
		
		ResultActions response = mockMvc.perform(put("/openTicket/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
		
		verify(ticket, never()).setStatus(anyInt());
		verify(TJR, never()).save(ticket);
		
		response.andExpect(status().isForbidden());
		
	}
	
	@Test
	@WithMockUser(username="User", roles={"User"})
	public void testFile() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);

		
        when(UJR.findByusername("User")).thenReturn(Optional.of(user));
        when(TJR.findById(anyInt())).thenReturn(Optional.of(ticket));
        when(PJR.findById(anyInt())).thenReturn(Optional.of(project));
        
        when(user.getRoles()).thenReturn("Admin");
        

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Test content".getBytes());

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.multipart("/addFile/{ticketid}", 0)
                .file(file)
                .with(request -> {
                    request.setMethod("PUT"); // Set the method to PUT
                    return request;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
                
    
		
		
	}
	

	
	@Test
	@WithMockUser(username="User", roles={"User"})
	public void TicketResource_DeleteTicket_TicketSubmitter() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
	
		project = Mockito.mock(Project.class);
		
		TicketComment ticketComment = new TicketComment();
		ticketComment.setCommentId(0);
		
		
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(TJR.findById(0)).thenReturn(Optional.of(ticket));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		when(TCR.findById(0)).thenReturn(Optional.of(ticketComment));
		
		
		when(user.getId()).thenReturn(0);
		//when(project.getProjectId()).thenReturn(0);
		when(ticket.getId()).thenReturn(0);
		when(ticket.getSubmitterId()).thenReturn(0);
		
		
		
		when(ticket.getStatus()).thenReturn(4);
		
		HashMap<Integer, String> users = new HashMap<>();
		users.put(user.getId(), "User");
		HashSet<Integer> projectTickets = new HashSet<>();
		projectTickets.add(ticket.getId());
		List<Integer> TicketComments = new LinkedList<Integer>();
		TicketComments.add(0);
		//when(project.getUserRole()).thenReturn(users);
		when(ticket.getTicketComments()).thenReturn(TicketComments);
		when(project.getTickets()).thenReturn(projectTickets);
		
		String userJson = objectMapper.writeValueAsString(ticket);
        System.out.println(userJson);
		
		
		ResultActions response = mockMvc.perform(delete("/deleteTicket/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
		
		//verify(ticket, never()).setStatus(anyInt());
		verify(TJR, times(1)).delete(ticket);
		verify(TCR, times(1)).deleteById(ticketComment.getCommentId());
		
		response.andExpect(status().isOk());
		
	}
	
	@Test
	@WithMockUser(username="User", roles={"User"})
	public void TicketResource_DeleteTicket_ProjectOwner() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
	
		project = Mockito.mock(Project.class);
		
		TicketComment ticketComment = new TicketComment();
		ticketComment.setCommentId(0);
		
		
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(TJR.findById(0)).thenReturn(Optional.of(ticket));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		when(TCR.findById(0)).thenReturn(Optional.of(ticketComment));
		
		
		when(user.getId()).thenReturn(0);
		when(ticket.getId()).thenReturn(0);
		when(ticket.getSubmitterId()).thenReturn(1);
		when(project.getProjectOwnerId()).thenReturn(0);
		
		
		
		when(ticket.getStatus()).thenReturn(4);
		
		HashMap<Integer, String> users = new HashMap<>();
		users.put(user.getId(), "User");
		HashSet<Integer> projectTickets = new HashSet<>();
		projectTickets.add(ticket.getId());
		List<Integer> TicketComments = new LinkedList<Integer>();
		TicketComments.add(0);
		//when(project.getUserRole()).thenReturn(users);
		when(ticket.getTicketComments()).thenReturn(TicketComments);
		when(project.getTickets()).thenReturn(projectTickets);
		
		String userJson = objectMapper.writeValueAsString(ticket);
        System.out.println(userJson);
		
		
		ResultActions response = mockMvc.perform(delete("/deleteTicket/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
		
		//verify(ticket, never()).setStatus(anyInt());
		verify(TJR, times(1)).delete(ticket);
		verify(TCR, times(1)).deleteById(ticketComment.getCommentId());
		
		response.andExpect(status().isOk());
		
	}
	
	@Test
	@WithMockUser(username="User", roles={"User"})
	public void TicketResource_DeleteTicket_Admin() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
	
		project = Mockito.mock(Project.class);
		
		TicketComment ticketComment = new TicketComment();
		ticketComment.setCommentId(0);
		
		
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(TJR.findById(0)).thenReturn(Optional.of(ticket));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		when(TCR.findById(0)).thenReturn(Optional.of(ticketComment));
		
		
		when(user.getId()).thenReturn(0);
		
		
		
		when(ticket.getId()).thenReturn(0);
		when(ticket.getSubmitterId()).thenReturn(1);
		when(project.getProjectOwnerId()).thenReturn(1);
	
		when(ticket.getStatus()).thenReturn(4);
		
		when(user.getRoles()).thenReturn("Admin");
		
		HashMap<Integer, String> users = new HashMap<>();
		users.put(user.getId(), "User");
		HashSet<Integer> projectTickets = new HashSet<>();
		projectTickets.add(ticket.getId());
		List<Integer> TicketComments = new LinkedList<Integer>();
		TicketComments.add(0);
		//when(project.getUserRole()).thenReturn(users);
		when(ticket.getTicketComments()).thenReturn(TicketComments);
		when(project.getTickets()).thenReturn(projectTickets);
		
		String userJson = objectMapper.writeValueAsString(ticket);
        System.out.println(userJson);
		
		
		ResultActions response = mockMvc.perform(delete("/deleteTicket/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
		
		//verify(ticket, never()).setStatus(anyInt());
		verify(TJR, times(1)).delete(ticket);
		verify(TCR, times(1)).deleteById(ticketComment.getCommentId());
		
		response.andExpect(status().isOk());
		
	}
	
	@Test
	@WithMockUser(username="User", roles={"User"})
	public void TicketResource_DeleteTicket_ProjectAuthorized() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
	
		project = Mockito.mock(Project.class);
		
		TicketComment ticketComment = new TicketComment();
		ticketComment.setCommentId(0);
		
		
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(TJR.findById(0)).thenReturn(Optional.of(ticket));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		when(TCR.findById(0)).thenReturn(Optional.of(ticketComment));
		
		
		when(user.getId()).thenReturn(0);
		
		
		
		when(ticket.getId()).thenReturn(0);
		when(ticket.getSubmitterId()).thenReturn(1);
		when(project.getProjectOwnerId()).thenReturn(1);
	
		when(ticket.getStatus()).thenReturn(4);
		
		when(user.getRoles()).thenReturn("User");
		
		HashMap<Integer, String> users = new HashMap<>();
		users.put(user.getId(), "Mod");
		HashSet<Integer> projectTickets = new HashSet<>();
		projectTickets.add(ticket.getId());
		List<Integer> TicketComments = new LinkedList<Integer>();
		TicketComments.add(0);
		when(project.getUserRole()).thenReturn(users);
		when(ticket.getTicketComments()).thenReturn(TicketComments);
		when(project.getTickets()).thenReturn(projectTickets);
		
		String userJson = objectMapper.writeValueAsString(ticket);
        System.out.println(userJson);
		
		
		ResultActions response = mockMvc.perform(delete("/deleteTicket/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
		
		//verify(ticket, never()).setStatus(anyInt());
		verify(TJR, times(1)).delete(ticket);
		verify(TCR, times(1)).deleteById(ticketComment.getCommentId());
		
		response.andExpect(status().isOk());
		
	}
	
	

  


    
	
	@Test
	@WithMockUser(username="User", roles={"User"})
	public void TicketResource_DeleteTicket_NoAuthorized() throws Exception {
		ticket = Mockito.mock(Tickets.class);
		user = Mockito.mock(User.class);
	
		project = Mockito.mock(Project.class);
		
		TicketComment ticketComment = new TicketComment();
		ticketComment.setCommentId(0);
		
		
		when(UJR.findByusername("User")).thenReturn(Optional.of(user));
		when(TJR.findById(0)).thenReturn(Optional.of(ticket));
		when(PJR.findById(0)).thenReturn(Optional.of(project));
		when(TCR.findById(0)).thenReturn(Optional.of(ticketComment));
		
		
		when(user.getId()).thenReturn(0);
		
		
		
		when(ticket.getId()).thenReturn(0);
		when(ticket.getSubmitterId()).thenReturn(1);
		when(project.getProjectOwnerId()).thenReturn(1);
	
		when(ticket.getStatus()).thenReturn(4);
		
		when(user.getRoles()).thenReturn("User");
		
		HashMap<Integer, String> users = new HashMap<>();
		users.put(user.getId(), "User");
		HashSet<Integer> projectTickets = new HashSet<>();
		projectTickets.add(ticket.getId());
		List<Integer> TicketComments = new LinkedList<Integer>();
		TicketComments.add(0);
		//when(project.getUserRole()).thenReturn(users);
		when(ticket.getTicketComments()).thenReturn(TicketComments);

		
		String userJson = objectMapper.writeValueAsString(ticket);
        System.out.println(userJson);
		
		
		ResultActions response = mockMvc.perform(delete("/deleteTicket/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
		
		//verify(ticket, never()).setStatus(anyInt());
		verify(TJR, never()).delete(ticket);
		verify(TCR, never()).deleteById(ticketComment.getCommentId());
		
		response.andExpect(status().isForbidden());
		
	}
	


}

package com.example.Bug.Tracker.Backend;


import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.Bug.Tracker.Backend.Projects.Project;
import com.example.Bug.Tracker.Backend.Projects.ProjectJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.TicketComment;
import com.example.Bug.Tracker.Backend.Tickets.TicketCommentJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.TicketCommentResource;
import com.example.Bug.Tracker.Backend.Tickets.TicketHistoryJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.TicketJPARepository;

import com.example.Bug.Tracker.Backend.Tickets.TicketUpdateRequest;
import com.example.Bug.Tracker.Backend.Tickets.Tickets;
import com.example.Bug.Tracker.Backend.User.User;
import com.example.Bug.Tracker.Backend.User.UserJPARepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = TicketCommentResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@EnableMethodSecurity(prePostEnabled = true)
@ActiveProfiles("test")
public class TicketCommentResourceTest {
	
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
    private TicketComment ticketComment;
    
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
    	 
    	 ticketComment = TicketComment.builder().commentId(0).ticketid(0).commenterId(0)
    			 .Message("Hello World!").Created(ZonedDateTime.now()).build();
    	 
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
    public void TicketComment_addComment() throws Exception{
    	
    	ticket = Mockito.mock(Tickets.class);
    	ticketComment = Mockito.mock(TicketComment.class);
		user = Mockito.mock(User.class);
		admin = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		
		
		
		when(user.getId()).thenReturn(1);
		
	
		
		when(ticketComment.getMessage()).thenReturn("Hello World");
		when(ticketComment.getCommenterId()).thenReturn(1);
		
		
		
    	when(UJR.findByusername("User")).thenReturn(Optional.of(user));
    	when(TJR.findById(0)).thenReturn(Optional.of(ticket));
    	when(PJR.findById(0)).thenReturn(Optional.of(project));
    	
    	HashMap<Integer, String> h = new HashMap<>();
    	h.put(0, "Admin");
    	h.put(1, "User");
    	when(project.getUserRole()).thenReturn(h);
    	
    	String userJson = objectMapper.writeValueAsString(ticketComment);
        System.out.println(userJson);
        
        
        
        
        // Perform POST request
        ResultActions response = mockMvc.perform(post("/addComment/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
        
       
       

        
        response.andExpect(status().isCreated());
       
        verify(TJR, times(1)).save(ticket);
        verify(TCR, times(1)).save(Mockito.any(TicketComment.class));
    	
    	
    }
    
    
    @Test
	@WithMockUser(username="User", roles={"User"})
    public void TicketComment_addComment_NonProjectMember() throws Exception{
    	
    	ticket = Mockito.mock(Tickets.class);
    	ticketComment = Mockito.mock(TicketComment.class);
		user = Mockito.mock(User.class);
		admin = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		
		
		
		when(user.getId()).thenReturn(1);
		
	
		
		when(ticketComment.getMessage()).thenReturn("Hello World");
		when(ticketComment.getCommenterId()).thenReturn(2);
		
		
		
    	when(UJR.findByusername("User")).thenReturn(Optional.of(user));
    	when(TJR.findById(0)).thenReturn(Optional.of(ticket));
    	when(PJR.findById(0)).thenReturn(Optional.of(project));
    	
    	HashMap<Integer, String> h = new HashMap<>();
    	h.put(0, "Admin");
    	h.put(2, "User");
    	when(project.getUserRole()).thenReturn(h);
    	
    	String userJson = objectMapper.writeValueAsString(ticketComment);
        System.out.println(userJson);
        
        
        
        
        // Perform POST request
        ResultActions response = mockMvc.perform(post("/addComment/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
        

        response.andExpect(status().isForbidden());

        verify(TJR, never()).save(ticket);
        verify(TCR, never()).save(ticketComment);
    	
    	
    }
    
    
    @Test
	@WithMockUser(username="User", roles={"User"})
    public void TicketComment_addComment_EmptyMessage() throws Exception{
    	
    	ticket = Mockito.mock(Tickets.class);
    	ticketComment = Mockito.mock(TicketComment.class);
		user = Mockito.mock(User.class);
		admin = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		
		
		
		
		
	
		
		when(ticketComment.getMessage()).thenReturn("");
		when(ticketComment.getCommenterId()).thenReturn(2);
		
		
		
    	when(UJR.findByusername("User")).thenReturn(Optional.of(user));
    	when(TJR.findById(0)).thenReturn(Optional.of(ticket));
    	when(PJR.findById(0)).thenReturn(Optional.of(project));
    	
    	
    	
    	String userJson = objectMapper.writeValueAsString(ticketComment);
        System.out.println(userJson);
        
        
        
        
        // Perform POST request
        ResultActions response = mockMvc.perform(post("/addComment/{ticketid}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
        

        response.andExpect(status().isBadRequest());
        
        verify(TJR, never()).save(ticket);
        verify(TCR, never()).save(ticketComment);
    	
    	
    }
    
    @Test
	@WithMockUser(username="User", roles={"User"})
    public void TicketComment_addComment_InvalidTicket() throws Exception{
    	
    	ticket = Mockito.mock(Tickets.class);
    	ticketComment = Mockito.mock(TicketComment.class);
		user = Mockito.mock(User.class);
		admin = Mockito.mock(User.class);
		project = Mockito.mock(Project.class);
		
		
		
		
	
		
		when(ticketComment.getMessage()).thenReturn("Hello World");
		when(ticketComment.getCommenterId()).thenReturn(1);
		
		
		
    	when(UJR.findByusername("User")).thenReturn(Optional.of(user));
    	when(TJR.findById(1)).thenReturn(Optional.empty());
    	when(PJR.findById(0)).thenReturn(Optional.of(project));
    	
    	
    	
    	String userJson = objectMapper.writeValueAsString(ticketComment);
        System.out.println(userJson);
        
        
        
        
        // Perform POST request
        ResultActions response = mockMvc.perform(post("/addComment/{ticketid}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
        
       
        
        response.andExpect(status().isNotFound());
       
        verify(TJR, never()).save(ticket);
        verify(TCR, never()).save(ticketComment);
    	
    	
    }
    
	
	
	
	
	
	

}
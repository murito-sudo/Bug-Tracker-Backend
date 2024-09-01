package com.example.Bug.Tracker.Backend;




import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;


import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.context.ActiveProfiles;


import com.example.Bug.Tracker.Backend.Projects.Project;
import com.example.Bug.Tracker.Backend.Projects.ProjectJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.TicketCommentJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.TicketHistory;
import com.example.Bug.Tracker.Backend.Tickets.TicketHistoryJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.TicketHistoryResource;
import com.example.Bug.Tracker.Backend.Tickets.TicketJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.TicketUpdateRequest;
import com.example.Bug.Tracker.Backend.Tickets.Tickets;
import com.example.Bug.Tracker.Backend.User.User;
import com.example.Bug.Tracker.Backend.User.UserJPARepository;

@WebMvcTest(controllers = TicketHistoryResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@EnableMethodSecurity(prePostEnabled = true)
@ActiveProfiles("test")
public class TicketHistoryTest {
	
	
	/*
	@Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MockMvc mockMvc;
    
    */
    
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
    private TicketHistory ticketHistory;
    
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
    	 
    	ticketHistory = TicketHistory.builder().ticketHistoryId(0).ticketId(0).property("Fixing bug")
    			.oldVal("old value").newVal("new value").updateDate(ZonedDateTime.now()).build();
    	
    	 
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
    
    
    //Tests for future api implementation
    

}

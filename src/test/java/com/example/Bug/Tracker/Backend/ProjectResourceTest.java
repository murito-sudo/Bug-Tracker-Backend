package com.example.Bug.Tracker.Backend;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.Bug.Tracker.Backend.Projects.Project;
import com.example.Bug.Tracker.Backend.Projects.ProjectJPARepository;
import com.example.Bug.Tracker.Backend.Projects.ProjectResource;
import com.example.Bug.Tracker.Backend.Tickets.TicketCommentJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.TicketHistoryJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.TicketJPARepository;
import com.example.Bug.Tracker.Backend.Tickets.Tickets;
import com.example.Bug.Tracker.Backend.User.User;
import com.example.Bug.Tracker.Backend.User.UserJPARepository;
import com.example.Bug.Tracker.Backend.User.UserRetrieval;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;


@WebMvcTest(controllers = ProjectResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@EnableMethodSecurity(prePostEnabled = true)
@ActiveProfiles("test")
public class ProjectResourceTest {
	
	
	 	@Autowired
	    private ObjectMapper objectMapper;
	    
	    @Autowired
	    private MockMvc mockMvc;
	    
	    @MockBean
	    private PasswordEncoder encoder;
	    
	    @MockBean
	    private ProjectJPARepository PJR;
	    
	    @MockBean
	    private TicketJPARepository TJR;
	    @MockBean
	    private TicketCommentJPARepository TCR;
	    
	    @MockBean
	    private TicketHistoryJPARepository THR;
	    
	    
	    @MockBean
	    private UserJPARepository UJR;
	    
	   

	   
	    
	    
	
	    @Mock
	    private User user;
	    @Mock
	    private User user2;
	    
	    @Mock
	    private Project project;
	    
	    @Mock
	    private Project project2;
	    
	    @Mock
	    private Tickets ticket;
	    
	   
	    
	
	    
	    
	    
	    
	    @BeforeEach
	    public void init() {
	    	MockitoAnnotations.openMocks(this);
	    	
	    	
	    	user = User.builder().id(0)
	                .username("Admin")
	                .password("perfect")
	                .roles("Admin")
	                .build();
	    	
	    	user2 = User.builder().id(1)
	                .username("User")
	                .password("perfect")
	                .roles("User")
	                .build();
	           
	    	
	    	project = Project.builder().projectId(0).projectName("Test Project")
	    			.description("Hello Description").
	    			tickets(new HashSet<Integer>()).projectOwnerId(0)
	    			.userRole(new HashMap<Integer, String>()).build();
	    	
	    	project2 = Project.builder().projectId(1).projectName("Test Project2")
	    			.description("Hello Description2").
	    			tickets(new HashSet<Integer>()).projectOwnerId(0)
	    			.userRole(new HashMap<Integer, String>()).build();
	    	
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
	    	
	    	 
	    }
	    
	    @Test
	    @WithMockUser(username="Admin", roles={"Admin"})
	    public void ProjectResource_GetProjects() throws Exception{
	    	user = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	List<Project> projects = new LinkedList<Project>();
	    	projects.add(project);
	    	projects.add(project2);
	    	when(PJR.findAll()).thenReturn(projects);
	    	
	    	ResultActions response = mockMvc.perform(get("/getProjects")
	                .contentType(MediaType.APPLICATION_JSON));
	    	
	    	response.andExpect(status().isOk());
	    	
	    	
	    }
	    
	    @Test
	    @WithMockUser(username="Admin", roles={"User"})
	    public void ProjectResource_GetProjects_NonAdmin() throws Exception{
	    	user = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	List<Project> projects = new LinkedList<Project>();
	    	projects.add(project);
	    	projects.add(project2);
	    	when(UJR.findByusername("Admin")).thenReturn(Optional.of(user));
	    	when(PJR.findAll()).thenReturn(projects);
	    	
	    	
	    	
	    	assertThrows(ServletException.class, () -> {
	            ResultActions response = mockMvc.perform(get("/getProjects")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    );
	            // Perform additional assertions on the response if needed
	        });
	    	
	    }
	    
	    @Test
	    @WithMockUser(username="Admin", roles={"Admin"})
	    public void ProjectResource_GetProject_Admin() throws Exception{
	    	user = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    
	    
	    	when(UJR.findByusername("Admin")).thenReturn(Optional.of(user));
	    	when(PJR.findById(0)).thenReturn(Optional.of(project));
	    	when(user.getRoles()).thenReturn("Admin");
	    	
	    	
	    	
	    
	    	MvcResult mvcResult = mockMvc.perform(get("/getProject/{projectid}", 0)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andReturn();
	            // Perform additional assertions on the response if needed
	           
	            
	            
	    	 String responseContent = mvcResult.getResponse().getContentAsString();
	    	 
	         assertTrue(!responseContent.isEmpty()); // Expecting an empty response if the method returns null
	     
	    	
	    }
	    
	    @Test
	    @WithMockUser(username="Admin", roles={"Admin"})
	    public void ProjectResource_GetProject_Member() throws Exception{
	    	user = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    
	    
	    	when(UJR.findByusername("Admin")).thenReturn(Optional.of(user));
	    	when(PJR.findById(0)).thenReturn(Optional.of(project));
	    	when(user.getRoles()).thenReturn("User");
	    	when(user.getId()).thenReturn(0);
	    	HashMap<Integer, String> members = new HashMap<>();
	    	members.put(0, "User");
	    	when(project.getUserRole()).thenReturn(members);
	    	
	    	
	    	
	    
	    	MvcResult mvcResult = mockMvc.perform(get("/getProject/{projectid}", 0)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andReturn();
	            // Perform additional assertions on the response if needed
	           
	            
	            
	    	 String responseContent = mvcResult.getResponse().getContentAsString();
	    	
	         assertTrue(!responseContent.isEmpty()); // Expecting an empty response if the method returns null
	     
	    	
	    }
	    
	    @Test
	    @WithMockUser(username="Admin", roles={"User"})
	    public void ProjectResource_GetProject_NonMember() throws Exception{
	    	user = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    
	    
	    	when(UJR.findByusername("Admin")).thenReturn(Optional.of(user));
	    	when(PJR.findById(0)).thenReturn(Optional.of(project));
	    	when(user.getRoles()).thenReturn("User");
	    	
	    
	    	
	    	
	    	
	    
	    	MvcResult mvcResult = mockMvc.perform(get("/getProject/{projectid}", 0)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andReturn();
	            // Perform additional assertions on the response if needed
	           
	            
	            
	    	 String responseContent = mvcResult.getResponse().getContentAsString();
	    	
	         assertTrue(responseContent.isEmpty()); // Expecting an empty response if the method returns null
	     
	    	
	    }
	    
	    @Test
	    @WithMockUser(username="Admin", roles={"User"})
	    public void ProjectResource_ProjectTickets_Admin() throws Exception{
	    	user = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	HashSet<Integer> tickets = new HashSet<Integer>();
	    	Tickets ticket = new Tickets();
	    	tickets.add(0);
	    	tickets.add(1);
	    	
	    	
	    
	    
	    	when(UJR.findByusername("Admin")).thenReturn(Optional.of(user));
	    	when(PJR.findById(0)).thenReturn(Optional.of(project));
	    	when(project.getTickets()).thenReturn(tickets);
	    	
	    	when(TJR.findById(0)).thenReturn(Optional.of(ticket));
	    	when(TJR.findById(1)).thenReturn(Optional.of(ticket));
	    	
	    	
	    	when(user.getRoles()).thenReturn("Admin");
	    	
	    
	    	
	    	
	    	
	    
	    	MvcResult mvcResult = mockMvc.perform(get("/projectTickets/{projectid}", 0)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andReturn();
	            // Perform additional assertions on the response if needed
	           
	            
	            
	    	
	        String responseContent = mvcResult.getResponse().getContentAsString();
	        System.out.println("Response: " + responseContent);

	       
	        ObjectMapper objectMapper = new ObjectMapper();
	        List<Tickets> ticketList = objectMapper.readValue(responseContent, new TypeReference<List<Tickets>>() {});


	        assertEquals(2, ticketList.size()); // Expecting 2 tickets in the response
	     
	    	
	    }
	    
	    @Test
	    @WithMockUser(username="Admin", roles={"User"})
	    public void ProjectResource_ProjectTickets_Member() throws Exception{
	    	user = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	HashSet<Integer> tickets = new HashSet<Integer>();
	    	Tickets ticket = new Tickets();
	    	tickets.add(0);
	    	tickets.add(1);
	    	
	    	
	    
	    
	    	when(UJR.findByusername("Admin")).thenReturn(Optional.of(user));
	    	when(PJR.findById(0)).thenReturn(Optional.of(project));
	    	when(project.getTickets()).thenReturn(tickets);
	    	
	    	when(TJR.findById(0)).thenReturn(Optional.of(ticket));
	    	when(TJR.findById(1)).thenReturn(Optional.of(ticket));
	    	
	    	when(user.getId()).thenReturn(0);
	    	when(user.getRoles()).thenReturn("User");
	    	
	    	HashMap<Integer, String> members = new HashMap<Integer, String>();
	    	members.put(0, "User");
	    	when(project.getUserRole()).thenReturn(members);
	    	
	    
	    	
	    	
	    	
	    
	    	MvcResult mvcResult = mockMvc.perform(get("/projectTickets/{projectid}", 0)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andReturn();
	            // Perform additional assertions on the response if needed
	           
	            
	            
	    	
	        String responseContent = mvcResult.getResponse().getContentAsString();
	        System.out.println("Response: " + responseContent);

	       
	        ObjectMapper objectMapper = new ObjectMapper();
	        List<Tickets> ticketList = objectMapper.readValue(responseContent, new TypeReference<List<Tickets>>() {});


	        assertEquals(2, ticketList.size()); // Expecting 2 tickets in the response
	     
	    	
	    }
	    
	    @Test
	    @WithMockUser(username="Admin", roles={"User"})
	    public void ProjectResource_ProjectTickets_NonMember() throws Exception{
	    	user = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	HashSet<Integer> tickets = new HashSet<Integer>();
	    	Tickets ticket = new Tickets();
	    	tickets.add(0);
	    	tickets.add(1);
	    	
	    	
	    
	    
	    	when(UJR.findByusername("Admin")).thenReturn(Optional.of(user));
	    	when(PJR.findById(0)).thenReturn(Optional.of(project));
	    	when(project.getTickets()).thenReturn(tickets);
	    	
	    	when(TJR.findById(0)).thenReturn(Optional.of(ticket));
	    	when(TJR.findById(1)).thenReturn(Optional.of(ticket));
	    	
	    
	    	when(user.getRoles()).thenReturn("User");
	    	
	    
	    	
	    
	    	
	    	
	    	
	    
	    	MvcResult mvcResult = mockMvc.perform(get("/projectTickets/{projectid}", 0)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andReturn();
	            // Perform additional assertions on the response if needed
	           
	            
	            
	    	
	        String responseContent = mvcResult.getResponse().getContentAsString();
	        System.out.println("Response: " + responseContent);

	       
	       


	        assertTrue(responseContent.isEmpty());
	     
	    	
	    }
	    
	    
	    
	    @Test
	    @WithMockUser(username="User", roles={"Admin"})
	    public void ProjectResource_GetMembers_Admin() throws Exception{
	    	user = Mockito.mock(User.class);
	    	user2 = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	
	    	
	    	
	    
	    
	    	when(UJR.findByusername("User")).thenReturn(Optional.of(user));
	    	when(PJR.findById(0)).thenReturn(Optional.of(project));
	   
	    	
	    	when(UJR.findById(0)).thenReturn(Optional.of(user2));
	    	when(UJR.findById(1)).thenReturn(Optional.of(user));
	    	
	    
	    	when(user2.getId()).thenReturn(0);
	    	when(user.getRoles()).thenReturn("Admin");
	    	
	    	HashMap<Integer, String> members = new HashMap<>();
	    	members.put(0, "Admin");
	    	when(project.getUserRole()).thenReturn(members);
	    	
	    
	    	
	    
	    	
	    	
	    	
	    
	    	MvcResult mvcResult = mockMvc.perform(get("/getMembers/{projectid}", 0)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andReturn();
	            // Perform additional assertions on the response if needed
	           
	            
	            
	    	
	        String responseContent = mvcResult.getResponse().getContentAsString();
	        System.out.println("Response: " + responseContent);

	       
	        ObjectMapper objectMapper = new ObjectMapper();
	        List<UserRetrieval> userRetrieval = objectMapper.readValue(responseContent, new TypeReference<List<UserRetrieval>>() {});


	        assertEquals(1, userRetrieval.size()); // Expecting 2 tickets in the response
	     
	    	
	    }
	    
	    @Test
	    @WithMockUser(username="User", roles={"User"})
	    public void ProjectResource_GetMembers_Member() throws Exception{
	    	user = Mockito.mock(User.class);
	    	user2 = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	
	    	
	    	
	    
	    
	    	when(UJR.findByusername("User")).thenReturn(Optional.of(user));
	    	when(PJR.findById(0)).thenReturn(Optional.of(project));
	   
	    	
	    	when(UJR.findById(0)).thenReturn(Optional.of(user2));
	    	when(UJR.findById(1)).thenReturn(Optional.of(user));
	    	
	    	when(user.getId()).thenReturn(1);
	    	when(user2.getId()).thenReturn(0);
	    	when(user.getRoles()).thenReturn("User");
	    	
	    	HashMap<Integer, String> members = new HashMap<>();
	    	members.put(0, "Admin");
	    	members.put(1, "User");
	    	when(project.getUserRole()).thenReturn(members);
	    	
	    
	    	
	    
	    	
	    	
	    	
	    
	    	MvcResult mvcResult = mockMvc.perform(get("/getMembers/{projectid}", 0)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andReturn();
	            // Perform additional assertions on the response if needed
	           
	            
	            
	    	
	        String responseContent = mvcResult.getResponse().getContentAsString();
	        System.out.println("Response: " + responseContent);

	       
	        ObjectMapper objectMapper = new ObjectMapper();
	        List<UserRetrieval> userRetrieval = objectMapper.readValue(responseContent, new TypeReference<List<UserRetrieval>>() {});


	        assertEquals(2, userRetrieval.size()); // Expecting 2 tickets in the response
	     
	    	
	    }
	    
	    @Test
	    @WithMockUser(username="User", roles={"User"})
	    public void ProjectResource_GetMembers_NonMember() throws Exception{
	    	user = Mockito.mock(User.class);
	    	user2 = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	
	    	
	    	
	    
	    
	    	when(UJR.findByusername("User")).thenReturn(Optional.of(user));
	    	when(PJR.findById(0)).thenReturn(Optional.of(project));
	   
	    	
	    	when(UJR.findById(0)).thenReturn(Optional.of(user2));
	    	when(UJR.findById(1)).thenReturn(Optional.of(user));
	    	
	    	when(user.getId()).thenReturn(1);
	    	when(user2.getId()).thenReturn(0);
	    	when(user.getRoles()).thenReturn("User");
	    	
	    	HashMap<Integer, String> members = new HashMap<>();
	    	members.put(0, "Admin");
	    	
	    	when(project.getUserRole()).thenReturn(members);
	    	
	    
	    	
	    
	    	
	    	
	    	
	    
	    	MvcResult mvcResult = mockMvc.perform(get("/getMembers/{projectid}", 0)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andReturn();
	            // Perform additional assertions on the response if needed
	           
	            
	            
	    	
	        String responseContent = mvcResult.getResponse().getContentAsString();
	        System.out.println("Response: " + responseContent);

	       


	        assertTrue(responseContent.isEmpty()); // Expecting 2 tickets in the response
	     
	    	
	    }
	    
	    
	    
	    @Test
	    @WithMockUser(username="User", roles={"Admin"})
	    public void ProjectResource_GetMember_Admin() throws Exception{
	    	user = Mockito.mock(User.class);
	    	user2 = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	
	    	
	    	
	    
	    
	    	when(UJR.findByusername("User")).thenReturn(Optional.of(user));
	    	when(PJR.findById(0)).thenReturn(Optional.of(project));
	   
	    	
	    	when(UJR.findById(0)).thenReturn(Optional.of(user2));
	    	when(UJR.findById(1)).thenReturn(Optional.of(user));
	    	
	    
	    	when(user2.getId()).thenReturn(0);
	    	when(user.getRoles()).thenReturn("Admin");
	    	
	    	HashMap<Integer, String> members = new HashMap<>();
	    	members.put(0, "Admin");
	    	when(project.getUserRole()).thenReturn(members);
	    	
	    
	    	
	    
	    	
	    	
	    	
	    
	    	MvcResult mvcResult = mockMvc.perform(get("/getMember/{userid}/{projectid}", 0, 0)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andReturn();
	            // Perform additional assertions on the response if needed
	           
	            
	            
	    	
	        String responseContent = mvcResult.getResponse().getContentAsString();
	        System.out.println("Response: " + responseContent);

	       
	        assertTrue(!responseContent.isEmpty());
	     
	    	
	    }
	    
	    @Test
	    @WithMockUser(username="User", roles={"Admin"})
	    public void ProjectResource_GetMember_Member() throws Exception{
	    	user = Mockito.mock(User.class);
	    	user2 = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	
	    	
	    	
	    
	    
	    	when(UJR.findByusername("User")).thenReturn(Optional.of(user));
	    	when(PJR.findById(0)).thenReturn(Optional.of(project));
	   
	    	
	    	when(UJR.findById(0)).thenReturn(Optional.of(user2));
	    	when(UJR.findById(1)).thenReturn(Optional.of(user));
	    	
	    
	    	when(user.getId()).thenReturn(1);
	    	when(user2.getId()).thenReturn(0);
	    	when(user2.getUsername()).thenReturn("Admin");
	    	when(user.getRoles()).thenReturn("User");
	    	
	    	HashMap<Integer, String> members = new HashMap<>();
	    	members.put(0, "Admin");
	    	members.put(1, "User");
	    	when(project.getUserRole()).thenReturn(members);
	    	
	    
	    	
	    
	    	
	    	
	    	
	    
	    	MvcResult mvcResult = mockMvc.perform(get("/getMember/{userid}/{projectid}", 0, 0)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andReturn();
	            // Perform additional assertions on the response if needed
	           
	            
	            
	    	
	        String responseContent = mvcResult.getResponse().getContentAsString();
	        System.out.println("Response GetMember: " + responseContent);

	       
	        assertTrue(!responseContent.isEmpty());
	     
	    	
	    }
	    
	    @Test
	    @WithMockUser(username="User", roles={"User"})
	    public void ProjectResource_GetAuthorizedMembers() throws Exception{
	    	user = Mockito.mock(User.class);
	    	user2 = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	
	    	
	    	when(UJR.findByusername("User")).thenReturn(Optional.of(user));
	    	when(PJR.findById(0)).thenReturn(Optional.of(project));
	   
	    	
	    	when(UJR.findById(0)).thenReturn(Optional.of(user2));
	    	when(UJR.findById(1)).thenReturn(Optional.of(user));
	    	
	    	
	    	when(user2.getId()).thenReturn(0);
	    
	    	
	    	HashMap<Integer, String> members = new HashMap<>();
	    	members.put(0, "Admin");
	    	members.put(1, "User");
	    	when(project.getUserRole()).thenReturn(members);
	    	
	    	
	    
	    	MvcResult mvcResult = mockMvc.perform(get("/authorizedMember/{projectid}", 0)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andReturn();
	            // Perform additional assertions on the response if needed
	           
	            
	            
	    	
	        String responseContent = mvcResult.getResponse().getContentAsString();
	        System.out.println("Response: " + responseContent);

	       
	        ObjectMapper objectMapper = new ObjectMapper();
	        List<UserRetrieval> userRetrieval = objectMapper.readValue(responseContent, new TypeReference<List<UserRetrieval>>() {});


	        assertEquals(1, userRetrieval.size()); // Expecting 2 tickets in the response
	     
	    	
	    }
	    
	    @Test
	    @WithMockUser(username="User", roles={"User"})
	    public void ProjectResource_GetAuthorizedMembers_2() throws Exception{
	    	user = Mockito.mock(User.class);
	    	user2 = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	
	    	
	    	when(UJR.findByusername("User")).thenReturn(Optional.of(user));
	    	when(PJR.findById(0)).thenReturn(Optional.of(project));
	   
	    	
	    	when(UJR.findById(0)).thenReturn(Optional.of(user2));
	    	when(UJR.findById(1)).thenReturn(Optional.of(user));
	    	
	    	
	    	when(user2.getId()).thenReturn(0);
	    
	    	
	    	HashMap<Integer, String> members = new HashMap<>();
	    	members.put(0, "Admin");
	    	members.put(1, "Admin");
	    	when(project.getUserRole()).thenReturn(members);
	    	
	    	
	    
	    	MvcResult mvcResult = mockMvc.perform(get("/authorizedMember/{projectid}", 0)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andReturn();
	            // Perform additional assertions on the response if needed
	           
	            
	            
	    	
	        String responseContent = mvcResult.getResponse().getContentAsString();
	        System.out.println("Response: " + responseContent);

	       
	        ObjectMapper objectMapper = new ObjectMapper();
	        List<UserRetrieval> userRetrieval = objectMapper.readValue(responseContent, new TypeReference<List<UserRetrieval>>() {});


	        assertEquals(2, userRetrieval.size()); // Expecting 2 tickets in the response
	     
	    	
	    }
	    
	    @Test
	    @WithMockUser(username="User", roles={"User"})
	    public void ProjectResource_GetDevelopers() throws Exception{
	    	user = Mockito.mock(User.class);
	    	user2 = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	
	    	
	    	when(UJR.findByusername("User")).thenReturn(Optional.of(user));
	    	when(PJR.findById(0)).thenReturn(Optional.of(project));
	   
	    	
	    	when(UJR.findById(0)).thenReturn(Optional.of(user2));
	    	when(UJR.findById(1)).thenReturn(Optional.of(user));
	    	
	    	
	    	when(user2.getId()).thenReturn(0);
	    
	    	
	    	HashMap<Integer, String> members = new HashMap<>();
	    	members.put(0, "Admin");
	    	members.put(1, "Developer");
	    	when(project.getUserRole()).thenReturn(members);
	    	
	    	
	    
	    	MvcResult mvcResult = mockMvc.perform(get("/authorizedMember/{projectid}", 0)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andReturn();
	            // Perform additional assertions on the response if needed
	           
	            
	            
	    	
	        String responseContent = mvcResult.getResponse().getContentAsString();
	        System.out.println("Response: " + responseContent);

	       
	        ObjectMapper objectMapper = new ObjectMapper();
	        List<UserRetrieval> userRetrieval = objectMapper.readValue(responseContent, new TypeReference<List<UserRetrieval>>() {});


	        assertEquals(1, userRetrieval.size()); // Expecting 2 tickets in the response
	     
	    	
	    }
	    
	   
	    
	    
	    @Test
	    @WithMockUser(username="Admin", roles={"Admin"})
	    public void ProjectResource_CreateProject() throws Exception {
	        // Convert user object to JSON string
	    	
	    	user = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	
	    	when(project.getProjectId()).thenReturn(0);
	    	when(project.getProjectName()).thenReturn("Project name");
	    	when(project.getProjectOwnerId()).thenReturn(0);
	    	when(project.getDescription()).thenReturn("Project description");
	    	
	    	
	    	when(user.getUsername()).thenReturn("Admin");
	    	when(user.getRoles()).thenReturn("Admin");
	  
	    	when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	    	
	        String projectJson = objectMapper.writeValueAsString(project);
	        System.out.println(projectJson);
	        

	        // Perform POST request
	        ResultActions response = mockMvc.perform(post("/createProject")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(projectJson));

	        // Assert the response
	        response.andExpect(status().isCreated());
	        
	    }
	    
	    @Test
	    @WithMockUser(username="Admin", roles={"Admin"})
	    public void ProjectResource_CreateProject_ShortProjectName() throws Exception {
	        // Convert user object to JSON string
	    	
	    	user = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	
	    	when(user.getUsername()).thenReturn("Admin");
	    	
	    	
	    	when(project.getProjectName()).thenReturn("klk");
	    	
	    	
	  
	    	when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	    	
	        String projectJson = objectMapper.writeValueAsString(project);
	        System.out.println(projectJson);
	        

	        // Perform POST request
	        ResultActions response = mockMvc.perform(post("/createProject")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(projectJson));

	        // Assert the response
	        response.andExpect(status().isBadRequest());
	    }
	    
	    @Test
	    @WithMockUser(username="Admin", roles={"User"})
	    public void ProjectResource_CreateProject_NonAdmin() throws ServletException {
// Convert user object to JSON string
	    	
	    	user = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	
	    	when(project.getProjectId()).thenReturn(0);
	    	when(project.getProjectName()).thenReturn("Project name");
	    	when(project.getProjectOwnerId()).thenReturn(0);
	    	when(project.getDescription()).thenReturn("Project description");
	    	
	    	
	    	when(user.getUsername()).thenReturn("Admin");

	  
	    	when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	    	
	        
	        

	        assertThrows(ServletException.class, () -> {
	        	String projectJson = objectMapper.writeValueAsString(project);
		        System.out.println(projectJson);
	        	ResultActions response = mockMvc.perform(post("/createProject")
		                .contentType(MediaType.APPLICATION_JSON)
		                .content(projectJson));
	            // Perform additional assertions on the response if needed
	        });

	      
	    	
	    	
	    }
	    
	    @Test
	    @WithMockUser(username="Admin", roles={"Admin"})
	    public void ProjectResource_ChangeProjectName() throws Exception {
	    
	    	user = Mockito.mock(User.class);
	    	project = Mockito.mock(Project.class);
	    	
	    	String projectName = "Sweet child o mine";
	    	
	    	when(user.getUsername()).thenReturn("Admin");
	    	
	    	when(project.getProjectId()).thenReturn(0);
	    	
	    	
	    	when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	    	when(PJR.findById(project.getProjectId())).thenReturn(Optional.of(project));
	    	
	    	when(project.getProjectName()).thenReturn(projectName);
	    	
	    	String projectJson = objectMapper.writeValueAsString(project);
	        System.out.println(projectJson);
	        

	        // Perform POST request
	        ResultActions response = mockMvc.perform(put("/changeProjectName/{projectid}", 0)
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(projectName));
	    	
	        
	        
	        verify(project).setProjectName(projectName);
	    	
	        response.andExpect(status().isOk());
	    	
	    	
	    	
	    }
	    
	    @Test
	    @WithMockUser(username="Admin", roles={"Admin"})
	    public void ProjectResource_ChangeProjectName_ShortName() throws Exception {
	        // Mock the user and project objects
	        user = Mockito.mock(User.class);
	        project = Mockito.mock(Project.class);
	        
	        String projectName = "qwq"; // Short name to trigger forbidden condition
	        
	        	     
	        
	        when(UJR.findByusername("Admin")).thenReturn(Optional.of(user));
	        when(PJR.findById(0)).thenReturn(Optional.of(project));
	        
	      
	        
	        
	        
	        // Perform PUT request
	        ResultActions response = mockMvc.perform(put("/changeProjectName/{projectid}", 0)
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(projectName));
	        
	        // Verify that the response status is Forbidden
	        response.andExpect(status().isBadRequest());
	  
	        
	        // Verify that setProjectName was never called because the condition fails
	        verify(project, never()).setProjectName(anyString());
	    }

	    
	    @Test
	    @WithMockUser(username = "Admin", roles = {"Admin"})
	    public void ProjectResource_AddUser() throws Exception {
	        user = Mockito.mock(User.class);
	        user2 = Mockito.mock(User.class);
	        project = Mockito.mock(Project.class);
	     

	        when(user.getUsername()).thenReturn("Admin");
	        when(user.getRoles()).thenReturn("Admin");

	        int userId = user2.getId();
	        int projectId = project.getProjectId();


	 
	        when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	        when(UJR.findById(user2.getId())).thenReturn(Optional.of(user2));
	        when(PJR.findById(0)).thenReturn(Optional.of(project));
	        

	        // Construct the JSON content
	        String jsonContent = String.format("{\"userId\": %d, \"projectId\": %d}", userId, projectId);

	        // Perform the PUT request with path variables and JSON content
	        ResultActions response = mockMvc.perform(put("/addUser/{userid}/{projectid}", userId, projectId)
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(jsonContent));

	        

	        // Verify the response status
	        response.andExpect(status().isOk());
	        
	        // Verify the save operation was called on the repository
	        verify(PJR, times(1)).save(project);
	  
	    }

	    
	    @Test
	    @WithMockUser(username="Admin", roles={"Admin"})
	    public void ProjectResource_AddUser_UserNotFound() throws Exception {
	    	user = Mockito.mock(User.class);
	        user2 = Mockito.mock(User.class);
	        project = Mockito.mock(Project.class);

	        when(user.getUsername()).thenReturn("Admin");

	        int userId = user2.getId();
	        int projectId = project.getProjectId();

	        HashMap<Integer, String> members = new HashMap<>();
	        members.put(user2.getId(), "User");

	 
	        when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	        when(UJR.findById(15)).thenReturn(Optional.empty());
	        when(PJR.findById(0)).thenReturn(Optional.of(project));
	        
	        /*
	        //when(PJR.findById(0).get().getUserRole()).thenReturn(hash);
	        //when(PJR.save(project)).thenReturn(project);
	        */

	        // Construct the JSON content
	        String jsonContent = String.format("{\"userId\": %d, \"projectId\": %d}", userId, projectId);

	        // Perform the PUT request with path variables and JSON content
	        ResultActions response = mockMvc.perform(put("/addUser/{userid}/{projectid}", userId, projectId)
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(jsonContent));

	        

	        // Verify the response status
	        response.andExpect(status().isNotFound());
	        
	        // Verify the save operation was called on the repository
	        verify(PJR, never()).save(project);
	      
	    	
	       
	    }
	    
	   
	    
	    @Test
	    @WithMockUser(username="Admin", roles={"Admin"})
	    public void ProjectResource_AddUser_UserInProject() throws Exception {
	    	
	    	user = Mockito.mock(User.class);
	        user2 = Mockito.mock(User.class);
	        project = Mockito.mock(Project.class);
	        
	        
	        when(user.getUsername()).thenReturn("Admin");
	        
	        when(user2.getId()).thenReturn(2);

	        int userId = user2.getId();
	        int projectId = project.getProjectId();
	        
	        HashMap<Integer, String> members = new HashMap<Integer, String>();
	        members.put(2, "User");
	        
	        when(project.getUserRole()).thenReturn(members);
	        
	

	 
	        when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	        when(UJR.findById(user2.getId())).thenReturn(Optional.of(user2));
	        when(PJR.findById(0)).thenReturn(Optional.of(project));
	        //when(PJR.findById(0).get().getUserRole()).thenReturn(hash);
	        //when(PJR.save(project)).thenReturn(project);
	        

	        // Construct the JSON content
	        String jsonContent = String.format("{\"userId\": %d, \"projectId\": %d}", userId, projectId);

	   
	        
	        // Verify the save operation was called on the repository
	        verify(PJR, never()).save(project);
	    
	     
	    	
	    	
			
			ResultActions response = mockMvc.perform(put("/addUser/{userid}/{projectid}", userId, projectId)
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(jsonContent));
				
			response.andExpect(status().isNotAcceptable());

	       
	    }
	    
	    @Test
	    @WithMockUser(username="Admin", roles={"User"})
	    public void ProjectResource_AddUser_AddingInvalidRole() throws Exception {
	    	
	    	user = Mockito.mock(User.class);
	        user2 = Mockito.mock(User.class);
	        project = Mockito.mock(Project.class);
	        
	        when(user.getId()).thenReturn(1);
	        when(user.getUsername()).thenReturn("Admin");
	        when(user.getRoles()).thenReturn("User");
	        when(user2.getId()).thenReturn(2);

	        int userId = user2.getId();
	        int projectId = project.getProjectId();
	        
	        HashMap<Integer, String> members = new HashMap<Integer, String>();
	        members.put(1, "User");
	        
	        when(project.getUserRole()).thenReturn(members);
	        
	

	 
	        when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	        when(UJR.findById(user2.getId())).thenReturn(Optional.of(user2));
	        when(PJR.findById(0)).thenReturn(Optional.of(project));
	        //when(PJR.findById(0).get().getUserRole()).thenReturn(hash);
	        //when(PJR.save(project)).thenReturn(project);
	        

	        // Construct the JSON content
	        String jsonContent = String.format("{\"userId\": %d, \"projectId\": %d}", userId, projectId);

	   
	        
	        // Verify the save operation was called on the repository
	        verify(PJR, never()).save(project);
	    
	     
	    	
	    	
			
			ResultActions response = mockMvc.perform(put("/addUser/{userid}/{projectid}", userId, projectId)
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(jsonContent));
				
			response.andExpect(status().isForbidden());

	       
	    }
	    
	    @Test
	    @WithMockUser(username = "Admin", roles = {"User"})
	    public void ProjectResource_KickUser() throws Exception {
	        // Mock users and project
	        User user = Mockito.mock(User.class);
	        User user2 = Mockito.mock(User.class);
	        Project project = Mockito.mock(Project.class);

	        // Mock user and project IDs and roles
	        when(user.getId()).thenReturn(0);
	        when(user2.getId()).thenReturn(1);
	        when(project.getProjectId()).thenReturn(0);
	        when(project.getProjectOwnerId()).thenReturn(0);
	        when(user.getUsername()).thenReturn("Admin");
	        
	        HashMap<Integer, String> members = new HashMap<Integer, String>();
	    	members.put(0, "Admin");
	    	members.put(1, "User");
	    	
	        when(project.getUserRole()).thenReturn(members);

	        // Set up user roles in the project (if directly needed)
	     
	        System.out.println(project.getUserRole().containsKey(user.getId()));

	        // Mock repository interactions
	        when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	        when(PJR.findById(0)).thenReturn(Optional.of(project));
	        when(PJR.save(project)).thenReturn(project);

	        // Construct the JSON content
	        String jsonContent = String.format("{\"userId\": %d, \"projectId\": %d}", user2.getId(), project.getProjectId());

	        // Perform the PUT request with path variables and JSON content
	        ResultActions response = mockMvc.perform(put("/kickUser/{userid}/{projectid}", user2.getId(), project.getProjectId())
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(jsonContent));

	        // Verify the response status
	        response.andExpect(status().isOk());

	        // Verify that the save operation on the repository was called
	        verify(PJR, times(1)).save(project);

	        // Optionally, verify other interactions or assertions as needed
	    }
	    
	    @Test
	    @WithMockUser(username = "Admin", roles = {"User"})
	    public void ProjectResource_KickUser_NonAdmin() throws Exception {
	        // Mock users and project
	        User user = Mockito.mock(User.class);
	        User user2 = Mockito.mock(User.class);
	        Project project = Mockito.mock(Project.class);

	        // Mock user and project IDs and roles
	        when(user.getId()).thenReturn(0);
	        when(user2.getId()).thenReturn(1);
	        when(project.getProjectId()).thenReturn(0);
	        when(project.getProjectOwnerId()).thenReturn(0);
	        when(user.getUsername()).thenReturn("Admin");
	        when(user.getRoles()).thenReturn("Developer");
	      
	 
	        HashMap<Integer, String> members = new HashMap<Integer, String>();
	    	members.put(0, "User");
	    	members.put(1, "User");
	        when(project.getUserRole()).thenReturn(members);

	        // Set up user roles in the project (if directly needed)
	     
	        System.out.println(project.getUserRole().containsKey(user.getId()));

	        // Mock repository interactions
	        when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	        when(PJR.findById(0)).thenReturn(Optional.of(project));
	        //when(PJR.save(project)).thenReturn(project);

	        // Construct the JSON content
	        String jsonContent = String.format("{\"userId\": %d, \"projectId\": %d}", user2.getId(), project.getProjectId());

	        // Perform the PUT request with path variables and JSON content
	        ResultActions response = mockMvc.perform(put("/kickUser/{userid}/{projectid}", user2.getId(), project.getProjectId())
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(jsonContent));

	        // Verify the response status
	        response.andExpect(status().isForbidden());

	        // Verify that the save operation on the repository was called
	        verify(PJR, never()).save(project);

	        // Optionally, verify other interactions or assertions as needed
	    }
	    
	    @Test
	    @WithMockUser(username = "Admin", roles = {"Admin"})
	    public void ProjectResource_KickUser_NoOwnerKick() throws Exception {
	        // Mock users and project
	        User user = Mockito.mock(User.class);
	      
	        Project project = Mockito.mock(Project.class);

	        // Mock user and project IDs and roles
	        when(user.getId()).thenReturn(0);
	        when(project.getProjectId()).thenReturn(0);
	        when(project.getProjectOwnerId()).thenReturn(0);
	        when(user.getUsername()).thenReturn("Admin");
	       
	        HashMap<Integer, String> members = new HashMap<Integer, String>();
	    	members.put(0, "Admin");
	    	members.put(1, "User");
	        when(project.getUserRole()).thenReturn(members);

	        // Set up user roles in the project (if directly needed)
	     
	        

	        // Mock repository interactions
	        when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	        when(PJR.findById(0)).thenReturn(Optional.of(project));
	       

	        // Construct the JSON content
	        String jsonContent = String.format("{\"userId\": %d, \"projectId\": %d}", user.getId(), project.getProjectId());

	        // Perform the PUT request with path variables and JSON content
	        ResultActions response = mockMvc.perform(put("/kickUser/{userid}/{projectid}", user.getId(), project.getProjectId())
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(jsonContent));

	        // Verify the response status
	        response.andExpect(status().isForbidden());

	        // Verify that the save operation on the repository was called
	        verify(PJR, never()).save(project);

	        // Optionally, verify other interactions or assertions as needed
	    }
	    
	    @Test
	    @WithMockUser(username = "Admin", roles = {"Admin"})
	    public void ProjectResource_ChangeRole() throws Exception {
	    	// Mock users and project
	        User user = Mockito.mock(User.class);
	      
	        Project project = Mockito.mock(Project.class);
	        String newRole = "Mod";
	        
	        // Mock user and project IDs and roles
	        when(user.getId()).thenReturn(0);
	        when(project.getProjectId()).thenReturn(0);
	        when(project.getProjectOwnerId()).thenReturn(0);
	        when(user.getUsername()).thenReturn("Admin");
	        when(user.getRoles()).thenReturn("Admin");
	       
	        HashMap<Integer, String> members = new HashMap<Integer, String>();
	    	members.put(0, "Admin");
	    	members.put(1, "User");
	        when(project.getUserRole()).thenReturn(members);
	        when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	        when(PJR.findById(0)).thenReturn(Optional.of(project));
	        
	    

	        // Perform the PUT request with path variables and JSON content
	        ResultActions response = mockMvc.perform(put("/changeRole/{userid}/{projectid}", 1, project.getProjectId())
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(newRole));

	        // Verify the response status
	        response.andExpect(status().isOk());

	        // Verify that the save operation on the repository was called
	    
	        verify(PJR, times(1)).save(project);
	    	
	    }
	    
	    @Test
	    @WithMockUser(username = "Admin", roles = {"Admin"})
	    public void ProjectResource_ChangeRole_InvalidRole() throws Exception {
	    	// Mock users and project
	        User user = Mockito.mock(User.class);
	      
	        Project project = Mockito.mock(Project.class);
	        String newRole = "Mode";
	        
	        // Mock user and project IDs and roles
	        when(user.getId()).thenReturn(0);
	        when(project.getProjectId()).thenReturn(0);
	        when(project.getProjectOwnerId()).thenReturn(0);
	        when(user.getUsername()).thenReturn("Admin");
	        when(user.getRoles()).thenReturn("Admin");
	       
	        HashMap<Integer, String> members = new HashMap<Integer, String>();
	    	members.put(0, "Admin");
	    	members.put(1, "User");
	        when(project.getUserRole()).thenReturn(members);
	        when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	        when(PJR.findById(0)).thenReturn(Optional.of(project));
	        
	    

	        // Perform the PUT request with path variables and JSON content
	        ResultActions response = mockMvc.perform(put("/changeRole/{userid}/{projectid}", 1, project.getProjectId())
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(newRole));

	        // Verify the response status
	        response.andExpect(status().isForbidden());

	        // Verify that the save operation on the repository was called
	    
	        verify(PJR,  never()).save(project);
	    	
	    }
	    
	    @Test
	    @WithMockUser(username = "Admin", roles = {"User"})
	    public void ProjectResource_ChangeRole_InvalidRoleCall() throws Exception {
	    	// Mock users and project
	        User user = Mockito.mock(User.class);
	      
	        Project project = Mockito.mock(Project.class);
	        String newRole = "Mod";
	        
	        // Mock user and project IDs and roles
	      
	        when(project.getProjectId()).thenReturn(0);
	       
	        when(user.getUsername()).thenReturn("Admin");
	       
	       
	       
	        when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	        when(PJR.findById(0)).thenReturn(Optional.of(project));
	        
	    

	        
		   ResultActions response = mockMvc.perform(put("/changeRole/{userid}/{projectid}", 1, project.getProjectId())
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(newRole));
	            // Perform additional assertions on the response if needed
	       
		    response.andExpect(status().isForbidden());
	        
	        verify(PJR,  never()).save(project);
	    	
	    }
	    
	    
	    //Future tests for project deletion
	    
	    
	    @Test
	    @WithMockUser(username = "Admin", roles = {"Admin"})
	    public void ProjectResource_DeleteProject_Admin() throws Exception{
	    	User user = Mockito.mock(User.class);
	        Project project = Mockito.mock(Project.class);
	        Tickets ticket = Mockito.mock(Tickets.class);
	        
	      
	        when(project.getProjectId()).thenReturn(0);
	        when(project.getProjectOwnerId()).thenReturn(1);
	        when(user.getUsername()).thenReturn("Admin");
	        when(user.getRoles()).thenReturn("Admin");
	       
	        
	        when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	        when(PJR.findById(0)).thenReturn(Optional.of(project));
	        when(TJR.findById(0)).thenReturn(Optional.of(ticket));
	        
	        String projectJson = objectMapper.writeValueAsString(project);
	        
	        ResultActions response = mockMvc.perform(delete("/deleteProject/{projectid}", project.getProjectId())
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(projectJson));
	        
	        response.andExpect(status().isOk());
	        
	        
	    	
	    }
	    
	    @Test
	    @WithMockUser(username = "Admin", roles = {"User"})
	    public void ProjectResource_DeleteProject_ProjectOwner() throws Exception{
	    	User user = Mockito.mock(User.class);
	        Project project = Mockito.mock(Project.class);
	        Tickets ticket = Mockito.mock(Tickets.class);
	        
	        when(user.getId()).thenReturn(0);
	        when(project.getProjectId()).thenReturn(0);
	        when(project.getProjectOwnerId()).thenReturn(0);
	        when(user.getUsername()).thenReturn("Admin");
	        when(user.getRoles()).thenReturn("User");
	       
	        
	        when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	        when(PJR.findById(0)).thenReturn(Optional.of(project));
	        when(TJR.findById(0)).thenReturn(Optional.of(ticket));
	        
	        String projectJson = objectMapper.writeValueAsString(project);
	        
	        ResultActions response = mockMvc.perform(delete("/deleteProject/{projectid}", project.getProjectId())
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(projectJson));
	        
	        response.andExpect(status().isOk());
	        
	        
	    	
	    }
	    
	    @Test
	    @WithMockUser(username = "Admin", roles = {"User"})
	    public void ProjectResource_DeleteProject_ProjectNotFound() throws Exception{
	    	User user = Mockito.mock(User.class);
	        Project project = Mockito.mock(Project.class);
	        Tickets ticket = Mockito.mock(Tickets.class);
	        
	      
	        when(project.getProjectId()).thenReturn(1);
	        when(project.getProjectOwnerId()).thenReturn(0);
	        when(user.getUsername()).thenReturn("Admin");
	       
	       
	        
	        when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	        when(PJR.findById(1)).thenReturn(Optional.empty());
	        when(TJR.findById(0)).thenReturn(Optional.of(ticket));
	        
	        String projectJson = objectMapper.writeValueAsString(project);
	        
	        ResultActions response = mockMvc.perform(delete("/deleteProject/{projectid}", 1)
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(projectJson));
	        
	        response.andExpect(status().isNotFound());
	        
	        
	    	
	    }
	    
	    @Test
	    @WithMockUser(username = "Admin", roles = {"User"})
	    public void ProjectResource_DeleteProject_Forbidden() throws Exception{
	    	User user = Mockito.mock(User.class);
	        Project project = Mockito.mock(Project.class);
	        Tickets ticket = Mockito.mock(Tickets.class);
	        
	        when(user.getId()).thenReturn(0);
	        when(project.getProjectId()).thenReturn(0);
	        when(project.getProjectOwnerId()).thenReturn(1);
	        when(user.getUsername()).thenReturn("Admin");
	        when(user.getRoles()).thenReturn("User");
	       
	        
	        when(UJR.findByusername(user.getUsername())).thenReturn(Optional.of(user));
	        when(PJR.findById(0)).thenReturn(Optional.of(project));
	        when(TJR.findById(0)).thenReturn(Optional.of(ticket));
	        
	        String projectJson = objectMapper.writeValueAsString(project);
	        
	        ResultActions response = mockMvc.perform(delete("/deleteProject/{projectid}", project.getProjectId())
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(projectJson));
	        
	        response.andExpect(status().isForbidden());
	        
	        
	    	
	    }
	    
	    
	    
	    


	    
	    
	    

}

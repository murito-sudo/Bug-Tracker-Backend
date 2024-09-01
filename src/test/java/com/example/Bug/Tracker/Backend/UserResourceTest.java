package com.example.Bug.Tracker.Backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import com.example.Bug.Tracker.Backend.User.User;
import com.example.Bug.Tracker.Backend.User.UserJPARepository;
import com.example.Bug.Tracker.Backend.User.UserResource;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;

@WebMvcTest(controllers = UserResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@EnableMethodSecurity(prePostEnabled = true)
@ActiveProfiles("test")
class UserResourceTest {

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserJPARepository UJR;
    
    @MockBean
    private ProjectJPARepository PJR;
    
    @MockBean
    private PasswordEncoder encoder;
    
    @Mock
    private User user;
    
    @Mock
    private User admin;
    
    @Mock
    private Project project;
    
    @BeforeEach
    public void init() {
    	MockitoAnnotations.openMocks(this);
    	user = User.builder()
                .username("La creta")
                .password("klktudiced")
                .roles("User")
                .build();
    	
    	admin = User.builder()
                .username("Admin")
                .password("perfect")
                .roles("Admin")
                .build();
    	
    	project = Project.builder().projectId(0).projectName("Test Project")
    			.description("Hello Description").
    			tickets(new HashSet<Integer>()).projectOwnerId(0)
    			.userRole(new HashMap<Integer, String>()).build();
    	
    	
    
       
    }
    
    @Test
    public void UserResource_Register() throws Exception {
       
    	
    	user = Mockito.mock(User.class);
    	when(user.getUsername()).thenReturn("User");
    	when(user.getPassword()).thenReturn("Perfect");
    	when(user.getRoles()).thenReturn("User");
    	
    	
    	
        String userJson = objectMapper.writeValueAsString(user);
        System.out.println(userJson);
        

        // Perform POST request
        ResultActions response = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));

        // Assert the response
        response.andExpect(status().isCreated());
    }
    
    @Test
    public void UserResource_Register_NonUniqueUsername() throws Exception {
       
    	
    	user = Mockito.mock(User.class);
    	when(user.getUsername()).thenReturn("User");
    	when(user.getPassword()).thenReturn("Perfect");
    	when(user.getRoles()).thenReturn("User");
    	User existingUser = new User(3, "User", "Perfect", "User");
    	when(UJR.findByusername("User")).thenReturn(Optional.of(existingUser));    	
    	
        String userJson = objectMapper.writeValueAsString(user);
        System.out.println(userJson);
        

        // Perform POST request
        ResultActions response = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));

        
        // Assert the response
        response.andExpect(status().isForbidden());
        
     
    }
    
    
    @Test
    public void UserResource_Register_ShortPassword() throws Exception {
       
    	
    
    	
    	user = Mockito.mock(User.class);
    	when(user.getUsername()).thenReturn("User");
    	when(user.getPassword()).thenReturn("Pe");
    	when(user.getRoles()).thenReturn("User");
        String userJson = objectMapper.writeValueAsString(user);
        System.out.println(userJson);
        

        // Perform POST request
        ResultActions response = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));

        // Assert the response
        response.andExpect(status().isBadRequest());
    }
    
    @Test
    public void UserResource_Register_InvalidRole() throws Exception {
       
    	
    
    	
    	user = Mockito.mock(User.class);
    	when(user.getUsername()).thenReturn("User");
    	when(user.getPassword()).thenReturn("Perfect");
    	when(user.getRoles()).thenReturn("InvalidRole");
        String userJson = objectMapper.writeValueAsString(user);
        System.out.println(userJson);
        

        // Perform POST request
        ResultActions response = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));

        // Assert the response
        response.andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(username="La creta", roles={"User"})
    public void UserResource_UpdateUser() throws Exception {
        // Convert user object to JSON string
    	user = Mockito.mock(User.class);
    	
    	when(user.getPassword()).thenReturn("Mañilon");
    	when(user.getRoles()).thenReturn("User");
    	
    	
    	when(UJR.findByusername("La creta")).thenReturn(Optional.of(user));
    	when(user.getUsername()).thenReturn("Changing us");
    	
    	
    	
        String userJson = objectMapper.writeValueAsString(user);
        System.out.println(userJson);
        
        
        ResultActions response = mockMvc.perform(put("/updateUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));

        // Assert the response
        response.andExpect(status().isCreated());
    }
    
    @Test
    @WithMockUser(username="Admin", roles={"Admin"})
    public void UserResource_AssignRole() throws Exception {
        // Convert user object to JSON string
    	user = Mockito.mock(User.class);
    	admin = Mockito.mock(User.class);
    	String role = "Developer";
  
    	
    	
    	when(user.getId()).thenReturn(0);
    	when(user.getUsername()).thenReturn("La creta");
    	when(user.getPassword()).thenReturn("Mañilon");
    	when(user.getRoles()).thenReturn("User");
    	
    	lenient().when(admin.getId()).thenReturn(1);
    	lenient().when(admin.getUsername()).thenReturn("Admin");
    	lenient().when(admin.getPassword()).thenReturn("perfect");
    	lenient().when(admin.getRoles()).thenReturn("Admin");
    	
    	
    	when(UJR.findById(user.getId())).thenReturn(Optional.of(user));
    	when(UJR.findByusername("Admin")).thenReturn(Optional.of(admin));
    	
    	when(user.getRoles()).thenReturn(role);
    	
        String userJson = objectMapper.writeValueAsString(user);
        
     
        ResultActions response = mockMvc.perform(put("/AssignRole/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(role));
        
        verify(user).setRoles(role);

        // Assert the response
        response.andExpect(status().isOk());
        
        
    }
    
    @Test
    @WithMockUser(username="Admin", roles={"Admin"})
    public void UserResource_AssignRole_InvalidRole() throws Exception {
        // Convert user object to JSON string
    	user = Mockito.mock(User.class);
    	admin = Mockito.mock(User.class);
    	String role = "This is not a role";
  
    	
    	
    	when(user.getId()).thenReturn(0);
    	when(user.getUsername()).thenReturn("La creta");
    	when(user.getPassword()).thenReturn("Mañilon");
    	when(user.getRoles()).thenReturn("User");
    	
    	lenient().when(admin.getId()).thenReturn(1);
    	lenient().when(admin.getUsername()).thenReturn("Admin");
    	lenient().when(admin.getPassword()).thenReturn("perfect");
    	lenient().when(admin.getRoles()).thenReturn("Admin");
    	
    	
    	when(UJR.findById(user.getId())).thenReturn(Optional.of(user));
    	when(UJR.findByusername("Admin")).thenReturn(Optional.of(admin));
    	
    	when(user.getRoles()).thenReturn(role);
    	
        String userJson = objectMapper.writeValueAsString(user);
        System.out.println("De velda" + userJson);
        
     
        ResultActions response = mockMvc.perform(put("/AssignRole/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(role));

        // Assert the response
        response.andExpect(status().isForbidden());
    }
    
    
    //User role not authorized to assign roles, throwing ServletException
    @Test
    @WithMockUser(username="Admin", roles={"Developer"})
    public void UserResource_update_NonAdminAssign() throws Exception  {
    
    	
    	user = Mockito.mock(User.class);
    	admin = Mockito.mock(User.class);
    	String role = "Developer";
  
    	
    	
    	when(user.getId()).thenReturn(0);
    	when(user.getUsername()).thenReturn("La creta");
    	when(user.getPassword()).thenReturn("Mañilon");
    	when(user.getRoles()).thenReturn("User");
    	
    	lenient().when(admin.getId()).thenReturn(1);
    	lenient().when(admin.getUsername()).thenReturn("Admin");
    	lenient().when(admin.getPassword()).thenReturn("perfect");
    	lenient().when(admin.getRoles()).thenReturn("Admin");
    	
    	
    	when(UJR.findById(user.getId())).thenReturn(Optional.of(user));
    	when(UJR.findByusername("Admin")).thenReturn(Optional.of(admin));
    	
    	when(user.getRoles()).thenReturn(role);
    	
        String userJson = objectMapper.writeValueAsString(user);
        System.out.println("De velda" + userJson);
        
    

        verify(user, never()).setRoles(role);
		assertThrows(ServletException.class, () -> {
            ResultActions response = mockMvc.perform(put("/AssignRole/{id}", 0)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(role));
            // Perform additional assertions on the response if needed
        });
    	  
    }
    
    @Test
    @WithMockUser(username="User", roles={"Admin"})
    public void UserResource_deleteUser() throws Exception  {
    
    	
    	user = Mockito.mock(User.class);
    	project = Mockito.mock(Project.class);
    
    	
    	when(user.getId()).thenReturn(0);
  
    	when(UJR.findById(user.getId())).thenReturn(Optional.of(user));
    	when(PJR.findByUserRoleKey(user.getId())).thenReturn(List.of(project));
    	
        
    	String userJson = objectMapper.writeValueAsString(user);

        
		
        ResultActions response = mockMvc.perform(delete("/deleteUser/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
        
        verify(UJR, times(1)).delete(user);
     
		response.andExpect(status().isOk()); 
    }
    
    
    //Removing deleted user from projects
    @Test
    @WithMockUser(username="User", roles={"Admin"})
    public void UserResource_deleteUser_2() throws Exception  {
    
    	
    	user = Mockito.mock(User.class);
    	project = Mockito.mock(Project.class);
    
    	
    	when(user.getId()).thenReturn(0);
    	when(project.getProjectOwnerId()).thenReturn(0);
  
    	when(UJR.findById(user.getId())).thenReturn(Optional.of(user));
    	when(PJR.findByUserRoleKey(user.getId())).thenReturn(List.of(project));
    	
    	
    	
        
    	String userJson = objectMapper.writeValueAsString(user);

        
		
        ResultActions response = mockMvc.perform(delete("/deleteUser/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
        
        verify(UJR, times(1)).delete(user);
        verify(PJR, times(1)).save(project);
        verify(project, times(1)).setProjectOwnerId(anyInt());
     
		response.andExpect(status().isOk()); 
    }
    
    
    
    @Test
    @WithMockUser(username="User", roles={"Admin"})
    public void UserResource_deleteUser_UserNotFound() throws Exception  {
    
    	
    	user = Mockito.mock(User.class);
    	project = Mockito.mock(Project.class);
    
    	
    	when(user.getId()).thenReturn(1);
  
    	when(UJR.findById(0)).thenReturn(Optional.empty());
    	
    	
        
    	String userJson = objectMapper.writeValueAsString(user);

        
		
        ResultActions response = mockMvc.perform(delete("/deleteUser/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
        
        verify(UJR, never()).delete(user);
     
		response.andExpect(status().isNotFound()); 
    }
    
    
}

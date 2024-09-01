package com.example.Bug.Tracker.Backend.helper;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.Bug.Tracker.Backend.Projects.Project;
import com.example.Bug.Tracker.Backend.User.UserJPARepository;

public class MethodsHelper {
	
	@Autowired
	static UserJPARepository UJR;
	
	
	public static boolean isValidRole(String role) {
		return role.equals("Admin") || role.equals("Developer") || role.equals("Guest") 
				|| role.equals("Mod") || role.equals("User");
	}
	
	public static boolean hasAdminRole(String role) {
		return role.equals("Admin");
	}
	
	public static boolean hasAuthorizedRoles(String s) {
		return s.equals("Admin") || s.equals("Mod");
	}
	
	public static boolean isUserInProject(Project project, int id) {
		return project.getUserRole().containsKey(id);
	}
	
	public static boolean isDeveloper(String role) {
		return role.equals("Developer");
	}
	
	public static String getUserName(int id) {
		return UJR.findById(id).get().getUsername();
	}

}

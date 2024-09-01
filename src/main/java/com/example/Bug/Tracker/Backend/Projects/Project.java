package com.example.Bug.Tracker.Backend.Projects;

import java.util.HashMap;
import java.util.HashSet;


import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "projects")
@Builder
@NoArgsConstructor // Default no-args constructor for JPA
@AllArgsConstructor // All-args constructor for convenience
public class Project {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int projectId;
	@NotNull
	@Size(min = 0)
	@Size(max = 200)
	String description;
	@NotNull
	@Size(min = 5)
	@Size(max = 50)
	String projectName;
	@NotNull
	HashSet<Integer> tickets;
	@NotNull
	int projectOwnerId;
	@ElementCollection()
	HashMap<Integer, String> userRole; // "Admin", "User"
	
	
	
	


	public int getProjectId() {
		return projectId;
	}



	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}



	public String getProjectName() {
		return projectName;
	}



	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}



	public HashSet<Integer> getTickets() {
		return tickets;
	}



	public void setTickets(HashSet<Integer> tickets) {
		this.tickets = tickets;
	}



	public int getProjectOwnerId() {
		return projectOwnerId;
	}



	public void setProjectOwnerId(int projectOwnerId) {
		this.projectOwnerId = projectOwnerId;
	}



	public HashMap<Integer, String> getUserRole() {
		return userRole;
	}



	public void setUserRole(HashMap<Integer, String> userRole) {
		this.userRole = userRole;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}

	
	

	
	
}

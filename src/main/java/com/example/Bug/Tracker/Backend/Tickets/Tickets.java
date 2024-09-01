package com.example.Bug.Tracker.Backend.Tickets;


import java.time.LocalDateTime;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tickets")
@Data
@Builder
@NoArgsConstructor // Default no-args constructor for JPA
@AllArgsConstructor // All-args constructor for convenience
public class Tickets {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private int id;
	
	
	@Size(min = 5)
	@Size(max = 50)
	private String ticketTitle;
	@Size(min = 5)
	@Size(max = 50)
	private String ticketDescription;
	
	@NotNull
	private int developerId;
	@NotNull
	private int submitterId;
	@NotNull
	private int projectId;
	@Min(0)
	@Max(5)
	@NotNull
	private int priority; // 0 being least important 5 being most important
	
	
	@Min(-1)
	@Max(3)
	@NotNull
	private int status; // -1 = not solved, 0 = cancelled, 1 = in progress, 2 = final check, 3 = solved
	@NotNull
	private String type;
	
	private LocalDateTime createdDate;
	
	private List<Integer> ticketHistory;
	
	private List<Integer> ticketComments;
	
	 @OneToMany(cascade = CascadeType.ALL)
	private List<TicketFile> uploadedFiles;
	
	

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTicketTitle() {
		return ticketTitle;
	}

	public void setTicketTitle(String ticketTitle) {
		this.ticketTitle = ticketTitle;
	}

	public String getTicketDescription() {
		return ticketDescription;
	}

	public void setTicketDescription(String ticketDescription) {
		this.ticketDescription = ticketDescription;
	}

	public int getDeveloperId() {
		return developerId;
	}

	public void setDeveloperId(int developerId) {
		this.developerId = developerId;
	}


	public int getSubmitterId() {
		return submitterId;
	}

	public void setSubmitterId(int submitterId) {
		this.submitterId = submitterId;
	}



	public int getProject() {
		return projectId;
	}

	public void setProject(int project) {
		this.projectId = project;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public List<Integer> getTicketHistory() {
		return ticketHistory;
	}

	public void setTicketHistory(List<Integer> ticketHistory) {
		this.ticketHistory = ticketHistory;
	}

	public List<Integer> getTicketComments() {
		return ticketComments;
	}

	public void setTicketComments(List<Integer> ticketComments) {
		this.ticketComments = ticketComments;
	}

   public List<TicketFile> getFiles(){
	   return uploadedFiles;
   }
	
	
	
}
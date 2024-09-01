package com.example.Bug.Tracker.Backend.Tickets;


import java.time.ZonedDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor // Default no-args constructor for JPA
@AllArgsConstructor // All-args constructor for convenience
public class TicketComment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int commentId;
	
	
	@NotNull
	private int ticketid;
	@NotNull
	private int commenterId;
	
	@NotNull
	@Size(min = 5)
	@Size(max = 255)
	private String Message;
	
	private ZonedDateTime Created;
	
	

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public int getTicketid() {
		return ticketid;
	}

	public void setTicketid(int ticketid) {
		this.ticketid = ticketid;
	}

	public int getCommenterId() {
		return commenterId;
	}

	public void setCommenterId(int commenterId) {
		this.commenterId = commenterId;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public ZonedDateTime getCreated() {
		return Created;
	}

	public void setCreated(ZonedDateTime created) {
		Created = created;
	}

	


}

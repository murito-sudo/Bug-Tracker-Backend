package com.example.Bug.Tracker.Backend.Tickets;

import java.time.ZonedDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ticket_history")
@Data
@Builder
@NoArgsConstructor // Default no-args constructor for JPA
@AllArgsConstructor // All-args constructor for convenience
public class TicketHistory {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ticketHistoryId;
	
	@NotNull
	private int ticketId;
	@NotNull
	@Size(min = 5)
	@Size(max = 100)
	private String property;
	@NotNull
	@Size(min = 0)
	@Size(max = 50)
	private String oldVal;
	@NotNull
	@Size(min = 0)
	@Size(max = 50)
	private String newVal;
	private ZonedDateTime updateDate;
	
	

	public int getTicketHistoryId() {
		return ticketHistoryId;
	}

	public void setTicketHistoryId(int ticketHistoryId) {
		this.ticketHistoryId = ticketHistoryId;
	}

	public int getTicketId() {
		return ticketId;
	}

	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getOldVal() {
		return oldVal;
	}

	public void setOldVal(String oldVal) {
		this.oldVal = oldVal;
	}

	public String getNewVal() {
		return newVal;
	}

	public void setNewVal(String newVal) {
		this.newVal = newVal;
	}

	public ZonedDateTime getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(ZonedDateTime updateDate) {
		this.updateDate = updateDate;
	}

	
	
	

}

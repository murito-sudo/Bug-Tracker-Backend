package com.example.Bug.Tracker.Backend.Tickets;

import org.springframework.stereotype.Service;

@Service
public class Bug {
	
	String type;

	
	
	public Bug() {
		
	}
	
	public Bug(String type) {
		super();
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}

package com.example.Bug.Tracker.Backend.Tickets;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor // Default no-args constructor for JPA
@AllArgsConstructor // All-args constructor for convenience
public class TicketUpdateRequest {
	
	@NotNull
	private int developerId;
	
	@Size(min = 1)
	@Size(max = 50)
	@NotNull
    private String title;
    
	@Size(min = 5)
    @Size(max = 200)
	@NotNull
    private String desc;
    
    @Min(0)
    @Max(5)
    @NotNull
    private Integer priority;
    
    @Min(-1)
    @Max(3)
    @NotNull
    private Integer status;
    
  
    
    @NotNull
    private String bug;
    
   
    
    
    

    
    public int getDeveloperId() {
    	return developerId;
    }
    
    public void setDeveloperId(int developerId) {
    	this.developerId = developerId;
    }

	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	public Integer getPriority() {
		return priority;
	}


	public void setPriority(Integer priority) {
		this.priority = priority;
	}


	public Integer getStatus() {
		return status;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}


	public String getBug() {
		return bug;
	}


	public void setBug(String bug) {
		this.bug = bug;
	}




	
    


}

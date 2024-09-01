package com.example.Bug.Tracker.Backend.Tickets;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "ticket_files")
@NoArgsConstructor // Default no-args constructor for JPA
@AllArgsConstructor // All-args constructor for convenience
public class TicketFile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@NotNull
	private File uploadedFile;
	@NotNull
	private String uploader;
	private String note;
	private ZonedDateTime created;
	
	// Method to get the uploaded file
    public File getUploadedFile() {
        return uploadedFile;
    }

	public String getUploader() {
		return uploader;
	}

	public void setUploader(String uploader) {
		this.uploader = uploader;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public ZonedDateTime getCreated() {
		return created;
	}

	public void setCreated(ZonedDateTime created) {
		this.created = created;
	}


	
	
	// Method to handle MultipartFile upload and save the file
    public File saveUploadedFile(MultipartFile multipartFile, String uploadDir) throws IOException {
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        String fileName = multipartFile.getOriginalFilename();
        uploadedFile = new File(uploadDir + File.separator + fileName);
        multipartFile.transferTo(uploadedFile);
        return uploadedFile;
    }

    // Method to delete the uploaded file
    public void deleteFile() {
        if (uploadedFile != null && uploadedFile.exists()) {
            uploadedFile.delete();
            uploadedFile = null;
        }
    }

    

}

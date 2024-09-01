package com.example.Bug.Tracker.Backend.Projects;


import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectJPARepository extends JpaRepository<Project, Integer> {
	@Query("SELECT p FROM Project p JOIN p.userRole ur WHERE KEY(ur) = :userId")
    List<Project> findByUserRoleKey(@Param("userId") Integer userId);
	
	
}

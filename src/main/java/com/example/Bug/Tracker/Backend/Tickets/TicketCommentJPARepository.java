package com.example.Bug.Tracker.Backend.Tickets;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketCommentJPARepository extends JpaRepository<TicketComment, Integer> {

}

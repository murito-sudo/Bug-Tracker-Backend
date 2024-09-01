package com.example.Bug.Tracker.Backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.Bug.Tracker.Backend.Tickets.Bug;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ServicesTest {
	
	@Mock
	private Bug bug;
	
	@Test
	void getBugType() {
		bug = new Bug("Random");
		
		assertEquals("Random", bug.getType());
		
	}

}

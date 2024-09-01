package com.example.Bug.Tracker.Backend.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import org.springframework.security.core.userdetails.User;
import com.example.Bug. Tracker.Backend.User.UserJPARepository;


@Service
public class UserDetailsLog implements UserDetailsService {

	@Autowired
	private UserJPARepository UJR;
	
	public UserDetailsLog() {
		
	}
	
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.example.Bug.Tracker.Backend.User.User user = UJR.findByusername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
		
		
		System.out.println(user.getPassword());
	    return new User(user.getUsername(), user.getPassword(),  AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_" + user.getRoles()));
	}
}


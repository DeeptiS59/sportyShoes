package com.shoewear.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shoewear.model.MyUserDetails;
import com.shoewear.model.User;
import com.shoewear.repository.UserRepository;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUserName(userName);
        if(user.isEmpty()) {
        	if(userName.equals("admin")) {
        		User auser= new User();
        		auser.setUserName("admin");
        		auser.setPassword("123");
        		auser.setRoles("ROLE_ADMIN");
        		auser.setActive(true);
        		return new MyUserDetails(auser);
        	}
        	else {
        		throw new UsernameNotFoundException("Not found: " + userName);
        	}
        }


        return new MyUserDetails(user.get());
    }
}
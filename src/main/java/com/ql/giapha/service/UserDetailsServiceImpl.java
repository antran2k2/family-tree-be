package com.ql.giapha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ql.giapha.model.AppUser;
import com.ql.giapha.repository.UserRepo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired(required = true)
    private UserRepo userRepo;

    // public UserDetailsServiceImpl(UserRepo userRepository) {
    // this.userRepo = userRepository;
    // }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UnsupportedOperationException("Unimplemented method 'loadUserByUsername'");
        } else
            return UserDetailsImpl.build(user);
    }
}
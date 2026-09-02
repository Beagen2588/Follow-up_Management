package com.AMiller.Follow_up_Management.Service;

import com.AMiller.Follow_up_Management.Repository.UsersRepository;
import com.AMiller.Follow_up_Management.entities.Users;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserLogInService implements UserDetailsService{

    private final UsersRepository usersRepository;

    public UserLogInService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException{
            Users user = usersRepository.findByUserName(username)
            .orElseThrow(() ->
                new UsernameNotFoundException("User not found"));

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUserName())
                    .password(user.getPasswordHash())
                    .roles(user.getRole())
                    .disabled(!user.isActive())
                    .build();
        }
    }


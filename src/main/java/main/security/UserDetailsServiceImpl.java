package main.security;

import lombok.extern.slf4j.Slf4j;
import main.model.User;
import main.services.userService.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {


    private final UserServiceImpl userService;

    @Autowired
    public UserDetailsServiceImpl(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByUserEmail(email);
        if(user == null){
            throw new UsernameNotFoundException("User not found with email " + email);
        }
        UserDetailsImpl userDetails = UserDetailsFactory.create(user);
        return userDetails;
    }
}

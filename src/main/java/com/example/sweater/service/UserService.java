package com.example.sweater.service;

import com.example.sweater.Entity.Role;
import com.example.sweater.Entity.User;
import com.example.sweater.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
//    @Autowired в конструкторе
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    private MailSenderService mailSenderService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }

    public boolean addUser(User user) {
        User userFromDb = userRepo.findByUsername(user.getUsername());
        if (userFromDb != null) {
            return false;
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        userRepo.save(user);

        if(!ObjectUtils.isEmpty(user.getEmail())){
           String message = String.format(
                   "Hello, %s! \n" +
                           "Welcome to sweater, please follow the link (http://localhost:8080/activate/%s) to activate your account",
                   user.getUsername(), user.getActivationCode()
           );
            mailSenderService.send(user.getEmail(),"Activation code", message);

        }
        return true;
    }


    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);

        if (user == null) return false;
        user.setActivationCode(null);
        userRepo.save(user);
        return true;
    }
}

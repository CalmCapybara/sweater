package com.example.sweater.service;

import com.example.sweater.Entity.Role;
import com.example.sweater.Entity.User;
import com.example.sweater.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
//    @Autowired в конструкторе
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    public boolean addUser(User user) {
        User userFromDb = userRepo.findByUsername(user.getUsername());
        if (userFromDb != null) {
            return false;
        }
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        sendActivasionCode(user);
        return true;
    }

    private void sendActivasionCode(User user) {
        if(!ObjectUtils.isEmpty(user.getEmail())){
           String message = String.format(
                   "Hello, %s! \n" +
                           "Welcome to sweater, please follow the link (http://localhost:8080/activate/%s) to activate your account",
                   user.getUsername(), user.getActivationCode()
           );
            mailSenderService.send(user.getEmail(),"Activation code", message);

        }
    }


    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);
        if (user == null) return false;
        user.setActive(true);
        user.setActivationCode(null);
        userRepo.save(user);
        return true;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public void saveUser(User user, String username, Map<String, String> form) {

        user.setUsername(username);

        Set<String> allRoles = Arrays.stream(Role.values()).map(role -> role.name()).collect(Collectors.toSet());

        Set<Role> userRoles = form.keySet().stream()
                .filter(role -> allRoles.contains(role))
                .map(role -> Role.valueOf(role))
                .collect(Collectors.toSet());

        user.setRoles(userRoles);

        userRepo.save(user);
    }

    public void updateProfile(User user, String password, String email) {
        String userEmail = user.getEmail();
        boolean isEmailChanged = (email != null && !email.equals(userEmail)) || (userEmail != null && !userEmail.equals(email));
        if(isEmailChanged) {
            user.setEmail(email);
            if(!ObjectUtils.isEmpty(email)) {
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }

        if(!ObjectUtils.isEmpty(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepo.save(user);

        if(isEmailChanged) sendActivasionCode(user);
    }
}

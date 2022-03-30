package com.example.sweater.controller;

import com.example.sweater.Entity.Role;
import com.example.sweater.Entity.User;
import com.example.sweater.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    @Autowired
    private UserRepo userRepo;

    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userRepo.findAll());
        return "userList";
    }

    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "userEdit";

    }

    @PostMapping
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user)
    {
        user.setUsername(username);

        Set<String> allRoles = Arrays.stream(Role.values()).map(role -> role.name()).collect(Collectors.toSet());

        Set<Role> userRoles = form.keySet().stream()
                .filter(role -> allRoles.contains(role))
                .map(role -> Role.valueOf(role))
                .collect(Collectors.toSet());

        user.setRoles(userRoles);

        userRepo.save(user);

        return "redirect:/user";
    }
}

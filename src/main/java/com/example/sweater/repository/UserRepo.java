package com.example.sweater.repository;

import com.example.sweater.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}

package com.example.sweater.Entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER;

    @Override
    public String getAuthority() {
        return name(); //стандартное строковое представление User
    }
}

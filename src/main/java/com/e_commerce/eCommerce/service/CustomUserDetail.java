package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.entity.Roles;
import com.e_commerce.eCommerce.entity.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetail implements UserDetails {


    private User user;
    private Long id;
    private String email;
    private Roles role;


    public CustomUserDetail(User user) {
        this.user = user;
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
//jwt
    public CustomUserDetail(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = Roles.valueOf(role);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }


    @Override
    public String getPassword() {
        return user != null ? user.getPassword() : null;
    }


    @Override
    public String getUsername() {
        return email;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.getAccountLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getActive();
    }


    public Long getId() {
        return id;
    }

    public Roles getRole() {
        return role;
    }

    public User getUser() {
        return this.user;
    }
}
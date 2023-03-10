package com.example.weblab4.jwt;

import com.example.weblab4.model.Status;
import com.example.weblab4.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class JwtUserFactory {

    public JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                new ArrayList<GrantedAuthority>(){{
                    add(new SimpleGrantedAuthority(user.getRole().name()));
                }},
                user.getStatus().equals(Status.ACTIVE),
                user.getUpdated()
        );
    }
}
package com.example.danguen.config.oauth;

import com.example.danguen.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PrincipalUserDetails implements OAuth2User {

    private final User user;

    public PrincipalUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of((GrantedAuthority) () -> user.getRole().toString());
    }

    public Long getUserId() {
        return user.getId();
    }

    @Override
    public String getName() {
        return user.getName();
    }

    public User getUser() {
        return user;
    }
}

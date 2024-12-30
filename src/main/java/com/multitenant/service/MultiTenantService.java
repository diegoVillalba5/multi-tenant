package com.multitenant.service;

import com.multitenant.config.TenantContext;
import com.multitenant.config.auth.JwtTokenUtils;
import com.multitenant.dto.user.User;
import com.multitenant.dto.user.UserCredentials;
import com.multitenant.dto.user.UserResponse;
import com.multitenant.repository.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MultiTenantService {
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;

    public UserResponse authenticate(UserCredentials userCredentials) {
        authenticateUser(userCredentials.getUsername(), userCredentials.getPassword());

        User user = userMapper.getUserByUsername(userCredentials.getUsername());

        return UserResponse.builder()
                .username(user.getUsername())
                .token(jwtTokenUtils.buildToken(user, TenantContext.getCurrentTenant()))
                .build();
    }

    public List<User> getUsers() {
        return userMapper.getUsers();
    }

    private void authenticateUser(String user, String password) throws AuthenticationException {
        Authentication authentication;

        UsernamePasswordAuthenticationToken userToAuthenticate = new UsernamePasswordAuthenticationToken(user, password);
        authentication = authenticationManager.authenticate(userToAuthenticate);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

package com.multitenant.config.auth;

import com.multitenant.config.TenantContext;
import com.multitenant.dto.user.UserCredentials;
import com.multitenant.repository.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;
import static org.springframework.util.ObjectUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class CAuthenticationProvider implements AuthenticationProvider, AuthenticationManager {
    private final UserMapper userMapper;

    @Value("#{'${tenant.names}'.split(',')}")
    private List<String> tenantsList;

    private static final Logger logger = LoggerFactory.getLogger(CAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (isEmpty(authentication.getPrincipal())) {
            return null;
        }
        String username = authentication.getPrincipal().toString();

        String tenantId = getTenantId(username);
        if (!tenantsList.contains(tenantId))
            throw new BadCredentialsException("Tenant Id is not valid");

        TenantContext.setCurrentTenant(tenantId);

        UserCredentials user = userMapper.getUserCredentialsByUsername(username);
        validateUser(user);

        logger.info("Authenticating user {}", username);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = authentication.getCredentials().toString();

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            logger.info("Clave invalida para el usuario: {}", username);
            throw new BadCredentialsException("Invalid credentials");
        }

        return new UsernamePasswordAuthenticationToken(username, "");
    }

    public void validateUser(UserCredentials user) {
        if (isNull(user))
            throw new BadCredentialsException("User does not exists");

        if (!user.getActive())
            throw new DisabledException("User is not active");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    private String getTenantId(String username) {
        if (username == null || username.isEmpty())
            throw new BadCredentialsException("INVALID_CREDENTIALS");

        return username.split("@")[1];
    }
}

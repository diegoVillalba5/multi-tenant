package com.multitenant.config.auth;

import com.multitenant.config.TenantContext;
import com.multitenant.repository.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
@RequiredArgsConstructor
public class CAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserMapper userMapper;

    private static final Logger logger = LoggerFactory.getLogger(CAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws IOException {
        try {
            if (jwtTokenUtils.checkToken(request)) {
                Claims claims = jwtTokenUtils.validateRequest(request);
                setUpSpringAuthentication(claims);

                String username = (String) claims.get(JwtTokenUtils.USER_USERNAME_KEY);
                if (!userMapper.isUserActive(username)) {
                    SecurityContextHolder.clearContext();
                }
            }
            chain.doFilter(request, response);
        } catch (ServletException | ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            logger.error("Token expired or with errores");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    private void setUpSpringAuthentication(Claims claims) {
        // Setting authorities in null so the constructor sets authenticated = true
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        String currentTenant = (String) claims.get(JwtTokenUtils.TENANT_ID_KEY);
        TenantContext.setCurrentTenant(currentTenant);
    }

}
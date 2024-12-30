package com.multitenant.config.auth;

import com.multitenant.dto.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.*;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String TENANT_ID_KEY = "tenant_id";
    public static final String USER_ID_KEY = "user_username";
    public static final String USER_USERNAME_KEY = "user_username";
    public static final String USER_NAME_KEY = "user_name";
    public static final String USER_EMAIL_KEY = "user_email";

    @Value("${jwt.secret}")
    private String secret;

    public boolean checkToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(JwtTokenUtils.HEADER_STRING);
        return (authenticationHeader != null && authenticationHeader.startsWith(JwtTokenUtils.TOKEN_PREFIX));
    }

    public Claims validateRequest(HttpServletRequest request) {
        String jwtToken = request.getHeader(JwtTokenUtils.HEADER_STRING).replace(JwtTokenUtils.TOKEN_PREFIX, "");
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(jwtToken).getPayload();
    }

    public String buildToken(User user, String tenantId) {
        return Jwts.builder()
                .claim(TENANT_ID_KEY, tenantId)
                .claim(USER_ID_KEY, user.getId())
                .claim(USER_USERNAME_KEY, user.getUsername())
                .claim(USER_NAME_KEY, user.getName())
                .claim(USER_EMAIL_KEY, user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(getEndOfTheDay())
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = this.secret.getBytes(StandardCharsets.UTF_16);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Date getEndOfTheDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(HOUR_OF_DAY, 23);
        cal.set(MINUTE, 59);
        cal.set(SECOND, 59);

        return new Date(cal.getTimeInMillis());
    }

}

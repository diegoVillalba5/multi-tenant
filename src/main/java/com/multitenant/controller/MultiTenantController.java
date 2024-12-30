package com.multitenant.controller;

import com.multitenant.dto.user.User;
import com.multitenant.dto.user.UserCredentials;
import com.multitenant.dto.user.UserResponse;
import com.multitenant.service.MultiTenantService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@CrossOrigin
@AllArgsConstructor
public class MultiTenantController {
    private final MultiTenantService svc;

    private static final Logger logger = LoggerFactory.getLogger(MultiTenantController.class);

    @PostMapping("auth")
    public ResponseEntity<UserResponse> authenticate(
            @RequestBody UserCredentials userCredentials) {
        try {
            return ResponseEntity.ok(svc.authenticate(userCredentials));
        } catch (BadCredentialsException | DisabledException e) {
            logger.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("user")
    public ResponseEntity<List<User>> getUsers() {
        try {
            return ResponseEntity.ok(svc.getUsers());
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

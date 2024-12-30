package com.multitenant.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private int id;
    private String username;
    private String name;
    private String email;
}

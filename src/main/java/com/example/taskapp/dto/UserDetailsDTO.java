package com.example.taskapp.dto;

import lombok.Data;

@Data
public class UserDetailsDTO {
    private Long id;
    private String email;
    private String username;
    private String role;
}

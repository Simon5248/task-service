package com.example.taskapp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.taskapp.config.UserClientConfig;
import com.example.taskapp.dto.UserDetailsDTO;

@FeignClient(
    name = "user-service",
    fallback = UserClientFallback.class,
    configuration = UserClientConfig.class
)
public interface UserClient {
    
    @GetMapping("/api/users/details/{email}")
    UserDetailsDTO getUserDetailsByEmail(@PathVariable("email") String email);
    
    @GetMapping("/api/users/validate-token")
    boolean validateToken(@RequestHeader("Authorization") String token);
    
    @GetMapping("/api/users/token/extract-email")
    ResponseEntity<String> extractUsername(@RequestHeader("Authorization") String token);
    
    @GetMapping("/api/users/token/extract-user-id")
    ResponseEntity<Long> extractUserId(@RequestHeader("Authorization") String token);
}

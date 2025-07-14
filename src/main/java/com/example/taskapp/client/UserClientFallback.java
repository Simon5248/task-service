package com.example.taskapp.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.example.taskapp.dto.UserDetailsDTO;

@Component
public class UserClientFallback implements UserClient {
    
    private static final Logger logger = LoggerFactory.getLogger(UserClientFallback.class);
    @Override
    public UserDetailsDTO getUserDetailsByEmail(String email) {
        // 返回空對象或者拋出異常，取決於你的錯誤處理策略
        logger.error("Failed to get user details for email: {}", email);
        throw new RuntimeException("User service is not available");
    }
    
    @Override
    public boolean validateToken(String token) {
        // 如果無法訪問 user-service，預設返回 false
        return false;
    }
    
    @Override
    public ResponseEntity<String> extractUsername(String token) {
        logger.error("Failed to extract username from token");
        return ResponseEntity.badRequest().body(null);
    }

    @Override
    public ResponseEntity<Long> extractUserId(String token) {
        logger.error("Failed to extract user ID from token");
        return ResponseEntity.badRequest().body(null);
    }
}

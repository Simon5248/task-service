package com.example.taskapp.interceptor;

import com.example.taskapp.client.UserClient;
import com.example.taskapp.dto.UserDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserContextInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(UserContextInterceptor.class);
    
    private final ApplicationContext applicationContext;
    private UserClient userClient;

    public UserContextInterceptor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private UserClient getUserClient() {
        if (userClient == null) {
            userClient = applicationContext.getBean(UserClient.class);
        }
        return userClient;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String bearerToken = request.getHeader("Authorization");
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            try {
                ResponseEntity<String> emailResponse = getUserClient().extractUsername(bearerToken);
                
                if (emailResponse != null && emailResponse.getBody() != null) {
                    String email = emailResponse.getBody();
                    UserDetailsDTO userDetails = getUserClient().getUserDetailsByEmail(email);
                    
                    if (userDetails != null) {
                        // 將用戶信息存儲在請求屬性中
                        request.setAttribute("userDetails", userDetails);
                        request.setAttribute("userEmail", email);
                        log.debug("User context set for email: {}", email);
                    }
                }
            } catch (Exception e) {
                log.error("Error processing authentication token", e);
            }
        }
        
        return true;
    }
}

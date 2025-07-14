package com.example.taskapp.util;

import com.example.taskapp.dto.UserDetailsDTO;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class UserContextHolder {
    
    public static UserDetailsDTO getCurrentUser() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return (UserDetailsDTO) request.getAttribute("userDetails");
        }
        return null;
    }

    public static String getCurrentUserEmail() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return (String) request.getAttribute("userEmail");
        }
        return null;
    }
}

// ===================================================================================
// FILE: src/main/java/com/example/taskapp/controller/TaskController.java
// ===================================================================================
package com.example.taskapp.controller;

import com.example.taskapp.entity.Task;
import com.example.taskapp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskRepository taskRepository;

    private Long getUserIdFromRequest(HttpServletRequest request) {
        // Gateway 已經驗證了 token，並將用戶 ID 放在 header 中
        String userIdStr = request.getHeader("X-User-ID");
        return userIdStr != null ? Long.parseLong(userIdStr) : null;
    }

    @GetMapping
    public List<Task> getTasks(HttpServletRequest request) {
        logger.debug("收到請求 headers: {}", Collections.list(request.getHeaderNames())
            .stream()
            .collect(Collectors.toMap(h -> h, request::getHeader)));
        System.out.println("Received request headers: " + Collections.list(request.getHeaderNames())
            .stream()
            .collect(Collectors.toMap(h -> h, request::getHeader)));
        Long userId = getUserIdFromRequest(request);
        logger.debug("解析到的 userId: {}", userId);
        
        if (userId == null) {
            System.out.println("未能從請求中獲取 userId");
            logger.error("未能從請求中獲取 userId");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未授權的訪問");
        }
        
        return taskRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        task.setUserId(userId);
        Task savedTask = taskRepository.save(task);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return taskRepository.findById(id)
                .map(task -> {
                    if (!task.getUserId().equals(userId)) {
                        throw new RuntimeException("無權限修改此任務");
                    }
                    task.setTitle(taskDetails.getTitle());
                    task.setDescription(taskDetails.getDescription());
                    task.setStatus(taskDetails.getStatus());
                    task.setPriority(taskDetails.getPriority());
                    task.setDueDate(taskDetails.getDueDate());
                    return new ResponseEntity<>(taskRepository.save(task), HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @RequestBody Task taskDetails, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return taskRepository.findById(id)
            .map(task -> {
                if (!task.getUserId().equals(userId)) {
                    return new ResponseEntity<Task>(HttpStatus.FORBIDDEN);
                }
                task.setStatus(taskDetails.getStatus()); // 只更新 status
                Task updatedTask = taskRepository.save(task);
                return ResponseEntity.ok(updatedTask);
            })
            .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return taskRepository.findById(id)
                .map(task -> {
                    if (!task.getUserId().equals(userId)) {
                        throw new RuntimeException("無權限刪除此任務");
                    }
                    taskRepository.delete(task);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
// ===================================================================================
// FILE: src/main/java/com/example/taskapp/TaskApplication.java
// ===================================================================================
package com.example.taskapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class TaskApplication {
    private static final Logger log = LoggerFactory.getLogger(TaskApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TaskApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application is ready, starting Eureka registration process...");
    }

    @EventListener(InstanceRegisteredEvent.class)
    public void onInstanceRegistered(InstanceRegisteredEvent<?> event) {
        log.info("Instance successfully registered with Eureka!");
    }
}
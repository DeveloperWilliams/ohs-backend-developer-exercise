package com.countyhospital.healthapi.home;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Root response delivered successfully")
    })
    public ResponseEntity<Map<String, Object>> root() {

        logger.debug("Root endpoint accessed");

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "OK");
 
        return ResponseEntity.ok(payload);
    }
}

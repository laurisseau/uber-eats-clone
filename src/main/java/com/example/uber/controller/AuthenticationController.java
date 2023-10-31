package com.example.uber.controller;

import com.example.uber.model.Users;
import com.example.uber.service.AuthenticationService;
import com.example.uber.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;


    @PostMapping("/login")
    ResponseEntity<Map<String, Object>> login(@RequestBody Users user){
        try {
            UserDetails userDetails = authenticationService.loadUserByUsername(user.getUsername());

            Map<String, Object> userInfo = authenticationService.userInfo(user.getUsername(), jwtUtils.generateToken(userDetails));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            return ResponseEntity.ok(userInfo);

        }catch(Exception e){
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("Login failed", e.getMessage());
            return ResponseEntity.badRequest().body(userInfo);
        }
    }

    @PostMapping("/signup")
    ResponseEntity<String> Signup(@RequestBody Users user){
        try{

            authenticationService.signUp(user);

            return ResponseEntity.ok("User created");

        }catch(Exception e){
            return ResponseEntity.badRequest().body("Signup failed: " + e.getMessage());
        }
    }




}

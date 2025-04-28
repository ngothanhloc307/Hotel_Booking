package com.example.backend.controllers;

import com.example.backend.dtos.LoginRequest;
import com.example.backend.dtos.RegistrationRequest;
import com.example.backend.dtos.Response;
import com.example.backend.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    @PostMapping ("/register")
        public ResponseEntity<Response> registerUser(@RequestBody @Valid RegistrationRequest request) {
            return ResponseEntity.ok(userService.registerUser(request));
        }


    @PostMapping ("/login")
    public ResponseEntity<Response> registerUser(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }
}

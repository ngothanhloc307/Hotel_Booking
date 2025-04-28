package com.example.backend.controllers;


import com.example.backend.dtos.Response;
import com.example.backend.dtos.UserDTO;
import com.example.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Response> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/update")
    public ResponseEntity<Response> updateOwnAccount(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateOwnAccount(userDTO));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response> deleteOwnAccount() {
        return ResponseEntity.ok(userService.deleteOwnAccount());
    }

    @GetMapping("/account")
    public ResponseEntity<Response> getOwnAccountDetails() {
        return ResponseEntity.ok(userService.getOwnAccountDetails());
    }

    @GetMapping("/bookings")
    public ResponseEntity<Response> getMyBookingsHistory() {
        return ResponseEntity.ok(userService.getMyBookingHistory());
    }


}

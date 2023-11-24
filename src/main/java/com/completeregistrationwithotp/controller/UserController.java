package com.completeregistrationwithotp.controller;

import com.completeregistrationwithotp.dto.LoginDto;
import com.completeregistrationwithotp.dto.RegisterDto;
import com.completeregistrationwithotp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        return new ResponseEntity<>(userService.register(registerDto), HttpStatus.OK);
    }

    @PutMapping("/verify-account")
    public ResponseEntity<String> verifyAccount(@RequestParam String email, @RequestParam String otp){
        return new ResponseEntity<>(userService.verifyAccount(email, otp),HttpStatus.OK);
    }

    @PutMapping("/regenrate-otp")
    public ResponseEntity<String> regenrateOtp(@RequestParam String email){
        return new ResponseEntity<>(userService.regenrateOtp(email),HttpStatus.OK);
    }

    @PutMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto){
        return new ResponseEntity<>(userService.login(loginDto),HttpStatus.OK);
    }
}

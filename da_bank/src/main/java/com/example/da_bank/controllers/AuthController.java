package com.example.da_bank.controllers;

import com.example.da_bank.auth.AuthRequest;
import com.example.da_bank.auth.AuthResponse;
import com.example.da_bank.auth.JwtTokenUtil;
import com.example.da_bank.models.AccountUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );

            AccountUser accountUser = (AccountUser) authentication.getPrincipal();
            String accessToken = jwtTokenUtil.generateAccessToken(accountUser);
            AuthResponse response = new AuthResponse(accountUser.getUsername(), accessToken);
            return ResponseEntity.ok().body(response);

        } catch (BadCredentialsException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

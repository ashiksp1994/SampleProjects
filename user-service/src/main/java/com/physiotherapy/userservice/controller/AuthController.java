package com.physiotherapy.userservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import com.physiotherapy.userservice.model.UserModel;
import com.physiotherapy.userservice.security.JwtUtils;
import com.physiotherapy.userservice.service.UserService;


@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "https://frontend-371541271718.europe-central2.run.app")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public UserModel register(@RequestBody UserModel user) {
        return userService.saveUser(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody UserModel user) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Invalid username or password");
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        return jwtUtils.generateToken(userDetails);
    }
}

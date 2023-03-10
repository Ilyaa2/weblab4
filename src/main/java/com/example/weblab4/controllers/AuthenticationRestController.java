package com.example.weblab4.controllers;

import com.example.weblab4.dto.AuthenticationRequestDto;
import com.example.weblab4.jwt.JwtTokenProvider;
import com.example.weblab4.jwt.UsernameAlreadyExistsException;
import com.example.weblab4.model.User;
import com.example.weblab4.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/auth/")
public class AuthenticationRestController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Autowired
    public AuthenticationRestController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity login(@RequestBody AuthenticationRequestDto requestDto) {
        try {
            String username = requestDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            User user = userService.findByUsername(username);

            if (user == null) {
                throw new UsernameNotFoundException("User with username: " + username + " not found");
            }

            String token = jwtTokenProvider.createToken(username, user.getRole());

            Map<Object, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping("register")
    public ResponseEntity<Map<Object,Object>> register(@RequestBody AuthenticationRequestDto requestDto){
        try{
            String username = requestDto.getUsername();
            User user = userService.findByUsername(username);

            if (user != null){
                throw new UsernameAlreadyExistsException("A user with the same name already exists");
            }

            //регистрация и создаение токена
            User createdUser = userService.register(new User(username, requestDto.getPassword()));
            String token = jwtTokenProvider.createToken(createdUser.getUsername(), createdUser.getRole());

            Map<Object, Object> responce = new HashMap<>();
            responce.put("username", username);
            responce.put("token", token);
            //return ResponseEntity<>().
            return new ResponseEntity<>(responce, HttpStatus.CREATED);

        } catch (UsernameAlreadyExistsException e){
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }
}

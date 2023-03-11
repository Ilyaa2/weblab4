package com.example.weblab4.controllers;

import com.example.weblab4.dto.AuthenticationRequestDto;
import com.example.weblab4.jwt.JwtTokenProvider;
import com.example.weblab4.jwt.UsernameAlreadyExistsException;
import com.example.weblab4.model.User;
import com.example.weblab4.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

    @CrossOrigin(origins = "http://localhost:5173")
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

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "register")
    public ResponseEntity<Map<Object,Object>> register(@RequestBody AuthenticationRequestDto requestDto){
        Map<Object, Object> responce = new HashMap<>();
        try{
            String username = requestDto.getUsername();
            //todo во-первых ручная проверка на то, что пользователь с таким именем уже регистрирован - большой риск
            // приложение многопоточное и сразу после того, как проверка показала, что все хорошо, сразу же после этого,
            // клиент с таким же именем уже может регистрироваться.
            // в таком случае, лучше полагаться на бд, указав там unique напротив name и ждать пока она вернет exception
            // внизу я как раз его обрабатываю, так что нужно эту проверку ручную убрать. БД специально делают таким образом,
            // что они отказоустойчивые в плане многопоточки.
            User user = userService.findByUsername(username);

            //todo во-вторых выбрасывать ексепшн и тут же его обрабатывать - моветон
            if (user != null){
                throw new UsernameAlreadyExistsException("A user with the same name already exists");
            }

            User createdUser = userService.register(new User(username, requestDto.getPassword()));
            String token = jwtTokenProvider.createToken(createdUser.getUsername(), createdUser.getRole());


            responce.put("username", username);
            responce.put("token", token);
            return new ResponseEntity<>(responce, HttpStatus.CREATED);
        } catch (UsernameAlreadyExistsException e){
            responce.put("error", "Conflict");
            responce.put("message", "The user with this name already exists");
            responce.put("path", "/auth/register");
            responce.put("status", 409);
            responce.put("timestamp", new Date());
            return new ResponseEntity<>(responce, HttpStatus.CONFLICT);
        }
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @RequestMapping(method = RequestMethod.OPTIONS, headers = "Accept=application/json", value = "/register")
    public ResponseEntity<?> optionsRegister(){
        return getResponseEntityForOptions();
    }

    @CrossOrigin(origins = "http://localhost:5173") // - ЭТО САМОЕ ВАЖНОЕ, БЕЗ НЕГО НИЧЕГО РАБОТАТЬ НЕ БУДЕТ
    @RequestMapping(method = RequestMethod.OPTIONS, headers = "Accept=application/json", value = "login")
    public ResponseEntity<?> optionsLogin(){
        return getResponseEntityForOptions();
    }

    private ResponseEntity<?> getResponseEntityForOptions(){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Methods","POST, OPTIONS");
        responseHeaders.set("Access-Control-Allow-Headers","Content-Type");
        responseHeaders.set("Access-Control-Max-Age","3600");
        responseHeaders.set("Access-Control-Allow-Origin", "http://localhost:5173");
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.OK);
    }
}

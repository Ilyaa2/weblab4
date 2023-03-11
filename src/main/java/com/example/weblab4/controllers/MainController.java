package com.example.weblab4.controllers;

import com.example.weblab4.util.CalcVerdict;
import com.example.weblab4.jwt.JwtUser;
import com.example.weblab4.model.User;
import com.example.weblab4.repository.DotRepo;
import com.example.weblab4.model.Dot;
import com.example.weblab4.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


import java.util.List;

@RestController
@RequestMapping("/")
public class MainController {

    private DotRepo dotRepo;
    private UserRepo userRepo;
    private User user;

    @Autowired
    public MainController(DotRepo dotRepo, UserRepo userRepo){
        this.dotRepo = dotRepo;
        this.userRepo = userRepo;
    }


    private void setUser(){
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        this.user = userRepo.findById(((JwtUser) user.getPrincipal()).getId()).get();
    }


    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "dot",consumes="application/json")
    @ResponseBody
    public ResponseEntity<Dot> saveDot(@RequestBody Dot dot){
        setUser();

        dot.setUser(user);
        dot.setVerdict(CalcVerdict.calculate(dot));
        try {
            return new ResponseEntity<>(dotRepo.save(dot), HttpStatus.CREATED);
        } catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "dot/{rad}",produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<Dot>> getDotsById(@PathVariable("rad") double r){
        setUser();
        try {
            return new ResponseEntity<>(dotRepo.findByUserAndR(user, r), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "dot",produces = "application/json")
    @ResponseBody
    public List<Dot> getAllDots(){
        setUser();
        return dotRepo.findByUser(user);
    }


    @CrossOrigin(origins = "http://localhost:5173")
    @RequestMapping(method = RequestMethod.OPTIONS, headers = "Accept=application/json", value = "/dot")
    public ResponseEntity<?> options(){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Methods","POST, GET, OPTIONS");
        responseHeaders.set("Access-Control-Allow-Headers","Content-Type, Authorization");
        responseHeaders.set("Access-Control-Max-Age","3600");
        responseHeaders.set("Access-Control-Allow-Origin", "http://localhost:5173");
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.OK);
    }

}

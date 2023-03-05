package com.example.weblab4.controllers;

import com.example.weblab4.CalcVerdict;
import com.example.weblab4.repository.DotRepo;
import com.example.weblab4.domain.Dot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


import java.util.List;

@RestController
@RequestMapping("/")
public class MainController {

    private DotRepo dotRepo;

    @Autowired
    public MainController(DotRepo dotRepo){
        this.dotRepo = dotRepo;
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("dot")
    @ResponseBody
    public Dot saveDot(@RequestBody Dot dot){
        dot.setVerdict(CalcVerdict.calculate(dot));
        //System.out.println(dot);
        return dotRepo.save(dot);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("dot/{rad}")
    @ResponseBody
    public List<Dot> getDotsById(@PathVariable("rad") double r){
        return dotRepo.findAllByREquals(r);
    }

    /*
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("dot")
    @ResponseBody
    public List<Dot> getAllDots(){
        return dotRepo.findAll();
    }

     */

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("dot")
    @ResponseBody
    public List<Dot> getAllDots(){
        /*
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Methods","POST, GET, OPTIONS, DELETE");
        responseHeaders.set("Access-Control-Allow-Headers","Content-Type, Authorization");
        responseHeaders.set("Access-Control-Max-Age","3600");
        responseHeaders.set("Access-Control-Allow-Origin", "http://localhost:5173");

        ResponseEntity<List<Dot>> a = new ResponseEntity<>(dotRepo.findAll(),responseHeaders,HttpStatus.OK);

         */

        return dotRepo.findAll();
    }


    @CrossOrigin(origins = "http://localhost:5173")
    @RequestMapping(method = RequestMethod.OPTIONS, headers = "Accept=application/json", value = "/dot")
    public ResponseEntity<?> options(){

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Methods","POST, GET, OPTIONS, DELETE");
        responseHeaders.set("Access-Control-Allow-Headers","Content-Type, Authorization");
        responseHeaders.set("Access-Control-Max-Age","3600");
        responseHeaders.set("Access-Control-Allow-Origin", "http://localhost:5173");

        return new ResponseEntity<>(null, responseHeaders, HttpStatus.OK);
    }
}

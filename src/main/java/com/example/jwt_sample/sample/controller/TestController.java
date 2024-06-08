package com.example.jwt_sample.sample.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
  
  @GetMapping("hello")
  public String getHello(){
    return "Hello World";
  }

  @GetMapping("test")
  public ResponseEntity<String> test(){
    return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication().getName());
  }

}

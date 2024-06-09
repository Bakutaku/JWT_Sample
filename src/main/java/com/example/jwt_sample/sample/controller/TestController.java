package com.example.jwt_sample.sample.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwt_sample.auth.model.User;

/**
 * 実験用のコントローラー
 */
@RestController
public class TestController {

  @GetMapping("hello")
  public String getHello() {
    return "Hello World";
  }

  @GetMapping("test")
  public ResponseEntity<String> test() {
    return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication().getName());
  }

  @GetMapping("user")
  public ResponseEntity<String> userName() {
    Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!(user instanceof User)) {
      System.out.println(user.toString());
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok(((User) user).getName());

  }

}

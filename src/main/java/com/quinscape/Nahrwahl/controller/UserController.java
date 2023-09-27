package com.quinscape.Nahrwahl.controller;

import com.quinscape.Nahrwahl.model.user.User;
import com.quinscape.Nahrwahl.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<User> register(@RequestBody User user) {
    User savedUser = userService.saveNewUser(user);
    return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
  }

// ToDo: /profile /update /logout?
}

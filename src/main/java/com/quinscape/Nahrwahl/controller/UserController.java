package com.quinscape.Nahrwahl.controller;

import com.quinscape.Nahrwahl.model.user.User;
import com.quinscape.Nahrwahl.service.UserService;
import java.security.Principal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{username}")
  public ResponseEntity<User> getProfile(@PathVariable String username) {
    Optional<User> optionalUser = userService.getUserProfile(username);
    return optionalUser
        .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/profile")
  public ResponseEntity<User> getOwnProfile(@AuthenticationPrincipal UserDetails userDetails) {
    Optional<User> optionalUser = userService.getUserProfile(userDetails.getUsername());
    return optionalUser
        .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/update")
  public ResponseEntity<User> updateUser(@RequestBody User userToUpdate, Principal principal) {
    User updated = userService.updateUserProfile(principal.getName(), userToUpdate);

    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

// ToDo: /update /logout?
}

package com.quinscape.Nahrwahl.service;

import com.quinscape.Nahrwahl.model.user.User;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {

  private final UserService userService;
  private final Environment env;


  private User createAdmin() {
    log.info("Creating admin user!");
    User admin = new User();
    admin.setUsername(env.getProperty("admin.username"));
    admin.setPassword(env.getProperty("admin.password"));
    admin.setRoles(List.of("ROLE_ADMIN", "ROLE_USER"));
    admin.setFirstName("Admin");
    admin.setLastName("Admin");
    return admin;
  }

  private User createUser() {
    log.info("Creating dummy user!");
    User user = new User();
    user.setUsername(env.getProperty("user.username"));
    user.setPassword(env.getProperty("user.password"));
    user.setRoles(List.of("ROLE_USER"));
    user.setFirstName("User");
    user.setLastName("User");
    return user;
  }


  @PostConstruct
  public void init() {
    log.info("Creating default admin user if none exists.");
    Optional<User> optionalAdmin = userService.findByUsername("admin");
    if (optionalAdmin.isEmpty()) {
      User admin = createAdmin();
      userService.saveNewUser(admin);
    }

    log.info("Creating default user if none exists.");
    Optional<User> optionalUser = userService.findByUsername("user");
    if (optionalUser.isEmpty()) {
      User user = createUser();
      userService.saveNewUser(user);
    }
  }

}

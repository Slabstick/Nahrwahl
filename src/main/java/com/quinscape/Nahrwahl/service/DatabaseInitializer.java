package com.quinscape.Nahrwahl.service;

import com.quinscape.Nahrwahl.model.user.User;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {

  private final UserService userService;


  @PostConstruct
  public void init() {
    log.info("Initializer: Checking if admin exists.");
    Optional<User> optionalUser = userService.findByUsername("admin");
    if (optionalUser.isEmpty()) {
      log.info("Creating admin user!");
      User admin = new User();
      admin.setUsername("admin");
      admin.setPassword("admin123"); // ToDo: Set username and pw as EnvVar
      admin.setRoles(List.of("ROLE_ADMIN"));
      admin.setFirstName("Admin");
      admin.setLastName("Admin");

      userService.saveNewUser(admin);


    }
  }

}

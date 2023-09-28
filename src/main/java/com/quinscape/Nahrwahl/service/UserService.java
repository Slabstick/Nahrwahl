package com.quinscape.Nahrwahl.service;

import com.quinscape.Nahrwahl.exception.UsernameAlreadyExistsException;
import com.quinscape.Nahrwahl.model.user.User;
import com.quinscape.Nahrwahl.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username).orElseThrow(
        () -> new UsernameNotFoundException("User not found with username: " + username));

    List<GrantedAuthority> authorities = user.getRoles().stream().map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    return org.springframework.security.core.userdetails.User.withUsername(username)
        .password(user.getPassword()).authorities(authorities).build();
  }

  @Transactional
  public User saveNewUser(User user) throws UsernameAlreadyExistsException {

    checkIfUsernameExists(user.getUsername());
    hashUserPassword(user);
    setTimestamps(user);

    log.info("Saving new user");
    return userRepository.save(user);

  }

  private void checkIfUsernameExists(String username) throws UsernameAlreadyExistsException {
    Optional<User> existingUser = userRepository.findByUsername(username);
    if (existingUser.isPresent()) {
      log.info("Username already exists at creation. Throwing exception!");
      throw new UsernameAlreadyExistsException("Username already exists!");
    }
  }

  private void hashUserPassword(User user) {
    String hashedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(hashedPassword);
  }

  private void setTimestamps(User user) {
    Instant today = Instant.now();
    user.setCreatedAt(today);
    user.setUpdatedAt(today);

  }

  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }


}

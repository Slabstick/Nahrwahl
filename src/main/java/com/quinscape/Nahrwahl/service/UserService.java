package com.quinscape.Nahrwahl.service;

import com.quinscape.Nahrwahl.exception.UsernameAlreadyExistsException;
import com.quinscape.Nahrwahl.model.user.User;
import com.quinscape.Nahrwahl.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    User user = userRepository
        .findByUsername(username)
        .orElseThrow(
            () -> new UsernameNotFoundException("User not found with username: " + username));

    List<GrantedAuthority> authorities = user
        .getRoles()
        .stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    return org.springframework.security.core.userdetails.User
        .withUsername(username)
        .password(user.getPassword())
        .authorities(authorities)
        .build();
  }

  @Transactional
  public User saveNewUser(User user) throws UsernameAlreadyExistsException {

    checkIfUsernameExists(user.getUsername());
    hashUserPassword(user);

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


  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public Optional<User> getUserProfile(String username) {
    Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();
    String currentUsername = authentication.getName();
    boolean isAdmin = isAdmin(authentication);
    if (username.equals(currentUsername)) {
      Optional<User> user = findByUsername(username);
      return user.map(u -> isAdmin ? u : sanitizeUserForProfile(u));

    }
    return Optional.empty();
  }

  private User sanitizeUserForProfile(User user) {
    user.setPassword(null);
    user.setLastName(null);
    user.setFirstName(null);

    return user;
  }

  private boolean isAdmin(Authentication authentication) {
    return authentication
        .getAuthorities()
        .stream()
        .anyMatch(grantedAuthority -> "ROLE_ADMIN".equals(grantedAuthority.getAuthority()));
  }

}

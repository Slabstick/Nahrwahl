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

  /**
   * Loads user by the provided username.
   *
   * @param username The username of the user to load.
   * @return A UserDetails object built from the user found in the database.
   * @throws UsernameNotFoundException if the user is not found in the database.
   */
  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    final User user = userRepository
        .findByUsername(username)
        .orElseThrow(() -> {
          log.warn("User not found with username: {}", username);
          return new UsernameNotFoundException("User not found with username: " + username);
        });

    final List<GrantedAuthority> authorities = user
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

  /**
   * Saves a new user to the database.
   *
   * @param user The user to save.
   * @return The saved User object.
   * @throws UsernameAlreadyExistsException if the username is already present in the database.
   */
  @Transactional
  public User saveNewUser(final User user) throws UsernameAlreadyExistsException {

    checkIfUsernameExists(user.getUsername());
    hashUserPassword(user);

    log.info("Saving new user {}", user.getUsername());
    return userRepository.save(user);
  }

  /**
   * Checks if the username already exists in the database.
   *
   * @param username The username to check.
   * @throws UsernameAlreadyExistsException if the username is already present in the database.
   */
  private void checkIfUsernameExists(final String username) throws UsernameAlreadyExistsException {
    final Optional<User> existingUser = userRepository.findByUsername(username);
    if (existingUser.isPresent()) {
      log.error("Username already exists at creation. Throwing exception!");
      throw new UsernameAlreadyExistsException("Username already exists!");
    }
  }


  /**
   * Hashes the password of the given user.
   *
   * @param user The user whose password should be hashed.
   */
  private void hashUserPassword(final User user) {
    final String hashedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(hashedPassword);
  }

  /**
   * Finds a user by their username.
   *
   * @param username The username of the user.
   * @return An Optional containing the User or empty if not found.
   */
  protected Optional<User> findByUsername(final String username) {
    return userRepository.findByUsername(username);
  }

  /**
   * Retrieves a sanitized user profile based on username and role.
   *
   * @param username The username of the user.
   * @return An Optional containing the sanitized User or empty if not found.
   */
  public Optional<User> getUserProfile(final String username) {

    return findByUsername(username).map(this::sanitizeUserPassword);
  }

  /**
   * Sanitizes the user information based on the role.
   *
   * @param user The user to be sanitized.
   * @return The sanitized user.
   */
  private User sanitizeUser(final User user) {
    final Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();
    final boolean isAdmin = isAdmin(authentication);

    return isAdmin ? sanitizeUserPassword(user) : sanitizeUserPasswordAndNames(user);

  }

  /**
   * Sanitizes the user information so password and both name fields are set to null.
   *
   * @param user The user to be sanitized.
   * @return The sanitized user.
   */
  private User sanitizeUserPasswordAndNames(final User user) {
    user.setPassword(null);
    user.setLastName(null);
    user.setFirstName(null);

    return user;
  }

  /**
   * Sanitizes the user information. Only password is set to null.
   *
   * @param user The user to be sanitized.
   * @return The sanitized user.
   */
  private User sanitizeUserPassword(final User user) {
    user.setPassword(null);

    return user;
  }


  /**
   * Checks if the authenticated user has admin privileges.
   *
   * @param authentication The authentication object containing user's authorities.
   * @return True if the user has admin role, false otherwise.
   */
  private boolean isAdmin(final Authentication authentication) {
    return authentication
        .getAuthorities()
        .stream()
        .anyMatch(grantedAuthority -> "ROLE_ADMIN".equals(grantedAuthority.getAuthority()));
  }

  /**
   * Updates the profile of an existing user in the database.
   * <p>
   * This method performs an update on a user profile based on the provided username and
   * userToUpdate object. If the password field of userToUpdate is not null, it will be hashed
   * before updating. The other non-null fields of userToUpdate (email, first name, and last name)
   * will be used to update the existing user profile. The method will sanitize the user object
   * before returning.
   * </p>
   *
   * @param username     the username of the user to be updated
   * @param userToUpdate a User object containing the updated information
   * @return the updated User object
   * @throws UsernameNotFoundException if no user is found with the provided username
   */

  @Transactional
  public User updateUserProfile(String username, User userToUpdate)
      throws UsernameNotFoundException {
    if (userToUpdate.getPassword() != null) {
      hashUserPassword(userToUpdate);
    }
    return userRepository
        .findByUsername(username)
        .map(existingUser -> {
          Optional
              .ofNullable(userToUpdate.getEmail())
              .ifPresent(existingUser::setEmail);
          Optional
              .ofNullable(userToUpdate.getPassword())
              .ifPresent(existingUser::setPassword);
          Optional
              .ofNullable(userToUpdate.getFirstName())
              .ifPresent(existingUser::setFirstName);
          Optional
              .ofNullable(userToUpdate.getLastName())
              .ifPresent(existingUser::setLastName);
          sanitizeUserPassword(existingUser);
          return userRepository.save(existingUser);
        })
        .orElseThrow(() -> {
          log.warn("User not found. Throwing exception!");
          return new UsernameNotFoundException("User not found with username " + username);
        });
  }


}

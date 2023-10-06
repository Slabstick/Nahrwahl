package com.quinscape.Nahrwahl.service;

import com.quinscape.Nahrwahl.exception.UserNotFoundException;
import com.quinscape.Nahrwahl.exception.UsernameAlreadyExistsException;
import com.quinscape.Nahrwahl.model.user.User;
import com.quinscape.Nahrwahl.repository.UserRepository;
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

    return findByUsername(username).map(this::sanitizeUser);
  }

  /**
   * Sanitizes the user information. Only password is set to null.
   *
   * @param user The user to be sanitized.
   * @return The sanitized user.
   */
  private User sanitizeUser(final User user) {
    user.setPassword(null);

    return user;
  }


  /**
   * Updates the profile of an existing user in the database excluding the password.
   * <p>
   * This method performs an update on a user profile based on the provided username and
   * userToUpdate object. The non-null fields of userToUpdate (email, first name, and last name)
   * will be used to update the existing user profile. The password field of userToUpdate is ignored
   * in this method. A separate method should be used to update the password.
   * </p>
   *
   * @param username     the username of the user to be updated
   * @param userToUpdate a User object containing the updated information excluding the password
   * @return the updated User object
   * @throws UsernameNotFoundException if no user is found with the provided username
   */

  @Transactional
  public User updateUserProfile(String username, User userToUpdate)
      throws UsernameNotFoundException {
    return userRepository
        .findByUsername(username)
        .map(existingUser -> updateExistingUser(existingUser, userToUpdate))
        .orElseThrow(() -> {
          log.warn("User not found. Throwing exception!");
          return new UserNotFoundException("User not found with username " + username);
        });
  }

  /**
   * Updates the existing user with the provided updated user information.
   *
   * @param existingUser the existing user to be updated
   * @param userToUpdate the user object containing the updated information
   * @return the updated User object
   */
  private User updateExistingUser(User existingUser, User userToUpdate) {
    updateEmailIfNeeded(existingUser, userToUpdate);
    updateFirstNameIfNeeded(existingUser, userToUpdate);
    updateLastNameIfNeeded(existingUser, userToUpdate);
    sanitizeUser(existingUser);
    return userRepository.save(existingUser);
  }

  /**
   * Updates the email of the existing user if the updated user's email is not null.
   *
   * @param existingUser the existing user to be updated
   * @param userToUpdate the user object containing the updated information
   */
  private void updateEmailIfNeeded(User existingUser, User userToUpdate) {
    Optional
        .ofNullable(userToUpdate.getEmail())
        .ifPresent(existingUser::setEmail);
  }

  /**
   * Updates the first name of the existing user if the updated user's first name is not null.
   *
   * @param existingUser the existing user to be updated
   * @param userToUpdate the user object containing the updated information
   */
  private void updateFirstNameIfNeeded(User existingUser, User userToUpdate) {
    Optional
        .ofNullable(userToUpdate.getFirstName())
        .ifPresent(existingUser::setFirstName);
  }

  /**
   * Updates the last name of the existing user if the updated user's last name is not null.
   *
   * @param existingUser the existing user to be updated
   * @param userToUpdate the user object containing the updated information
   */
  private void updateLastNameIfNeeded(User existingUser, User userToUpdate) {
    Optional
        .ofNullable(userToUpdate.getLastName())
        .ifPresent(existingUser::setLastName);
  }


}

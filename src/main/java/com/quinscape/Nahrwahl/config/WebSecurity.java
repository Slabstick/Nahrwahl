package com.quinscape.Nahrwahl.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component("webSecurity")
public class WebSecurity {

  public boolean checkUserName(Authentication authentication, String username) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return userDetails.getUsername().equals(username);
  }
}

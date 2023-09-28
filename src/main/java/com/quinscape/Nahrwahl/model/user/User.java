package com.quinscape.Nahrwahl.model.user;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

  @Id
  private String userId;
  private String username;
  private String email;
  private String password;
  private String firstName;
  private String lastName;
  private List<String> nutritionLogs; // List of IDs
  private List<String> foodItems; // List of IDs
  private Instant createdAt;
  private Instant updatedAt;
  private List<String> roles;
  // Maybe Preferences?


}

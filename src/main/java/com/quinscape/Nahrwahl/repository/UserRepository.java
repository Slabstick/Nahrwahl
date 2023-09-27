package com.quinscape.Nahrwahl.repository;

import com.quinscape.Nahrwahl.model.user.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

  Optional<User> findByUsername(String username);
}

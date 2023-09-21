package com.quinscape.Nahrwahl.repository;

import com.quinscape.Nahrwahl.model.FoodItem;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodItemRepository extends MongoRepository<FoodItem, String> {

  Optional<FoodItem> findByNameIgnoreCase(String name);
}

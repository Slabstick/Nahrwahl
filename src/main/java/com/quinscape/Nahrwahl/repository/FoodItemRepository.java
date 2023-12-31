package com.quinscape.Nahrwahl.repository;

import com.quinscape.Nahrwahl.model.foodItem.FoodItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodItemRepository extends MongoRepository<FoodItem, String> {

  Optional<FoodItem> findByNameIgnoreCase(String name);

  List<FoodItem> findByNameContainingIgnoreCase(String name);

}

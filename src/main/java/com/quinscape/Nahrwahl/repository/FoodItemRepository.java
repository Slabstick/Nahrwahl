package com.quinscape.Nahrwahl.repository;

import com.quinscape.Nahrwahl.model.FoodItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FoodItemRepository extends MongoRepository<FoodItem, String> {

}

package com.quinscape.Nahrwahl.service;

import com.quinscape.Nahrwahl.model.FoodItem;
import com.quinscape.Nahrwahl.repository.FoodItemRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodItemService {

  private final FoodItemRepository foodItemRepository;

  public List<FoodItem> getAllFoodItems() {
    return foodItemRepository.findAll();
  }

  public FoodItem createOrUpdateFoodItem(FoodItem newFoodItem) {
    log.info("Service: Creating food item: " + newFoodItem.getName());
    Optional<FoodItem> existingFoodItem = foodItemRepository.findByNameIgnoreCase(newFoodItem.getName());
    if(existingFoodItem.isPresent()) {
      log.info("Service: " + newFoodItem.getName() + " already exists. Updating instead.");
      FoodItem toUpdate = existingFoodItem.get();
      // TODO: Maybe nest all fields in one subclass?
      toUpdate.setCalories(newFoodItem.getCalories());
      toUpdate.setFat(newFoodItem.getFat());
      toUpdate.setProtein(newFoodItem.getProtein());
      toUpdate.setUser_id(newFoodItem.getUser_id());
      toUpdate.setCarbohydrates(newFoodItem.getCarbohydrates());
      return foodItemRepository.save(toUpdate);
    } else {
      return foodItemRepository.save(newFoodItem);
    }
  }



}

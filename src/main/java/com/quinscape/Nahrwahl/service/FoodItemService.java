package com.quinscape.Nahrwahl.service;

import com.quinscape.Nahrwahl.model.FoodItem;
import com.quinscape.Nahrwahl.repository.FoodItemRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodItemService {

  private final FoodItemRepository foodItemRepository;

  public List<FoodItem> getAllFoodItems(Sort sort) {
    return foodItemRepository.findAll(sort);
  }

  public FoodItem createOrUpdateFoodItem(FoodItem newFoodItem) {
    log.info("Service: Creating food item: " + newFoodItem.getName());
    Optional<FoodItem> existingFoodItem = foodItemRepository.findByNameIgnoreCase(newFoodItem.getName());
    if(existingFoodItem.isPresent()) {
      log.info("Service: " + newFoodItem.getName() + " already exists. Updating instead.");
      FoodItem toUpdate = existingFoodItem.get();
      toUpdate.setNutrients(newFoodItem.getNutrients());
      return foodItemRepository.save(toUpdate);
    } else {
      return foodItemRepository.save(newFoodItem);
    }
  }





}

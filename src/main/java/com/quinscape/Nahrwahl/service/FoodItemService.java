package com.quinscape.Nahrwahl.service;

import com.quinscape.Nahrwahl.exception.FoodItemNotFoundException;
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

  public boolean deleteFoodItem(String id) {

    if(foodItemRepository.existsById(id)) {
      log.info("Service: Deleting food item: " + id);
      foodItemRepository.deleteById(id);
      return true;
    }
    log.info("Service: Couldn't delete food item. Not found!");
    return false;
  }


  public Optional<FoodItem> getFoodItemById(String id) {
    return foodItemRepository.findById(id);
  }

  public List<FoodItem> searchFoodItems(String searchTerm) {
    return foodItemRepository.findByNameContainingIgnoreCase(searchTerm);
  }

  public FoodItem updateFoodItem(String id, FoodItem updatedFoodItem) {
    Optional<FoodItem> optionalOriginalFoodItem = foodItemRepository.findById(id);

    return optionalOriginalFoodItem.map(originalFoodItem -> {
      Optional.ofNullable(updatedFoodItem.getName()).ifPresent(originalFoodItem::setName);
      Optional.ofNullable(updatedFoodItem.getNutrients()).ifPresent(originalFoodItem::setNutrients);
      return foodItemRepository.save(originalFoodItem);
    }).orElseThrow(() -> new FoodItemNotFoundException("Food item with id " + id + " not found."));
  }

}

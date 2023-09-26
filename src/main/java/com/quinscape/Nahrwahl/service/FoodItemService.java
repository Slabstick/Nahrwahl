package com.quinscape.Nahrwahl.service;

import com.quinscape.Nahrwahl.exception.FoodItemNotFoundException;
import com.quinscape.Nahrwahl.model.FoodItem;
import com.quinscape.Nahrwahl.repository.FoodItemRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodItemService {


  private final FoodItemRepository foodItemRepository;


  public List<FoodItem> getAllFoodItemsSorted(String sortBy, Direction direction) {
    String prefixedSortBy = getPrefixedSortBy(sortBy);
    Sort sort = Sort.by(direction, prefixedSortBy);
    return foodItemRepository.findAll(sort);
  }

  private String getPrefixedSortBy(String sortBy) {
    String prefixedSortBy = "nutrients." + sortBy;

    if ("fiber".equals(sortBy) || "sugar".equals(sortBy) || "carbsTotal".equals(sortBy)) {
      prefixedSortBy = "nutrients.carbohydrates." + sortBy;
    } else if ("name".equals(sortBy)) {
      prefixedSortBy = "name";
    }

    return prefixedSortBy;

  }

//  public FoodItem createOrUpdateFoodItemOld(FoodItem newFoodItem) {
//    log.info("Service: Creating food item: " + newFoodItem.getName());
//    Optional<FoodItem> optionalExistingFoodItem = foodItemRepository.findByNameIgnoreCase(newFoodItem.getName());
//    FoodItem itemToUpdate = newFoodItem;
//    if(optionalExistingFoodItem.isPresent()) {
//      log.info("Service: " + newFoodItem.getName() + " already exists. Updating instead.");
//      FoodItem existingFoodItem = optionalExistingFoodItem.get();
//      existingFoodItem.setNutrients(newFoodItem.getNutrients());
//      itemToUpdate = existingFoodItem;
//    }
//    return foodItemRepository.save(itemToUpdate);
//  }

  public FoodItem createOrUpdateFoodItem(FoodItem newFoodItem) {
    log.info("Service: Creating food item: " + newFoodItem.getName());

    Optional<FoodItem> optionalExistingFoodItem = foodItemRepository.findByNameIgnoreCase(newFoodItem.getName());

    return foodItemRepository.save(
      optionalExistingFoodItem.map(existingFoodItem -> {
        log.info("Service: " + newFoodItem.getName() + " already exists. Updating instead.");
        existingFoodItem.setNutrients(newFoodItem.getNutrients());
        return existingFoodItem;
      }).orElse(newFoodItem)
    );
  }

  @Transactional
  public List<FoodItem> createOrUpdateFoodItemsBulk(List<FoodItem> newFoodItems) {
    return newFoodItems.stream()
        .map(this::createOrUpdateFoodItem)
        .collect(Collectors.toList());
  }

  public boolean deleteFoodItem(String id) {

    if(foodItemRepository.existsById(id)) {
      log.info("Service: Deleting food item: " + id);
      foodItemRepository.deleteById(id);
      return true;
    }
    throw new FoodItemNotFoundException("Service: Couldn't delete food item with id: " + id + " not found!");
  }


  public Optional<FoodItem> getFoodItemById(String id) {
    return foodItemRepository.findById(id);
  }

  public List<FoodItem> searchFoodItems(String searchTerm) {
    return foodItemRepository.findByNameContainingIgnoreCase(searchTerm);
  }

  public FoodItem updateFoodItem(String id, FoodItem updatedFoodItem) {
    return foodItemRepository.findById(id)
        .map(originalFoodItem -> {
      Optional.ofNullable(updatedFoodItem.getName()).ifPresent(originalFoodItem::setName);
      Optional.ofNullable(updatedFoodItem.getNutrients()).ifPresent(originalFoodItem::setNutrients);
      return foodItemRepository.save(originalFoodItem);
    })
        .orElseThrow(() -> new FoodItemNotFoundException("Food item with id " + id + " not found."));
  }

}

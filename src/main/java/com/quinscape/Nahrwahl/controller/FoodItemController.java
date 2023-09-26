package com.quinscape.Nahrwahl.controller;

import com.quinscape.Nahrwahl.model.FoodItem;
import com.quinscape.Nahrwahl.service.FoodItemService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/foodItems")
public class FoodItemController {

  private final FoodItemService foodItemService;

  @GetMapping
  public ResponseEntity<List<FoodItem>> getAllFoodItems(
      @RequestParam(name = "sortBy", required = false, defaultValue = "name") String sortBy,
      @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction) {

    log.info("Controller: Fetching list of food items sorted by " + sortBy);
    Direction sortDirection = Direction.fromString(direction);
    List<FoodItem> foodItems = foodItemService.getAllFoodItemsSorted(sortBy, sortDirection);

    return new ResponseEntity<>(foodItems, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<FoodItem> createOrUpdateFoodItem(@RequestBody FoodItem newFoodItem) {
    log.info("Controller: Creating or updating Food Item: " + newFoodItem.getName());
    FoodItem savedFoodItem = foodItemService.createOrUpdateFoodItem(newFoodItem);
    return new ResponseEntity<>(savedFoodItem, HttpStatus.CREATED);
  }

  @PostMapping("/bulk")
  public ResponseEntity<List<FoodItem>> createOrUpdateFoodItems(
      @RequestBody List<FoodItem> newFoodItems) {
    log.info("Controller: Creating or updating one or multiple food items");
    return new ResponseEntity<>(foodItemService.createOrUpdateFoodItemsBulk(newFoodItems),
        HttpStatus.CREATED);
  }


  @PatchMapping("/{id}")
  public ResponseEntity<FoodItem> updateFoodItem(@PathVariable String id,
      @RequestBody FoodItem foodItem) {
    log.info("Controller: Updating food item Id: " + id);
    FoodItem updatedFoodItem = foodItemService.updateFoodItem(id, foodItem);
    return new ResponseEntity<>(updatedFoodItem, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFoodItem(@PathVariable String id) {
    log.info("Controller: Deleting food item");
    if (foodItemService.deleteFoodItem(id)) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<FoodItem> getFoodItemById(@PathVariable String id) {
    return foodItemService.getFoodItemById(id)
        .map(foodItem -> new ResponseEntity<>(foodItem, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping("/search")
  public ResponseEntity<List<FoodItem>> searchFoodItems(@RequestParam String searchTerm) {
    List<FoodItem> foodItems = foodItemService.searchFoodItems(searchTerm);
    return new ResponseEntity<>(foodItems, HttpStatus.OK);
  }

}

package com.quinscape.Nahrwahl.controller;

import com.quinscape.Nahrwahl.model.FoodItem;
import com.quinscape.Nahrwahl.service.FoodItemService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

  @GetMapping("")
  public ResponseEntity<List<FoodItem>> getAllFoodItems(
      @RequestParam(name = "sortBy", required = false, defaultValue = "name") String sortBy,
      @RequestParam(name = "direction", required = false, defaultValue = "DESC") String direction) {

    log.info("Controller: Fetching list of food items sorted by " + sortBy);

    // Alias to be able to sort by fiber, sugar and carbsTotal instead of needing
    // to add the prefix carbohydrates. in the params.
    String prefixedSortBy = "nutrients." + sortBy;
    if ("fiber".equals(sortBy) || "sugar".equals(sortBy) || "carbsTotal".equals(sortBy)) {
      prefixedSortBy = "nutrients.carbohydrates." + sortBy;
    }

    Sort sort = Sort.by(Direction.fromString(direction), prefixedSortBy);

    List<FoodItem> foodItems = foodItemService.getAllFoodItems(sort);

    return new ResponseEntity<>(foodItems, HttpStatus.OK);
  }

  @PostMapping("")
  public ResponseEntity<FoodItem> createOrUpdateFoodItem(@RequestBody FoodItem newFoodItem) {
    log.info("Controller: Creating or updating Food Item: " + newFoodItem.getName());
    FoodItem savedFoodItem = foodItemService.createOrUpdateFoodItem(newFoodItem);
    return new ResponseEntity<>(savedFoodItem, HttpStatus.CREATED);
  }

  @PostMapping("/bulk")
  public ResponseEntity<List<FoodItem>> createOrUpdateFoodItems(@RequestBody List<FoodItem> newFoodItems) {
    log.info("Controller: Creating or updating multiple food items");
    List<FoodItem> savedFoodItems = new ArrayList<>();

    for(FoodItem newFoodItem : newFoodItems) {
      FoodItem savedFoodItem = foodItemService.createOrUpdateFoodItem(newFoodItem);
      savedFoodItems.add(savedFoodItem);
    }

    return new ResponseEntity<>(savedFoodItems, HttpStatus.CREATED);
  }

}

package com.quinscape.Nahrwahl.controller;

import com.quinscape.Nahrwahl.service.FoodItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class FoodItemController {

  private FoodItemService foodItemService;

  @GetMapping("/foodItems")
  public String getFoodItems() {
    log.info("Fetching list of food items");
    return "Placeholder List of Food Items";
  }

}

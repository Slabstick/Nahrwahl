package com.quinscape.Nahrwahl.model.nutritionLog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemEntry {

  private String foodItemId;
  private int quantity;
}

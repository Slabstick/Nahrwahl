package com.quinscape.Nahrwahl.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// Don't need getters and setters with @Data
@Data
@Document(collection = "food_items")
public class FoodItem implements Comparable<FoodItem>{

  @Id
  private ObjectId id;
  private String name;
  private ObjectId user_id; // reference to user who added the item
  private Nutrients nutrients;

  @Override
  public int compareTo(FoodItem other) {
    return this.name.compareTo(other.name);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    FoodItem other = (FoodItem) object;

    // Deep equality check:
    return nutrients.equals(other.nutrients);

  }

  // maybe vitamins and minerals? saturated fats?

  @Data
  public static class Nutrients {
    private double calories;
    private double protein;
    private double fat;
    private Carbohydrates carbohydrates;
  }

  @Data
  public static class Carbohydrates {
    private double sugar;
    private double fiber;
    private double carbsTotal;
  }



}

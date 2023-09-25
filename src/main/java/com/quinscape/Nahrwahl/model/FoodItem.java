package com.quinscape.Nahrwahl.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "foodItems")
public class FoodItem {

  public FoodItem() {}

  @Id
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId id;

  private String name;
  private String user_id; // reference to user who added the item
  private Nutrients nutrients;


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

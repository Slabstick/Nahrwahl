package com.quinscape.Nahrwahl.model.foodItem;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "foodItems")
public class FoodItem {

  @Id
  private String id;
  private String name;
  private String user_id; // reference to user who added the item
  private Nutrients nutrients;

}
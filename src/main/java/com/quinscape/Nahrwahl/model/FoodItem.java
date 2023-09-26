package com.quinscape.Nahrwahl.model;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "foodItems")
public class FoodItem {

  @Id
  private ObjectId id;
  private String name;
  private String user_id; // reference to user who added the item
  private Nutrients nutrients;

}
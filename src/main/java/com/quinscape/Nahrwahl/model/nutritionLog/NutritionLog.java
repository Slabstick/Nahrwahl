package com.quinscape.Nahrwahl.model.nutritionLog;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "nutritionLogs")
public class NutritionLog {

  @Id
  private String id;
  private String userId;
  private Date date;
  private List<FoodItemEntry> foodEntries;
  private double totalCalories;
  private double totalProtein;
  private double totalFat;
  private TotalCarbohydrates totalCarbs;
  private String notes;


}

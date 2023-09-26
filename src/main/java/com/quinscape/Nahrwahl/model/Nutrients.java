package com.quinscape.Nahrwahl.model;

import lombok.Data;

@Data
public class Nutrients {

  private double calories;
  private double protein;
  private double fat;
  private Carbohydrates carbohydrates;
}

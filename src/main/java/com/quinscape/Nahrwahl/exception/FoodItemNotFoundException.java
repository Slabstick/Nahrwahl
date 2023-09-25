package com.quinscape.Nahrwahl.exception;

public class FoodItemNotFoundException extends RuntimeException{

  public FoodItemNotFoundException(String message) {
    super(message);
  }

}

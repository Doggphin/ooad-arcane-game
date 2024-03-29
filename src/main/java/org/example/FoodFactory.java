package org.example;

public class FoodFactory {
    Food createFood(String name){
        return new Food(name);
    }
}

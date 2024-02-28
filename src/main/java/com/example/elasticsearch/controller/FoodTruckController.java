package com.example.elasticsearch.controller;


import com.example.elasticsearch.entity.FoodTruck;
import com.example.elasticsearch.service.FoodTruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class FoodTruckController {

    @Autowired
    private FoodTruckService foodTruckService;

    @GetMapping("/findAll")
    Iterable<FoodTruck> findAll(){
        return foodTruckService.getFoodTrucks();
    }

    @PostMapping("/create")
    public FoodTruck saveFoodTrucks(@RequestBody FoodTruck foodTruck){
        return foodTruckService.saveFoodTrucks(foodTruck);
    }

    @PostMapping("/getEsFoodTrucks")
    public List<FoodTruck> getEsFoodTrucks(@RequestBody Map keywords) throws IOException {
        return foodTruckService.getEsFoodTrucks(keywords);
    }

    @GetMapping("/syncToES")
    Iterable<FoodTruck> bulkSaveFoodTrucks() throws IOException{
        return foodTruckService.bulkSaveFoodTrucks();
    }

    @GetMapping("/deleteAll")
    public void deleteAllFoodTrucks(){
        foodTruckService.deleteAll();
    }


    @GetMapping("/getFoods")
    public List<String> getFoods() throws IOException {
        return foodTruckService.getFoods();
    }
}

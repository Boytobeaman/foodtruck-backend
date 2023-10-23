package com.example.elasticsearch.repo;

import com.example.elasticsearch.entity.FoodTruck;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FoodTruckRepo extends ElasticsearchRepository<FoodTruck,Integer> {
}

package com.example.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.elasticsearch.entity.FoodTruck;
import com.example.elasticsearch.repo.FoodTruckRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class FoodTruckService {

    @Autowired
    private FoodTruckRepo foodTruckRepo;

    @Autowired
    ElasticsearchClient client;


    public Iterable<FoodTruck> getFoodTrucks(){
        return foodTruckRepo.findAll();
    }


    public FoodTruck saveFoodTrucks(FoodTruck foodTruck){
        return foodTruckRepo.save(foodTruck);
    }

    public Iterable<FoodTruck> bulkSaveFoodTrucks() throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("foodtruck.json");


        // Read the JSON file into a Java object
        List<Object> foodTrucks = mapper.readValue(inputStream, List.class);


        ArrayList<FoodTruck> foodTruckToEs = new ArrayList<FoodTruck>();
        for (int i = 0; i < foodTrucks.size(); i++) {
            Object foodtruck = foodTrucks.get(i);

            FoodTruck thisFoodTruck = new FoodTruck();
            String id = ((LinkedHashMap) foodtruck).get("objectid").toString();
            thisFoodTruck.setId(id);

            Object fooditemsObj = ((LinkedHashMap) foodtruck).get("fooditems");
            String fooditems;
            if(fooditemsObj != null){
                fooditems = fooditemsObj.toString();
            }else{
                fooditems = "";
            }
            thisFoodTruck.setFooditems(fooditems);

            String locationdescription;
            Object locationdescriptionObj = ((LinkedHashMap) foodtruck).get("locationdescription");
            if (locationdescriptionObj != null){
                locationdescription = locationdescriptionObj.toString();
            }else{
                locationdescription = "";
            }
            thisFoodTruck.setLocationdescription(locationdescription);


            Object applicantObj = ((LinkedHashMap) foodtruck).get("applicant");
            String applicant;
            if(applicantObj != null){
                applicant = applicantObj.toString();
            }else{
                applicant = "";
            }
            thisFoodTruck.setApplicant(applicant);



            foodTruckToEs.add(thisFoodTruck);
        }
        if(foodTruckToEs.size() > 0){
            return foodTruckRepo.saveAll(foodTruckToEs);
        }
        return null;
    }


    public List<String> getFoods() throws IOException{



        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("foodtruck.json");

        ObjectMapper mapper = new ObjectMapper();

        // Read the JSON file into a Java object
        List<Object> foodTrucks = mapper.readValue(inputStream, List.class);
        HashSet<String> foods = new HashSet<String>();


        for (int i = 0; i < foodTrucks.size(); i++) {
            Object foodtruck = foodTrucks.get(i);
            Object ft = ((LinkedHashMap) foodtruck).get("fooditems");
            if(ft != null){
                String[] thisList = ft.toString().split(":|;|\\.");
                for (int j = 0; j < thisList.length; j++) {
                    foods.add(thisList[j].trim());
                }
            }
        }

        ArrayList<String> list =new ArrayList<>(); //Creation of ArrayList
        list.addAll(foods); //HashSet to ArrayList
        return list;
    }


    public List<FoodTruck> getEsFoodTrucks(Map<String,ArrayList> keywords) throws IOException{


        List<String> theKeywords = keywords.get("keywords");

        String combinedKeywordsString = "";
        for (int i = 0; i < theKeywords.size(); i++) {
//            combinedKeywordsString += theKeywords.get(i);
            combinedKeywordsString = String.format("%s %s", combinedKeywordsString, theKeywords.get(i));
            System.out.println(theKeywords.get(i));
        }
        System.out.println(combinedKeywordsString);

//        TermsQueryField theKeywordsTerms = new TermsQueryField.Builder()
//                .value(theKeywords.stream().map(FieldValue::of).toList())
//                .build();


        String finalCombinedKeywordsString = combinedKeywordsString;
        SearchResponse<FoodTruck> searchResponse = client.search(s -> s
                        .index("foodtruck")
                        .query(q -> q
//                                .terms(t -> t
//                                        .field("fooditems")
//                                        .terms(theKeywordsTerms)
//                                )
                                        .match(t -> t
                                                .field("fooditems")
                                                .query(finalCombinedKeywordsString)
                                        )
                        ),
                FoodTruck.class
        );

        System.out.println(theKeywords);

        List<Hit<FoodTruck>> hits = searchResponse.hits().hits();

        List<FoodTruck> foodTrucks = new ArrayList<>();
        for (Hit<FoodTruck> hit: hits) {
            FoodTruck foodTruckSource = hit.source();
            foodTrucks.add(foodTruckSource);
        }

        return foodTrucks;
    }


    public void deleteAll(){
        foodTruckRepo.deleteAll();
    }
}

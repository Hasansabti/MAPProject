package com.hzstore.mapproject.models;

import com.squareup.moshi.Json;

import java.util.List;

public class Product {
    int id;
    double price = 0;
    String desc;
    String name;
    String image;
    @Json(name = "revavg")
    float avg_reviews;
    List<Review> reviews;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }


    public String getDesc() {
        return desc;
    }


    public String getName() {
        return name;
    }



    public String getImage() {
        return image;
    }


    public float getAvg_reviews() {
        return avg_reviews;
    }

    public List<Review> getReviews() {
        return reviews;
    }
}

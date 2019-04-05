package com.hzstore.mapproject.models;

import com.squareup.moshi.Json;

import java.util.List;

public class Order {

    int id;

    List<Orderitem> items;

    @Json(name = "created_at")
    String orderdate;
    String tracking;

    public int getId() {
        return id;
    }

    public List<Orderitem> getItems() {
        return items;
    }



    public String getOrderdate() {
        return orderdate;
    }

    public String getTracking() {
        return tracking;
    }
}

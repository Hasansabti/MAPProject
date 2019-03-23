package com.hzstore.mapproject.net.requests;

import com.hzstore.mapproject.models.Order;

import java.util.List;

public class OrdersResponse {
    List<Order> data;

    public List<Order> getData() {
        return data;
    }
}

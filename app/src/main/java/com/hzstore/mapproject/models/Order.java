package com.hzstore.mapproject.models;

import java.util.List;

public class Order {

    int id;

    List<Orderitem> items;
    String body;
    String orderdate;
    String trackingnumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<Orderitem> getItems() {
        return items;
    }

    public void setItems(List<Orderitem> items) {
        this.items = items;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(String orderdate) {
        this.orderdate = orderdate;
    }

    public String getTrackingnumber() {
        return trackingnumber;
    }

    public void setTrackingnumber(String trackingnumber) {
        this.trackingnumber = trackingnumber;
    }
}

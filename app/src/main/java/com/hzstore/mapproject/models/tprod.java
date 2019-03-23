package com.hzstore.mapproject.models;

public class tprod {
    private int id;
    private String name;
    private String image;
    private double price;
    private String desc;
    private int stock;


    public tprod(int id, String name, String image, double price, String desc, int stock) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.desc = desc;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}

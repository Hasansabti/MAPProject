package com.hzstore.mapproject.models;

import java.util.List;

public class Cart {

    int id;
    List<Cartitem> cartitem;
    String total;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {

        this.total = total;
    }

    public List<Cartitem> getCartitem() {
        return cartitem;
    }

    public void setCartitem(List<Cartitem> cartitem) {
        this.cartitem = cartitem;
    }
}

package com.hzstore.mapproject.net.requests;

import com.hzstore.mapproject.models.Address;
import com.hzstore.mapproject.models.Order;

import java.util.List;

public class AddressResponse {
    List<Address> data;

    public List<Address> getData() {
        return data;
    }
}

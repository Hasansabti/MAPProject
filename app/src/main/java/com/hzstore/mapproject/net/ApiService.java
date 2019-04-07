package com.hzstore.mapproject.net;

import com.hzstore.mapproject.models.AccessToken;
import com.hzstore.mapproject.models.Address;
import com.hzstore.mapproject.models.Cart;
import com.hzstore.mapproject.models.Cartitem;
import com.hzstore.mapproject.models.Order;
import com.hzstore.mapproject.models.Post;
import com.hzstore.mapproject.models.Product;

import com.hzstore.mapproject.models.User;
import com.hzstore.mapproject.net.requests.ValueResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiService {

    @POST("register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("name") String name, @Field("email") String email, @Field("password") String password);

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("username") String username, @Field("password") String password);



    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @GET("posts")
    Call<Post> posts();

    @GET("cart")
    Call<Cart> cart();

    @POST("cart/deleteitem")
    @FormUrlEncoded
    Call<Cart> deleteCartItems(@Field("ids") String items);


    @POST("cart/addtocart")
    @FormUrlEncoded
    Call<Cart> addtocart(@Field("id") int product_id, @Field("count") int count);

    @POST("cart/updateitem")
    @FormUrlEncoded
    Call<Cartitem> updatecount(@Field("id") int product_id, @Field("count") int count);


    @POST("account/orders")
    Call<List<Order>> orders();

    @POST("account/address/add")
    @FormUrlEncoded
    Call<Address> add_addresses(@Field("fname") String fname, @Field("lname") String lname, @Field("address1") String address1, @Field("address2") String address2, @Field("postcode") String code, @Field("phone") String phone);

    @POST("account/address")
    Call<List<Address>> addresses();

    @POST("cart/count")
    Call<ValueResponse> cartcount();

    @GET("store")
    Call<List<Product>> products();

    @GET("product/q")
    Call<Product> product(@Query("id") int product_id);


    @GET("account")
    Call<User> userdata();



}

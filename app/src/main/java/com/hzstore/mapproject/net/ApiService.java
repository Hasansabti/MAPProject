package com.hzstore.mapproject.net;

import com.hzstore.mapproject.models.AccessToken;
import com.hzstore.mapproject.models.User;
import com.hzstore.mapproject.net.requests.AddtocartResponse;
import com.hzstore.mapproject.net.requests.CartResponse;
import com.hzstore.mapproject.net.requests.OrdersResponse;
import com.hzstore.mapproject.net.requests.PostResponse;
import com.hzstore.mapproject.net.requests.ProductResponse;
import com.hzstore.mapproject.net.requests.ProductsResponse;
import com.hzstore.mapproject.net.requests.UserResponse;

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
    Call<PostResponse> posts();

    @GET("cart")
    Call<CartResponse> cart();


    @POST("cart/addtocart")
    @FormUrlEncoded
    Call<AddtocartResponse> addtocart(@Field("id") int product_id, @Field("count") int count);

    @POST("orders")
    Call<OrdersResponse> orders();

    @GET("store")
    Call<ProductsResponse> products();

    @GET("product/q")
    Call<ProductResponse> product(@Query("id") int product_id);


    @GET("account")
    Call<UserResponse> userdata();


}

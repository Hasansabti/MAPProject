package com.hzstore.mapproject;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hzstore.mapproject.adapters.CartItemRecyclerViewAdapter;
import com.hzstore.mapproject.fragments.CartItemFragment;
import com.hzstore.mapproject.net.ApiError;
import com.hzstore.mapproject.models.Cart;
import com.hzstore.mapproject.models.Cartitem;
import com.hzstore.mapproject.net.ApiService;
import com.hzstore.mapproject.net.RetrofitBuilder;
import com.hzstore.mapproject.net.requests.CartResponse;

import retrofit2.Call;
import retrofit2.Callback;

public class CartActivity extends AppCompatActivity implements CartItemRecyclerViewAdapter.ItemListener {
    private static final String TAG = "CartActivity";
Cart mycart;
    CartItemFragment cif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getItems();
    }

    public void gotoCheckout(View v){
        Intent intent = new Intent(this,CheckoutActivity.class);
        startActivity(intent);
    }

    public void getItems(){
        if(HomeActivity.app.isLoggedin()) {
//initialize products call
            final Call<CartResponse> cart_call;
            ApiService authservice = RetrofitBuilder.createServiceWithAuth(ApiService.class, HomeActivity.app.tokenManager);

            cart_call = authservice.cart();
            cart_call.enqueue(new Callback<CartResponse>() {
                @Override
                public void onResponse(Call<CartResponse> call, retrofit2.Response<CartResponse> response) {
//print response

                    Gson gson = new Gson();
                    Log.d("CA", gson.toJson(response.body()));

                    Log.w(TAG, "onResponse: " + response);

                    //check the validity of the response
                    if (response.isSuccessful()) {
                        mycart = response.body().getData();



                        cif = CartItemFragment.newInstance(mycart.getCartitem());

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                        ft.add(R.id.cart_list, cif).commit();
                        int totalItems = 0;
                        for(Cartitem ci : mycart.getCartitem()){
                            totalItems += ci.getCount();
                        }

                        ((TextView)findViewById(R.id.tv_total)).setText(totalItems +" item/s");
                        ((TextView)findViewById(R.id.cart_total)).setText("Total "+mycart.getTotal() +" SR");

                    } else {
                        if (response.code() == 422) {
                            // handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = com.hzstore.mapproject.Utils.converErrors(response.errorBody());
                            Toast.makeText(CartActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }

                @Override
                public void onFailure(Call<CartResponse> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());

                }
            });
        }else{
            Toast.makeText(this,"You are not logged in",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cartitem_remove) {
            deleteSelected();

            return true;
        }else   if (id==android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCountUpdate(Cartitem cartitem) {
        int total = 0;
        for(Cartitem ci : mycart.getCartitem()){
            if(cartitem.getId() == ci.getId()) {
                total += cartitem.getCount();
            }else{
                total += ci.getCount();
            }
        }

        ((TextView)findViewById(R.id.tv_total)).setText(total +" item/s");
        ((TextView)findViewById(R.id.cart_total)).setText("Total "+mycart.getTotal() +" SR");
        Toast.makeText(getApplicationContext(),"Item count updated",Toast.LENGTH_SHORT).show();

    }

    public void deleteSelected(){
        if(HomeActivity.app.isLoggedin()) {
//initialize products call
            final Call<CartResponse> cart_call;
            ApiService authservice = RetrofitBuilder.createServiceWithAuth(ApiService.class, HomeActivity.app.tokenManager);
Gson gson = new Gson();
            cart_call = authservice.deleteCartItems(gson.toJson(  cif.selecteditemIds()));
            cart_call.enqueue(new Callback<CartResponse>() {
                @Override
                public void onResponse(Call<CartResponse> call, retrofit2.Response<CartResponse> response) {
//print response

                    Gson gson = new Gson();
                    Log.d("CA", gson.toJson(response.body()));

                    Log.w(TAG, "onResponse: " + response);

                    //check the validity of the response
                    if (response.isSuccessful()) {
                        mycart = response.body().getData();


                        int totalItems = 0;
                        for(Cartitem ci : mycart.getCartitem()){
                            totalItems += ci.getCount();
                        }
                        //update recyclerview

                       cif.rva.updateData(mycart.getCartitem());

                        ((TextView)findViewById(R.id.tv_total)).setText(totalItems +" item/s");
                        ((TextView)findViewById(R.id.cart_total)).setText("Total "+mycart.getTotal() +" SR");

                    } else {
                        if (response.code() == 422) {
                            // handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = com.hzstore.mapproject.Utils.converErrors(response.errorBody());
                            Toast.makeText(CartActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }

                @Override
                public void onFailure(Call<CartResponse> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());

                }
            });
        }else{
            Toast.makeText(this,"You are not logged in",Toast.LENGTH_SHORT).show();
        }
    }
}

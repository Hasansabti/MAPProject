package com.hzstore.mapproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hzstore.mapproject.adapters.ProductReviewAdapter;
import com.hzstore.mapproject.models.Cart;
import com.hzstore.mapproject.net.ApiError;
import com.hzstore.mapproject.models.Product;
import com.hzstore.mapproject.net.ApiService;
import com.hzstore.mapproject.net.RetrofitBuilder;

import com.hzstore.mapproject.net.requests.ValueResponse;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;

public class ProductActivity extends AppCompatActivity {
    private static final String TAG = "ProductActivity";
    Product product = null;
    ApiService service;
    TextView cartcounttv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, HomeActivity.app.tokenManager);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //for network process
                addtoCart(view);
                   /*
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);

                Snackbar.make(view, "Item has been added to your cart", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                        */


            }
        });

        requestProduct(Integer.parseInt(getIntent().getExtras().getString("productID")));
        setTitle(getIntent().getExtras().getString("title"));
        setupActionBar();




        Button cartbtn = findViewById(R.id.cartbtn);
        /*
        cartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addtoCart(product.getId());
            }
        });
        */

        if(HomeActivity.app.isLoggedin()){
            updateCCount();
        }else{

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        RelativeLayout notificationCount1;
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem item1 = menu.findItem(R.id.action_cart);
        MenuItemCompat.setActionView(item1, R.layout.cart_num);
        notificationCount1 = (RelativeLayout) MenuItemCompat.getActionView(item1);
        notificationCount1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });
        cartcounttv = notificationCount1.findViewById(R.id.cart_count);
        return true;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_cart) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get the product details from the HZ-store api
     *
     * @param productID
     */
    public void requestProduct(final int productID) {


        //initialize products call
        final Call<Product> product_call;

        product_call = service.product(productID);
        product_call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, retrofit2.Response<Product> response) {
                //print response
                Log.w(TAG, "onResponse: " + response);

                //check the validity of the response
                if (response.isSuccessful()) {

                    Gson gson = new Gson();
                    Log.d("PA", gson.toJson(response.body()));

                    product = response.body();
                    ImageView img = findViewById(R.id.productimg);
                    TextView price = findViewById(R.id.price);
                    RatingBar rate = findViewById(R.id.product_rating);
                    rate.setRating(product.getAvg_reviews());

                    price.setText("" + product.getPrice() + " SR");
                    ProductReviewAdapter adapter = new ProductReviewAdapter(product.getReviews());
                    RecyclerView reviews = findViewById(R.id.reviews);
                    reviews.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

                    reviews.setAdapter(adapter);
                    //   Log.d("HZStore", "Name is: " + response.toString());


                    Picasso.get().load("http://sabti-h.tech/hz-store/storage/img/" + product.getImage()).into(img);

                } else {
                    if (response.code() == 422) {
                        // handleErrors(response.errorBody());
                    }
                    if (response.code() == 401) {
                        ApiError apiError = com.hzstore.mapproject.Utils.converErrors(response.errorBody());
                        Toast.makeText(ProductActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());

            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        if(HomeActivity.app. isLoggedin())
            updateCCount();
    }
    public void updateCCount(){

        //refresh cart count


        final Call<ValueResponse> cart_count;
        ApiService service = RetrofitBuilder.createServiceWithAuth(ApiService.class,HomeActivity.app.tokenManager);
        cart_count = service.cartcount();
        cart_count.enqueue(new Callback<ValueResponse>() {
            @Override
            public void onResponse(Call<ValueResponse> call, retrofit2.Response<ValueResponse> response) {
//print response
                Log.w(TAG, "onResponse: " + response);

                //check the validity of the response
                if (response.isSuccessful()) {

                    //cart count is returned
                    double count = (Double) response.body().getData();

                    cartcounttv.setText(""+(int)count);
                } else {
                    if (response.code() == 422) {
                        // handleErrors(response.errorBody());
                    }
                    if (response.code() == 401) {
                        ApiError apiError = Utils.converErrors(response.errorBody());
                        Toast.makeText(ProductActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<ValueResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());

            }
        });
    }
    //network function
    public void addtoCart(final View v) {

        if (HomeActivity.app.isLoggedin()) {
//initialize products call
            final Call<Cart> addcart_call;
            ApiService authservice = RetrofitBuilder.createServiceWithAuth(ApiService.class, HomeActivity.app.tokenManager);

            addcart_call = authservice.addtocart(product.getId(), 1);
            addcart_call.enqueue(new Callback<Cart>() {
                @Override
                public void onResponse(Call<Cart> call, retrofit2.Response<Cart> response) {
//print response
                    Log.w(TAG, "onResponse: " + response);

                    //check the validity of the response
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Product has been added to cart", Toast.LENGTH_SHORT).show();


                        Snackbar.make(v, "Item has been added to your cart", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                        startActivity(intent);

                    } else {
                        if (response.code() == 422) {
                            // handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = com.hzstore.mapproject.Utils.converErrors(response.errorBody());
                            Toast.makeText(ProductActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }

                @Override
                public void onFailure(Call<Cart> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());

                }
            });
        } else {
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
        }
    }




}

package com.hzstore.mapproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hzstore.mapproject.net.ApiError;
import com.hzstore.mapproject.models.Product;
import com.hzstore.mapproject.net.ApiService;
import com.hzstore.mapproject.net.RetrofitBuilder;
import com.hzstore.mapproject.net.requests.AddtocartResponse;
import com.hzstore.mapproject.net.requests.ProductResponse;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;

public class ProductActivity extends AppCompatActivity {
    private static final String TAG = "ProductActivity";
    Product product = null;
    ApiService service;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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
        return super.onOptionsItemSelected(item);
    }

    public void requestProduct(final int productID) {


//initialize products call
        final Call<ProductResponse> product_call;

        product_call = service.product(productID);
        product_call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, retrofit2.Response<ProductResponse> response) {
//print response
                Log.w(TAG, "onResponse: " + response);

                //check the validity of the response
                if (response.isSuccessful()) {

                    Gson gson = new Gson();
                    Log.d("PA", gson.toJson(response.body()));

                    product = response.body().getData();
                    ImageView img = findViewById(R.id.productimg);
                    TextView price = findViewById(R.id.price);

                    price.setText("" + product.getPrice() + " SR");


                    //   Log.d("HZStore", "Name is: " + response.toString());

                    new DownloadImageTask(img).execute(product.getImage());
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
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());

            }
        });


    }


    //network function
    public void addtoCart(final View v) {

if(HomeActivity.app.isLoggedin()) {
//initialize products call
    final Call<AddtocartResponse> addcart_call;
    ApiService authservice = RetrofitBuilder.createServiceWithAuth(ApiService.class, HomeActivity.app.tokenManager);

    addcart_call = authservice.addtocart(product.getId(), 1);
    addcart_call.enqueue(new Callback<AddtocartResponse>() {
        @Override
        public void onResponse(Call<AddtocartResponse> call, retrofit2.Response<AddtocartResponse> response) {
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
        public void onFailure(Call<AddtocartResponse> call, Throwable t) {
            Log.w(TAG, "onFailure: " + t.getMessage());

        }
    });
}else{
    Toast.makeText(this,"You are not logged in",Toast.LENGTH_SHORT).show();
}
    }

    //Thread to download Image
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView pImage;

        public DownloadImageTask(ImageView bmImage) {
            this.pImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL("http://sabti-h.tech/hz-store/storage/img/" + urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            pImage.setImageBitmap(Bitmap.createScaledBitmap(result, 1200, 1200, false));
        }
    }


}

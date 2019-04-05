package com.hzstore.mapproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
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

import retrofit2.Call;
import retrofit2.Callback;

public class CartActivity extends AppCompatActivity implements CartItemRecyclerViewAdapter.ItemListener {
    private static final String TAG = "CartActivity";
Cart mycart;
    CartItemFragment cartif;
    private View mProgressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mProgressView = findViewById(R.id.loader_cart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getItems();
    }

    public void gotoCheckout(View v){
        Intent intent = new Intent(this,CheckoutActivity.class);
        startActivity(intent);
    }

    public void getItems(){
        if(HomeActivity.app.isLoggedin()) {
            showProgress(true);
//initialize products call
            final Call<Cart> cart_call;
            ApiService authservice = RetrofitBuilder.createServiceWithAuth(ApiService.class, HomeActivity.app.tokenManager);

            cart_call = authservice.cart();
            cart_call.enqueue(new Callback<Cart>() {
                @Override
                public void onResponse(Call<Cart> call, retrofit2.Response<Cart> response) {
//print response

                    Gson gson = new Gson();
                    Log.d("CA", gson.toJson(response.body()));

                    Log.w(TAG, "onResponse: " + response);

                    //check the validity of the response
                    if (response.isSuccessful()) {
                        mycart = response.body();



                        cartif = CartItemFragment.newInstance(mycart.getCartitem());

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                        ft.add(R.id.cart_list, cartif).commit();
                        int totalItems = 0;
                        for(Cartitem ci : mycart.getCartitem()){
                            totalItems += ci.getCount();
                        }

                        ((TextView)findViewById(R.id.tv_total)).setText(totalItems +" item/s");
                        ((TextView)findViewById(R.id.cart_total)).setText(""+mycart.getTotal() +" SR");
showProgress(false);
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
                public void onFailure(Call<Cart> call, Throwable t) {
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

    /**
     * Re calculate the total items and the total price of the cart after an item is updated
     * @param cartitem
     */
    @Override
    public void onCartitemUpdate(Cartitem cartitem) {

        int total = 0;
        int count = 0;
        for(Cartitem ci : mycart.getCartitem()){
            if(cartitem.getId() == ci.getId()) {
                total += cartitem.getCount()*cartitem.getProd().getPrice();
                count += cartitem.getCount();
            }else{
                total += ci.getCount()*ci.getProd().getPrice();
                count += ci.getCount();
            }
        }
        mycart.setTotal(""+total);


        ((TextView)findViewById(R.id.tv_total)).setText(count +" item/s");
        ((TextView)findViewById(R.id.cart_total)).setText(""+mycart.getTotal() +" SR");
        Toast.makeText(getApplicationContext(),"Item count updated",Toast.LENGTH_SHORT).show();

    }

    public void deleteSelected(){
        if(HomeActivity.app.isLoggedin() ){
            if(cartif.selecteditemIds().size()>0) {
//initialize products call
                final Call<Cart> cart_call;
                ApiService authservice = RetrofitBuilder.createServiceWithAuth(ApiService.class, HomeActivity.app.tokenManager);
                Gson gson = new Gson();
                cart_call = authservice.deleteCartItems(gson.toJson(cartif.selecteditemIds()));
                cart_call.enqueue(new Callback<Cart>() {
                    @Override
                    public void onResponse(Call<Cart> call, retrofit2.Response<Cart> response) {
//print response

                        Gson gson = new Gson();
                        Log.d("CA", gson.toJson(response.body()));

                        Log.w(TAG, "onResponse: " + response);

                        //check the validity of the response
                        if (response.isSuccessful()) {
                            mycart = response.body();


                            int totalItems = 0;
                            for (Cartitem ci : mycart.getCartitem()) {
                                totalItems += ci.getCount();
                            }
                            //update recyclerview

                            cartif.cartadapter.updateData(mycart.getCartitem());

                            ((TextView) findViewById(R.id.tv_total)).setText(totalItems + " item/s");
                            ((TextView) findViewById(R.id.cart_total)).setText("" + mycart.getTotal() + " SR");

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
                    public void onFailure(Call<Cart> call, Throwable t) {
                        Log.w(TAG, "onFailure: " + t.getMessage());

                    }
                });
            }else{
                Toast.makeText(this,"No items selected",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"You are not logged in",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
            //formContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        }
    }
}

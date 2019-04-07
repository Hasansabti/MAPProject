package com.hzstore.mapproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hzstore.mapproject.fragments.OrdersFragment;
import com.hzstore.mapproject.fragments.addressFragment;
import com.hzstore.mapproject.models.Address;
import com.hzstore.mapproject.models.Order;
import com.hzstore.mapproject.net.ApiError;
import com.hzstore.mapproject.net.ApiService;
import com.hzstore.mapproject.net.RetrofitBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";
    private View mProgressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //place fragment based on the action of the user
        mProgressView = findViewById(R.id.loader_account);
        String type = getIntent().getExtras().getString("type");
        if (type.equalsIgnoreCase("orders")){


           requestOrders();
        }else if (type.equalsIgnoreCase("address")){
            requestAddresses();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        String type = getIntent().getExtras().getString("type");
        if (type.equalsIgnoreCase("address")) {
            getMenuInflater().inflate(R.menu.address, menu);
        }
        return true;
    }
    public void requestOrders(){
        if(HomeActivity.app.isLoggedin()) {
            showProgress(true);
//initialize products call
            final Call<List<Order>> cart_call;
            ApiService authservice = RetrofitBuilder.createServiceWithAuth(ApiService.class, HomeActivity.app.tokenManager);

            cart_call = authservice.orders();
            cart_call.enqueue(new Callback<List<Order>>() {
                @Override
                public void onResponse(Call<List<Order>> call, retrofit2.Response<List<Order>> response) {
//print response

                    Gson gson = new Gson();
                    Log.d("CA", gson.toJson(response.body()));

                    Log.w(TAG, "onResponse: " + response);

                    //check the validity of the response
                    if (response.isSuccessful()) {
                        List<Order> myorders = response.body();

                        OrdersFragment pf = OrdersFragment.newInstance(myorders);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.add(R.id.account_content, pf).commit();


                        showProgress(false);
                    } else {
                        if (response.code() == 422) {
                            // handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = com.hzstore.mapproject.Utils.converErrors(response.errorBody());
                            Toast.makeText(AccountActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }

                @Override
                public void onFailure(Call<List<Order>> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());

                }
            });
        }else{
            Toast.makeText(this,"You are not logged in",Toast.LENGTH_SHORT).show();
        }
    }


    private void requestAddresses() {
        if(HomeActivity.app.isLoggedin()) {
            showProgress(true);
//initialize products call
            final Call<List<Address>> cart_call;
            ApiService authservice = RetrofitBuilder.createServiceWithAuth(ApiService.class, HomeActivity.app.tokenManager);

            cart_call = authservice.addresses();
            cart_call.enqueue(new Callback<List<Address>>() {
                @Override
                public void onResponse(Call<List<Address>> call, retrofit2.Response<List<Address>> response) {
//print response

                    Gson gson = new Gson();
                    Log.d("CA", gson.toJson(response.body()));

                    Log.w(TAG, "onResponse: " + response);

                    //check the validity of the response
                    if (response.isSuccessful()) {
                        List<Address> myaddress = response.body();

                        addressFragment af = addressFragment.newInstance(myaddress);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.add(R.id.account_content, af).commit();


                        showProgress(false);
                    } else {
                        if (response.code() == 422) {
                            // handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = com.hzstore.mapproject.Utils.converErrors(response.errorBody());
                            Toast.makeText(AccountActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }

                @Override
                public void onFailure(Call<List<Address>> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());

                }
            });
        }else{
            Toast.makeText(this,"You are not logged in",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cartitem_remove) {

            return true;
        }else   if (id==android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
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

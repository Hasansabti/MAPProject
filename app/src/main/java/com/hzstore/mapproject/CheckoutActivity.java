package com.hzstore.mapproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hzstore.mapproject.adapters.MyaddressAdapter;
import com.hzstore.mapproject.adapters.SelectaddressAdapter;
import com.hzstore.mapproject.fragments.addressFragment;
import com.hzstore.mapproject.models.Address;
import com.hzstore.mapproject.net.ApiError;
import com.hzstore.mapproject.net.ApiService;
import com.hzstore.mapproject.net.RetrofitBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class CheckoutActivity extends AppCompatActivity implements SelectaddressAdapter.ItemListener {
    private View mProgressView;
    TokenManager tokenManager;
    RecyclerView addresslist;
    SelectaddressAdapter adapter;
    CheckoutActivity activity;
    Address selected_address;
    TextView addAddress;
    private static final String TAG = "CheckoutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        setTitle("Select Address");
        activity = this;
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
addAddress = findViewById(R.id.add_address);
        mProgressView = findViewById(R.id.loader_checkout);
        addresslist = findViewById(R.id.list_selectaddress);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  requestAddresses();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
 if (id==android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    public void placeOrder(View v) {

        if (HomeActivity.app.isLoggedin(tokenManager)) {
            if (selected_address == null) {

                Toast.makeText(CheckoutActivity.this, "Please select an address where your item will be delivered", Toast.LENGTH_LONG).show();

                return;
            }
            showProgress(true);
//initialize products call
            final Call<String> cart_call;
            ApiService authservice = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

            cart_call = authservice.placeorder(selected_address.getID());
            cart_call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
//print response

                    Gson gson = new Gson();
                    Log.d("CA", gson.toJson(response.body()));

                    Log.w(TAG, "onResponse: " + response);

                    //check the validity of the response
                    if (response.isSuccessful()) {
                        String msg = response.body();

                        //Toast.makeText(CheckoutActivity.this, "", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(activity, ThankyouActivity.class);
                        intent.putExtra("msg", msg);
activity.startActivity(intent);
                        finish();

                        showProgress(false);
                    } else {
                        if (response.code() == 422) {
                            // handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = com.hzstore.mapproject.Utils.converErrors(response.errorBody());
                            Toast.makeText(CheckoutActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());
                    showProgress(false);
                    Toast.makeText(CheckoutActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();


                }
            });
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    private void requestAddresses() {
        if (HomeActivity.isLoggedin(tokenManager)) {
            showProgress(true);
//initialize products call
            final Call<List<Address>> cart_call;
            ApiService authservice = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

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

                        if(myaddress.isEmpty()){
                            addAddress.setVisibility(View.VISIBLE);
                            addAddress.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getApplicationContext(),AddAddressActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }else {
                            addAddress.setVisibility(View.GONE);
                            adapter = new SelectaddressAdapter(myaddress, activity);
                            addresslist.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                                    DividerItemDecoration.VERTICAL));
                            // if (adapter.getItemCount() <= 1) {
                            addresslist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            // } else {
                            //    addresslist.setLayoutManager(new GridLayoutManager(addresslist.getContext(), adapter.getItemCount()));
                            // }
                            addresslist.setAdapter(adapter);
                        }

                        showProgress(false);
                    } else {
                        if (response.code() == 422) {
                            // handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = com.hzstore.mapproject.Utils.converErrors(response.errorBody());
                            Toast.makeText(CheckoutActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }

                @Override
                public void onFailure(Call<List<Address>> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());

                }
            });
        } else {
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        requestAddresses();
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

    @Override
    public void onAddressSelected(Address address) {
        this.selected_address = address;
    }
}

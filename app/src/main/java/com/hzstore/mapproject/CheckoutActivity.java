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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hzstore.mapproject.fragments.CartItemFragment;
import com.hzstore.mapproject.models.Address;
import com.hzstore.mapproject.models.Cart;
import com.hzstore.mapproject.models.Cartitem;
import com.hzstore.mapproject.net.ApiError;
import com.hzstore.mapproject.net.ApiService;
import com.hzstore.mapproject.net.RetrofitBuilder;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "AddressActivity";
    private View mProgressView;
    TokenManager tokenManager;

    TextView  fname;

    TextView lname;

    TextView address1;

    TextView address2;

    TextView postcode;

    TextView phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        setContentView(R.layout.activity_checkout);
       fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        address1 = findViewById(R.id.address1);
        address2 = findViewById(R.id.address2);
        postcode = findViewById(R.id.postcode);
        phone = findViewById(R.id.phone);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressView = findViewById(R.id.loader_address);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }

        return true;
    }

    public void placeOrder(View v){

        if(HomeActivity.app.isLoggedin(tokenManager)) {
            showProgress(true);
//initialize products call
            final Call<Address> cart_call;
            ApiService authservice = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

            cart_call = authservice.add_addresses(fname.getText().toString(),lname.getText().toString(),address1.getText().toString(),address2.getText().toString(),postcode.getText().toString(),phone.getText().toString());
            cart_call.enqueue(new Callback<Address>() {
                @Override
                public void onResponse(Call<Address> call, retrofit2.Response<Address> response) {
//print response

                    Gson gson = new Gson();
                    Log.d("CA", gson.toJson(response.body()));

                    Log.w(TAG, "onResponse: " + response);

                    //check the validity of the response
                    if (response.isSuccessful()) {
                        Address address = response.body();
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
                public void onFailure(Call<Address> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());
                    showProgress(false);
                    Toast.makeText(CheckoutActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();


                }
            });
        }else{
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
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

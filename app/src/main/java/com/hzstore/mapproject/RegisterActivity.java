package com.hzstore.mapproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.hzstore.mapproject.models.AccessToken;
import com.hzstore.mapproject.models.User;
import com.hzstore.mapproject.net.ApiError;
import com.hzstore.mapproject.net.ApiService;
import com.hzstore.mapproject.net.RetrofitBuilder;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
TextView name, email,password, reg_btn, goto_login;
    private static final String TAG = "RegisterActivity";
    AwesomeValidation validator;
    private View mProgressView;
    TokenManager tokenManager;
    LinearLayout formContainer;
    TextInputLayout tilEmail;
    TextInputLayout tilPassword;
    TextInputLayout tilname;
    AccountManager accountManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.reg_name);
        email = findViewById(R.id.reg_email);
        tilname = findViewById(R.id.reg_til_name);
        tilEmail = findViewById(R.id.reg_til_email);
        tilPassword = findViewById(R.id.reg_til_password);
        password = findViewById(R.id.reg_password);
        formContainer = findViewById(R.id.regform_container);
        goto_login = findViewById(R.id.go_to_login);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        accountManager = AccountManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        mProgressView = findViewById(R.id.loader_reg);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        goto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });
        reg_btn = findViewById(R.id.btn_register);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               attemptRegister();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    public void performReister(){

    }


    /**
     * Attempts to register the account specified by the register form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {


        // Reset errors.
        name.setError(null);
        email.setError(null);
        password.setError(null);

        // Store values at the time of the login attempt.
        String rname = name.getText().toString();
        String remail = email.getText().toString();
        String rpassword = password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(rname)){
            name.setError("Username cannot be empty");
            focusView = name;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(rpassword) && !isPasswordValid(rpassword)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(remail)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(remail)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            //  mAuthTask = new UserLoginTask(email, password);
            //   mAuthTask.execute((Void) null);


            validator.clear();

            //validate user details on the network side
            if (validator.validate()) {
                // showProgress(true);
                Call<AccessToken> call = RetrofitBuilder.createService(ApiService.class).register(rname,remail,rpassword);

                call.enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                        Log.w(TAG, "onResponse: " + response);

                        if (response.isSuccessful()) {

                            tokenManager.saveToken(response.body());

                            getUserdata();


                        } else {
                            if (response.code() == 422) {
                                handleErrors(response.errorBody());
                            }
                            if (response.code() == 401) {
                                ApiError apiError = Utils.converErrors(response.errorBody());
                                Toast.makeText(RegisterActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            showProgress(false);
                        }

                    }

                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {
                        Log.w(TAG, "onFailure: " + t.getMessage());
                        showProgress(false);
                    }
                });

            }


        }
    }

    private void getUserdata() {

        Call<User> usercall;
        ApiService authservice = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        usercall = authservice.userdata();
        usercall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {

                    showProgress(false);

                    accountManager.saveUserdata(response.body());
                    // startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();

                    HomeActivity.app.performLogin();



                } else {
                    if (response.code() == 422) {
                        handleErrors(response.errorBody());
                    }
                    if (response.code() == 401) {
                        ApiError apiError = Utils.converErrors(response.errorBody());
                        Toast.makeText(RegisterActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    showForm();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
                showForm();
            }
        });

    }
    private void showForm() {
        //  TransitionManager.beginDelayedTransition(container);
        //      formContainer.setVisibility(View.VISIBLE);
        mProgressView.setVisibility(View.GONE);
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
            formContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        }
    }
    private void handleErrors(ResponseBody response) {

        ApiError apiError = Utils.converErrors(response);

        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
            if (error.getKey().equals("username")) {
                tilname.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("email")) {
                tilEmail.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("password")) {
                tilPassword.setError(error.getValue().get(0));
            }
        }

    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}

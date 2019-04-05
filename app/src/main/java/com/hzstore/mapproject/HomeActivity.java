package com.hzstore.mapproject;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hzstore.mapproject.adapters.ProductRecyclerViewAdapter;
import com.hzstore.mapproject.fragments.ProductsFragment;
import com.hzstore.mapproject.models.Product;
import com.hzstore.mapproject.net.ApiError;
import com.hzstore.mapproject.net.ApiService;
import com.hzstore.mapproject.net.RetrofitBuilder;
import com.hzstore.mapproject.net.requests.ValueResponse;
import com.hzstore.mapproject.settings.SettingsActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ProductRecyclerViewAdapter.ProductListener {
    private static final String TAG = "HomeActivity";
    public static HomeActivity app;
    public TokenManager tokenManager;
    public  AccountManager accountManager;
    TextView loginbtn;
    TextView cartcounttv;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        app = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        accountManager = AccountManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loginbtn = navigationView.getHeaderView(0).findViewById(R.id.accountname);


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Login toggle button
                if (isLoggedin()) {
                    performLogout();

                } else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivityForResult(intent, 0);
                }

            }
        });

        //get products data from the server
        requestProducts();


        //Start with the user logged out(for demo only)
      //  performLogout();

        //check if user is logged in
        if (tokenManager.getToken().getAccessToken() != null) {
            loginbtn.setText("Welcome, "+accountManager.getUserdata().getName());


            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_home3_drawer_loggedin);

            updateCCount();
        }

    }

    //return to previous activity
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_orders) {
            //show User's orders
            Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
            intent.putExtra("type", "orders");
            startActivity(intent);


        }else if(id == R.id.nav_address){
            Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
            intent.putExtra("type", "address");
            startActivity(intent);
        }

        else if (id == R.id.nav_wishlist) {
            //show User's wishlist
            Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
            intent.putExtra("type", "wish");
            startActivity(intent);

        } else if (id == R.id.nav_manage) {
            //open settings
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_share) {


        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void requestProducts() {


//initialize products HZ-api call
        Call<List<Product>> product_call;
        ApiService service = RetrofitBuilder.createService(ApiService.class);
        product_call = service.products();
        product_call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, retrofit2.Response<List<Product>> response) {
//print response
                Log.w(TAG, "onResponse: " + response);

                //check the validity of the response
                if (response.isSuccessful()) {

                  //  showProgress(false);

                    //show products list fragment once products are available and pass the products
                    ProductsFragment pf = ProductsFragment.newInstance(response.body());

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                    ft.add(R.id.main3, pf).commitAllowingStateLoss();


                } else {
                    if (response.code() == 422) {
                       // handleErrors(response.errorBody());
                    }
                    if (response.code() == 401) {
                        ApiError apiError = Utils.converErrors(response.errorBody());
                        Toast.makeText(HomeActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());

            }
        });



    }

    @Override
    protected void onPause() {
        super.onPause();



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isLoggedin())
            updateCCount();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if(isLoggedin())
            updateCCount();

    }

    public void updateCCount(){

    //refresh cart count


    final Call<ValueResponse> cart_count;
    ApiService service = RetrofitBuilder.createServiceWithAuth(ApiService.class,tokenManager);
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
                    Toast.makeText(HomeActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

        }

        @Override
        public void onFailure(Call<ValueResponse> call, Throwable t) {
            Log.w(TAG, "onFailure: " + t.getMessage());

        }
    });
}
    public void performLogin() {
        loginbtn.setText("Welcome, "+accountManager.getUserdata().getName());


        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.activity_home3_drawer_loggedin);

        Toast.makeText(getApplicationContext(),"Logged in successfully", Toast.LENGTH_SHORT).show();
    }

    public void performLogout() {

        loginbtn.setText("Welcome, Login here");

        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.activity_home3_drawer_visitor);
        tokenManager.deleteToken();
        Log.d("Home","Logging out");
    }

    public boolean isLoggedin() {

        return tokenManager.getToken().getAccessToken() != null;


    }

    @Override
    public void onCartUpdate() {
        updateCCount();
    }
}


package com.hzstore.mapproject.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hzstore.mapproject.CartActivity;
import com.hzstore.mapproject.HomeActivity;
import com.hzstore.mapproject.LoginActivity;
import com.hzstore.mapproject.ProductActivity;
import com.hzstore.mapproject.R;
import com.hzstore.mapproject.fragments.ProductsFragment.OnListFragmentInteractionListener;

import com.hzstore.mapproject.models.Cartitem;
import com.hzstore.mapproject.models.Product;
import com.hzstore.mapproject.net.ApiError;
import com.hzstore.mapproject.net.ApiService;
import com.hzstore.mapproject.net.RetrofitBuilder;
import com.hzstore.mapproject.net.requests.AddtocartResponse;
import com.squareup.picasso.Picasso;


import java.io.InputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Product} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder> {

    private final List<Product> mValues;
    private final OnListFragmentInteractionListener mListener;
    private ProductListener itemListener;

    public void setItemListener(ProductListener listener) {
        this.itemListener = listener;
    }

    public ProductRecyclerViewAdapter(List<Product> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getName());
        holder.mContentView.setText("" + mValues.get(position).getPrice() + " SR");
holder.rating.setRating(holder.mItem.getAvg_reviews());
if(holder.mItem.getReviews() != null) {
    holder.rev_count.setText("(" + holder.mItem.getReviews().size() + ")");
}

        Picasso.get().load("http://sabti-h.tech/hz-store/storage/img/" + mValues.get(position).getImage()).into(holder.imageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
                Intent intent = new Intent(HomeActivity.app, ProductActivity.class);
                intent.putExtra("productID", "" + mValues.get(position).getId());
                Log.d("HZStore", "Clicked product : " + mValues.get(position).getId());
                intent.putExtra("title", mValues.get(position).getName());
                HomeActivity.app.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        if (mValues != null)
            return mValues.size();
        else {
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView imageView, paddtocart;
        public final View cProgress;
        public Product mItem;
        public final RatingBar rating;
        public final TextView rev_count;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            imageView = view.findViewById(R.id.productImage);
            paddtocart = view.findViewById(R.id.p_addtocart);
            cProgress = view.findViewById(R.id.loader_pcart);
            rev_count = view.findViewById(R.id.rev_count);
            paddtocart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addtoCart();
                }
            });
            rating = view.findViewById(R.id.prodlist_rating);

        }

        /**
         * Add this item to cart
         */
        public void addtoCart() {
            cProgress.setVisibility(View.VISIBLE);
            paddtocart.setVisibility(View.INVISIBLE);
            if (HomeActivity.app.isLoggedin()) {
            //initialize Add to cart call
                final Call<AddtocartResponse> addcart_call;
                ApiService authservice = RetrofitBuilder.createServiceWithAuth(ApiService.class, HomeActivity.app.tokenManager);

                addcart_call = authservice.addtocart(mItem.getId(), 1);
                addcart_call.enqueue(new Callback<AddtocartResponse>() {
                    @Override
                    public void onResponse(Call<AddtocartResponse> call, retrofit2.Response<AddtocartResponse> response) {
                        cProgress.setVisibility(View.INVISIBLE);
                        paddtocart.setVisibility(View.VISIBLE);
                    //print response
                        Log.w("PI", "onResponse: " + response);

                        //check the validity of the response
                        if (response.isSuccessful()) {
                            Toast.makeText(HomeActivity.app.getApplicationContext(), "Item has been added to your cart", Toast.LENGTH_SHORT).show();

                            if (itemListener != null) {
                                itemListener.onCartUpdate();
                            }


                        } else {
                            if (response.code() == 422) {
                                // handleErrors(response.errorBody());
                            }
                            if (response.code() == 401) {
                                ApiError apiError = com.hzstore.mapproject.Utils.converErrors(response.errorBody());
                                Toast.makeText(HomeActivity.app, apiError.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<AddtocartResponse> call, Throwable t) {
                        cProgress.setVisibility(View.INVISIBLE);
                        paddtocart.setVisibility(View.VISIBLE);
                        Log.w("PI", "onFailure: " + t.getMessage());

                    }
                });
            } else {
                //show login activity
                Toast.makeText(HomeActivity.app, "You are not logged in", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.app, LoginActivity.class);
                HomeActivity.app.startActivityForResult(intent, 0);
            }
        }


        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public interface ProductListener {
        void onCartUpdate();
    }

}

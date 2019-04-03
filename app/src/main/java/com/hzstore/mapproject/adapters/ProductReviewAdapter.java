package com.hzstore.mapproject.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.media.Rating;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hzstore.mapproject.HomeActivity;
import com.hzstore.mapproject.R;
import com.hzstore.mapproject.Utils;
import com.hzstore.mapproject.fragments.CartItemFragment.OnListFragmentInteractionListener;

import com.hzstore.mapproject.models.Review;
import com.hzstore.mapproject.net.ApiError;
import com.hzstore.mapproject.net.ApiService;
import com.hzstore.mapproject.net.RetrofitBuilder;
import com.hzstore.mapproject.net.requests.CartitemResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Review} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ProductReviewAdapter extends RecyclerView.Adapter<ProductReviewAdapter.ViewHolder> {

    public final List<Review> mValues;

    private ItemListener itemListener;

    public void setItemListener(ItemListener listener) {
        this.itemListener = listener;
    }
    public void updateData(List<Review> viewModels) {
        mValues.clear();
        mValues.addAll(viewModels);
        notifyDataSetChanged();
    }


    public interface ItemListener {
       // void onCartitemUpdate(Review cartitem);
    }

    public ProductReviewAdapter(List<Review> items) {
        mValues = items;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
      //  holder.mIdView.setText(mValues.get(position).getName());
        //holder.mContentView.setText(mValues.get(position).getPrice());



holder.rev_name.setText("User "+holder.mItem.getUser());
holder.rev_text.setText(holder.mItem.getReview());
holder.rev_rb.setRating(holder.mItem.getRate());
    }




    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Review mItem;
        ImageView rev_image;
        TextView rev_name, rev_text;
        RatingBar rev_rb;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            rev_image = view.findViewById(R.id.rev_img);
            rev_name = view.findViewById(R.id.rev_name);
            rev_text = view.findViewById(R.id.rev_text);
            rev_rb = view.findViewById(R.id.rev_rating);


        }



        @Override
        public String toString() {
            return super.toString() + " '" + mItem.getReview() + "'";
        }
    }
}

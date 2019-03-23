package com.hzstore.mapproject.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzstore.mapproject.HomeActivity;
import com.hzstore.mapproject.ProductActivity;
import com.hzstore.mapproject.R;
import com.hzstore.mapproject.fragments.ProductsFragment.OnListFragmentInteractionListener;

import com.hzstore.mapproject.models.Product;


import java.io.InputStream;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Product} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder> {

    private final List<Product> mValues;
    private final OnListFragmentInteractionListener mListener;

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
        holder.mContentView.setText(""+mValues.get(position).getPrice() +" SR");


            new DownloadImageTask(holder.imageView).execute("http://sabti-h.tech/hz-store/storage/img/"+mValues.get(position).getImage());




        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
                Intent intent = new Intent(HomeActivity.app,ProductActivity.class);
intent.putExtra("productID",""+mValues.get(position).getId());
Log.d("HZStore","Clicked product : " + mValues.get(position).getId());
intent.putExtra("title",mValues.get(position).getName());
                HomeActivity.app.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        if(mValues != null)
        return mValues.size();
        else{
            return 0;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            bmImage.setImageBitmap(Bitmap.createScaledBitmap(result,200,200,false));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView imageView;
        public Product mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            imageView = view.findViewById(R.id.productImage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}

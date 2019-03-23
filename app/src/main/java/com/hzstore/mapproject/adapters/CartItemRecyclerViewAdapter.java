package com.hzstore.mapproject.adapters;

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

import com.hzstore.mapproject.ProductActivity;
import com.hzstore.mapproject.R;
import com.hzstore.mapproject.fragments.CartItemFragment.OnListFragmentInteractionListener;

import com.hzstore.mapproject.models.Cartitem;


import java.io.InputStream;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Cartitem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class CartItemRecyclerViewAdapter extends RecyclerView.Adapter<CartItemRecyclerViewAdapter.ViewHolder> {

    private final List<Cartitem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public CartItemRecyclerViewAdapter(List<Cartitem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_cartitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
      //  holder.mIdView.setText(mValues.get(position).getName());
        //holder.mContentView.setText(mValues.get(position).getPrice());

if(mValues.get(position).getProd() != null) {
    holder.itemname.setText(mValues.get(position).getProd().getName());

    holder.itemprice.setText(mValues.get(position).getProd().getPrice() + " SR");

    // holder.itemsize.setText(call.getSize());
    holder.tv_quantity.setText(" "+mValues.get(position).getCount());
    new DownloadImageTask(holder.item_img).execute(mValues.get(position).getProd().getImage());
}
     //   holder.cart_minus_img.setOnClickListener(new QuantityListener(context, holder.tv_quantity,call,false));
     //   holder.cart_plus_img.setOnClickListener(new QuantityListener(context, holder.tv_quantity,call,true));
      //  holder.img_deleteitem.setOnClickListener(new DeleteItemListener(context,call,this));


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private TextView itemprice,itemname, itemsize,tv_quantity;
        ImageView cart_minus_img, cart_plus_img,img_deleteitem, item_img;
        public Cartitem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;



            cart_minus_img=(ImageView) view.findViewById(R.id.minus_item);
            cart_plus_img=(ImageView) view.findViewById(R.id.plus_item);
            item_img=(ImageView) view.findViewById(R.id.cartiem_img);
          //  img_deleteitem=(ImageView) itemView.findViewById(R.id.img_deleteitem);
            itemname=(TextView) view.findViewById(R.id.crtname_name);
            itemprice=(TextView) view.findViewById(R.id.crtitem_price);
          //  itemsize=(TextView) itemView.findViewById(R.id.itemsize);
            tv_quantity=(TextView) view.findViewById(R.id.crt_count);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + itemname.getText() + "'";
        }
    }
}

package com.hzstore.mapproject.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hzstore.mapproject.HomeActivity;
import com.hzstore.mapproject.R;
import com.hzstore.mapproject.Utils;
import com.hzstore.mapproject.fragments.CartItemFragment.OnListFragmentInteractionListener;

import com.hzstore.mapproject.models.Cartitem;
import com.hzstore.mapproject.net.ApiError;
import com.hzstore.mapproject.net.ApiService;
import com.hzstore.mapproject.net.RetrofitBuilder;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Cartitem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class CartItemRecyclerViewAdapter extends RecyclerView.Adapter<CartItemRecyclerViewAdapter.ViewHolder> {

    public final List<Cartitem> mValues;

    private ItemListener itemListener;
List<Integer> selected = new ArrayList<>();

    public void setItemListener(ItemListener listener) {
        this.itemListener = listener;
    }
    public void updateData(List<Cartitem> viewModels) {
        mValues.clear();
        mValues.addAll(viewModels);
        notifyDataSetChanged();
    }
    public List<Integer> getselecteditems() {
        Gson gs = new Gson();
        Log.d("CA",gs.toJson(selected));
return selected;
    }

    public interface ItemListener {
        void onCartitemUpdate(Cartitem cartitem);
    }

    public CartItemRecyclerViewAdapter(List<Cartitem> items) {
        mValues = items;

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
    if(!selected.contains(holder.mItem.getId())) {
        ((CheckBox) holder.checkBox).setChecked(false);
    }
//Download image into Imageview
    Picasso.get().load("http://sabti-h.tech/hz-store/storage/img/" + mValues.get(position).getProd().getImage()).into(holder.item_img);

}

       holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CA","Adding " + holder.mItem.getId());
                if (((CheckBox) v).isChecked()) {

selected.add(holder.mItem.getId());
                }else{
if(selected.contains(holder.mItem.getId())){
    selected.remove(Integer.valueOf( holder.mItem.getId()));
}
                }
            }
        });


    }




    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private TextView itemprice,itemname, itemsize,tv_quantity;
        ImageView cart_minus_img, cart_plus_img,img_deleteitem, item_img;
        ProgressBar mProgressView;
        CheckBox checkBox;
        public Cartitem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;



            cart_minus_img=(ImageView) view.findViewById(R.id.minus_item);
            cart_plus_img=(ImageView) view.findViewById(R.id.plus_item);
            item_img=(ImageView) view.findViewById(R.id.oiem_img);
          //  img_deleteitem=(ImageView) itemView.findViewById(R.id.img_deleteitem);
            itemname=(TextView) view.findViewById(R.id.oitem_name);
            itemprice=(TextView) view.findViewById(R.id.crtitem_price);
          //  itemsize=(TextView) itemView.findViewById(R.id.itemsize);
            tv_quantity=(TextView) view.findViewById(R.id.oitem_count);
            mProgressView = view.findViewById(R.id.loader_ca);
            checkBox = view.findViewById(R.id.chk_selectitem);


            cart_minus_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItem.getCount() >1) {

                        updateCount(mItem.getId(),mItem.getCount()-1);
                    }

                }
            });

            cart_plus_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                        updateCount(mItem.getId(),mItem.getCount()+1);


                }
            });



        }


        public void updateCount(int itemid,int value){
showProgress(true);
            Call<Cartitem> usercall;
            ApiService authservice = RetrofitBuilder.createServiceWithAuth(ApiService.class, HomeActivity.app.tokenManager);
            usercall = authservice.updatecount(itemid,value);
            usercall.enqueue(new Callback<Cartitem>() {
                @Override
                public void onResponse(Call<Cartitem> call, Response<Cartitem> response) {

                    Log.w("CA", "onResponse: " + response);

                    if (response.isSuccessful()) {

                        showProgress(false);
mItem = response.body();

                        tv_quantity.setText("" + mItem.getCount());
                        if(itemListener != null){
                            itemListener.onCartitemUpdate(mItem);
                        }


                      //  accountManager.saveUserdata(response.body().getData());
                        // startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                      //  finish();

                     //   HomeActivity.app.performLogin();



                    } else {
                        if (response.code() == 422) {
                          //  handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = Utils.converErrors(response.errorBody());
                            Toast.makeText(HomeActivity.app.getApplicationContext(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                       // showForm();
                    }

                }

                @Override
                public void onFailure(Call<Cartitem> call, Throwable t) {
                    Log.w("CA", "onFailure: " + t.getMessage());
                   // showForm();
                }
            });

        }
        private void showProgress(final boolean show) {
            // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
            // for very easy animations. If available, use these APIs to fade-in
            // the progress spinner.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = HomeActivity.app. getResources().getInteger(android.R.integer.config_shortAnimTime);

tv_quantity.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                mProgressView.animate().setDuration(shortAnimTime).alpha(
                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });

            } else {
                // The ViewPropertyAnimator APIs are not available, so simply show
                // and hide the relevant UI components.
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

            }
        }
        @Override
        public String toString() {
            return super.toString() + " '" + itemname.getText() + "'";
        }
    }
}

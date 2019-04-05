package com.hzstore.mapproject.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hzstore.mapproject.HomeActivity;
import com.hzstore.mapproject.R;
import com.hzstore.mapproject.fragments.CartItemFragment.OnListFragmentInteractionListener;
import com.hzstore.mapproject.models.Order;
import com.hzstore.mapproject.models.Orderitem;
import com.hzstore.mapproject.models.Review;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Review} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class UserOrdersAdapter extends RecyclerView.Adapter<UserOrdersAdapter.ViewHolder> {

    public final List<Order> mValues;

    private ItemListener itemListener;

    public void setItemListener(ItemListener listener) {
        this.itemListener = listener;
    }
    public void updateData(List<Order> viewModels) {
        mValues.clear();
        mValues.addAll(viewModels);
        notifyDataSetChanged();
    }


    public interface ItemListener {
       // void onCartitemUpdate(Review cartitem);
    }

    public UserOrdersAdapter(List<Order> items) {
        mValues = items;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
holder.orderid.setText(""+holder.mItem.getId());
        holder.order_time.setText(holder.mItem.getOrderdate());
        holder.tracking.setText(holder.mItem.getTracking());

        holder.track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });


        RecyclerView recyclerView = (RecyclerView) holder.items;

        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.app, LinearLayoutManager.VERTICAL, false));

        UserOrderItemsAdapter orderitemssadapter = new UserOrderItemsAdapter(holder.mItem.getItems());


        //  ordersadapter.setItemListener((AccountActivity) getActivity());
        recyclerView.setAdapter(orderitemssadapter);




    }




    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Order mItem;
        ImageView order_image;
        TextView orderid, order_time, tracking;
       Button track;
        RatingBar rev_rb;
       RecyclerView items;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            orderid = view.findViewById(R.id.orderid);
order_time = view.findViewById(R.id.order_date);
tracking = view.findViewById(R.id.order_track);
track = view.findViewById(R.id.track_btn);
        items = view.findViewById(R.id.orderitems);



        }



        @Override
        public String toString() {
            return super.toString() + " '" + mItem.toString() + "'";
        }
    }
}

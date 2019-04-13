package com.hzstore.mapproject.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hzstore.mapproject.R;
import com.hzstore.mapproject.fragments.CartItemFragment.OnListFragmentInteractionListener;
import com.hzstore.mapproject.models.Orderitem;
import com.hzstore.mapproject.models.Review;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Review} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class UserOrderItemsAdapter extends RecyclerView.Adapter<UserOrderItemsAdapter.ViewHolder> {

    public final List<Orderitem> mValues;

    private ItemListener itemListener;

    public void setItemListener(ItemListener listener) {
        this.itemListener = listener;
    }
    public void updateData(List<Orderitem> viewModels) {
        mValues.clear();
        mValues.addAll(viewModels);
        notifyDataSetChanged();
    }


    public interface ItemListener {
       // void onAddressSelected(Review cartitem);
    }

    public UserOrderItemsAdapter(List<Orderitem> items) {
        mValues = items;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orderitem_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
holder.order_price.setText( ""+holder.mItem.getProduct().getPrice());
holder.order_name.setText(holder.mItem.getProduct().getName());
holder.orderitem_qty.setText(""+holder.mItem.getQty());
        Picasso.get().load("http://sabti-h.tech/hz-store/storage/img/" + holder.mItem.getProduct().getImage()).into(holder.order_image);




    }




    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Orderitem mItem;
        ImageView order_image;
        TextView order_name, order_price, orderitem_qty;


        public ViewHolder(View view) {
            super(view);
            mView = view;

order_image = view.findViewById(R.id.oiem_img);
order_name = view.findViewById(R.id.oitem_name);
order_price = view.findViewById(R.id.oitem_price);
orderitem_qty = view.findViewById(R.id.oitem_count);



        }



        @Override
        public String toString() {
            return super.toString() + " '" + mItem.toString() + "'";
        }
    }
}

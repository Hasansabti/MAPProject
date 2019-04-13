package com.hzstore.mapproject.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hzstore.mapproject.R;
import com.hzstore.mapproject.fragments.addressFragment.OnListFragmentInteractionListener;
import com.hzstore.mapproject.models.Address;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Address} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SelectaddressAdapter extends RecyclerView.Adapter<SelectaddressAdapter.ViewHolder> {

    private final List<Address> mValues;
    private final ItemListener mListener;

    public int lastSelectedPosition = 0;
    public SelectaddressAdapter(List<Address> items, ItemListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selectaddress_item, parent, false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
if(mValues.isEmpty()){


}else {

    holder.mItem = mValues.get(position);
    if(position == lastSelectedPosition){
        mListener.onAddressSelected(holder.mItem);
    }
    holder.mIdView.setText(mValues.get(position).getFname() + " " + holder.mItem.getLname());
    holder.mContentView.setText(mValues.get(position).getAddress1());
    holder.mPhoneView.setText(holder.mItem.getPhone());

    holder.select.setChecked(lastSelectedPosition == position);
    holder.mView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onAddressSelected(holder.mItem);

            }

            holder.select.performClick();
        }
    });
}
    }
    public interface ItemListener {
        void onAddressSelected(Address address);
    }
    @Override
    public int getItemCount() {
        if(mValues != null)
        return mValues.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mPhoneView;
        public  final RadioButton select;
        public Address mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.select_aname);
            mContentView = (TextView) view.findViewById(R.id.select_aaddress);
       mPhoneView = view.findViewById(R.id.select_phone_num);
       select = view.findViewById(R.id.radioButton);
       select.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               lastSelectedPosition = getAdapterPosition();
notifyDataSetChanged();

           }
       });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public class EmptyHolder extends RecyclerView.ViewHolder{
        public final View add_address;
        public EmptyHolder(@NonNull View itemView) {
            super(itemView);
            add_address = itemView.findViewById(R.id.add_address);
        }
    }
}

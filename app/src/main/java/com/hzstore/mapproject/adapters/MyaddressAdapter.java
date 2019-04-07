package com.hzstore.mapproject.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class MyaddressAdapter extends RecyclerView.Adapter<MyaddressAdapter.ViewHolder> {

    private final List<Address> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyaddressAdapter(List<Address> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getFname() + " " +holder.mItem.getLname());
        holder.mContentView.setText(mValues.get(position).getAddress1());
holder.mPhoneView.setText(holder.mItem.getPhone());
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
        if(mValues != null)
        return mValues.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mPhoneView;
        public Address mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.addressname);
            mContentView = (TextView) view.findViewById(R.id.address_content);
       mPhoneView = view.findViewById(R.id.phone_num);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}

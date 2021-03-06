package com.sample.tourguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import com.sample.tourguide.model.NavDrawerItem;
import com.sample.tourguide.R;

import java.util.Collections;
import java.util.List;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by keyur on 07-08-2015.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> mData = Collections.emptyList ();

    private LayoutInflater inflater;
    private Context mContext;
/*
Constructor for our class
 */
    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.mData = data;
    }
/*
Delete function for removing fragments if any
 */
    public void delete(int position) {
        mData.remove (position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem current = mData.get(position);
        holder.title.setText(current.getTitle());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    /*
    Inner class for ViewHolder
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}

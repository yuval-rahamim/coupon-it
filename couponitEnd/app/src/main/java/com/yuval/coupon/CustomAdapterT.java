package com.yuval.coupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapterT extends BaseAdapter {

    Context context;
    ArrayList<Coupon> coupons;

    LayoutInflater inflter;

    public CustomAdapterT(Context applicationContext, ArrayList<Coupon> coupons) {
        this.context = applicationContext;
        this.coupons = coupons;
        inflter = (LayoutInflater.from(applicationContext));
    }

    public void clearData() { coupons.clear();}

    @Override
    public int getCount() {
        return coupons.size();
    }


    @Override
    public Object getItem(int i) {
        return coupons.get(i);
    }

    @Override
    public long getItemId(int i) {
        return coupons.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.activity_list_view, null);
        TextView couponname = (TextView) view.findViewById(R.id.textView);
        TextView couponExpiryDate = (TextView) view.findViewById(R.id.couponExpiryDate);
        couponname.setText(coupons.get(i).couponname);
        couponExpiryDate.setText(coupons.get(i).ExpiryDate);
        return view;
    }


}
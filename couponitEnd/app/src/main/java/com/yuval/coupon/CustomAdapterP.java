package com.yuval.coupon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapterP extends BaseAdapter {

    Context context;
    ArrayList<CouponPhoto> couponsP;
    LayoutInflater inflter;

    public CustomAdapterP(Context applicationContext, ArrayList<CouponPhoto> couponsP) {
        this.context = applicationContext;
        this.couponsP = couponsP;
        inflter = (LayoutInflater.from(applicationContext));
    }

    public void clearData() { couponsP.clear();}

    @Override
    public int getCount() {
        return couponsP.size();
    }


    @Override
    public Object getItem(int i) {
        return couponsP.get(i);
    }

    @Override
    public long getItemId(int i) {
        return couponsP.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.photo_view, null);
            TextView couponname = (TextView) view.findViewById(R.id.textView2);
            ImageView imageView= (ImageView) view.findViewById(R.id.icon);
            TextView couponExpiryDate = (TextView) view.findViewById(R.id.couponExpiryDate);

            byte[] imageBytes = Base64.decode(couponsP.get(i).imagestring, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            couponExpiryDate.setText(couponsP.get(i).ExpiryDate);
            couponname.setText(couponsP.get(i).couponname);
            imageView.setImageBitmap(decodedImage);

            return view;
    }
}

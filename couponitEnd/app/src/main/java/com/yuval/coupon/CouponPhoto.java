package com.yuval.coupon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class CouponPhoto extends Coupon implements Serializable {
    int id;
    static int allid = 1;
    String imagestring,doucomentName;
    ImageView imageView;

    public CouponPhoto(String couponname, String expiryDate, String imagestring, String doucomentName)
    {
        super(couponname,expiryDate);
        this.imagestring=imagestring;
        this.id= allid;
        allid++;
        this.doucomentName = doucomentName;
    }
    public CouponPhoto(String couponname, String expiryDate, ImageView imageView)
    {
        super(couponname,expiryDate);
        this.imageView=imageView;
        this.id= allid;
        allid++;
    }

    public String getIdStr()
    {
        return Integer.toString(id);
    }

    public Bitmap getImageviewBitMap(){
        byte[] decodedBytes = Base64.decode(imagestring, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }

    public Integer getId()
    {
        return id;
    }
}

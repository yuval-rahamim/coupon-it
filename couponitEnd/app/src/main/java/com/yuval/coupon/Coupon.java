package com.yuval.coupon;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.Calendar;

public class Coupon implements Serializable {

    int id;
    static int allid = 1;
    String couponname,coupontext,ExpiryDate;
    String doucomentName;
    boolean haveImage = false;


    public Coupon(String couponname, String ExpiryDate)
    {
        this.couponname=couponname;
        this.ExpiryDate=ExpiryDate;
        this.id= allid;
        allid++;
    }

    public Coupon(String couponname, String coupontext,String ExpiryDate,String doucomentName)
    {
        this.couponname=couponname;
        this.coupontext=coupontext;
        this.id= allid;
        allid++;
        this.ExpiryDate = ExpiryDate;
        this.doucomentName = doucomentName;
    }
    public Coupon(String couponname, String coupontext,String ExpiryDate, boolean haveImage)
    {
        this.couponname=couponname;
        this.coupontext=coupontext;
        this.id= allid;
        allid++;
        this.ExpiryDate = ExpiryDate;
        this.doucomentName = doucomentName;
        this.haveImage = haveImage;
    }
    public Coupon(String couponname, String coupontext,String ExpiryDate)
    {
        this.couponname=couponname;
        this.coupontext=coupontext;
        this.id= allid;
        allid++;
        this.ExpiryDate = ExpiryDate;
        this.doucomentName = doucomentName;
    }

    public int getExpiryDay()
    {
        String[] arrOfStr = ExpiryDate.split("/", 5);
        int day = Integer.parseInt(arrOfStr[0]);
        return day;
    }
    public int getExpiryMonth()
    {
        String[] arrOfStr = ExpiryDate.split("/", 5);
        int Month = Integer.parseInt(arrOfStr[1]);
        return Month;
    }
    public int getExpiryYear()
    {
        String[] arrOfStr = ExpiryDate.split("/", 5);
        int year = Integer.parseInt(arrOfStr[2]);
        return year;
    }
    public Calendar getCalender()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR,getExpiryYear());
        calendar.set(Calendar.MONTH,getExpiryMonth()-1);
        calendar.set(Calendar.DAY_OF_MONTH,getExpiryDay()-1);
        calendar.set(Calendar.HOUR_OF_DAY,12);
        calendar.set(Calendar.MINUTE,52);
        calendar.set(Calendar.SECOND,0);
        return calendar;
    }
    public String getCouponname()
    {
        return couponname;
    }
    public String getCoupontext()
    {
        return coupontext;
    }
    public String getIdStr()
    {
        return Integer.toString(id);
    }

    public Integer getId()
    {
        return id;
    }
}



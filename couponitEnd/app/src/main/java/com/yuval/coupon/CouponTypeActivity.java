package com.yuval.coupon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CouponTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
    }

    public void back(View view)
    {
        startActivity(new Intent(this, CouponsListsActivity.class));
    }

    public void open_text(View view)
    {
        startActivity(new Intent(this, TextActivity.class));
    }

    public void open_photo(View view) {
        startActivity(new Intent(this, PhotoActivity.class));
    }
}
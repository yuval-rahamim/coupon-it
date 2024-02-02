package com.yuval.coupon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class PhotoActivity extends AppCompatActivity {

    ImageView imageView;
    Intent intent;
    EditText couponname;
    static String datestring;
    static Button date;
    CouponPhoto couponPhoto;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        date = findViewById(R.id.date);
        imageView = findViewById(R.id.imageView3);

        if (ContextCompat.checkSelfPermission(PhotoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(PhotoActivity.this,new String[]{
                    Manifest.permission.CAMERA
            },100);

        }

        intent = new Intent(this, CouponsListsActivity.class);
        Intent intentthis = getIntent();
        couponname = findViewById(R.id.couponname);

        if(intentthis!=null && intentthis.hasExtra("CouponP"))
        {
            couponPhoto= (CouponPhoto) getIntent().getSerializableExtra("CouponP");
            couponname.setText(couponPhoto.couponname);
            bitmap = couponPhoto.getImageviewBitMap();
            imageView.setImageBitmap(bitmap);
            date.setText(couponPhoto.ExpiryDate);
        }
    }

    public void add(View view)
    {
        SharedPreferences sp= getSharedPreferences("groups",0);
        SharedPreferences.Editor editor = sp.edit();
        String uid = sp.getString("groupname","");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference couponcollection = db.collection("groups").document(uid).collection("GroupPhotoCoupons");
        Map<String, Object> group = new HashMap<>();
        group.put("couponname",couponname.getText().toString());
        group.put("ExpiryDate",date.getText().toString());
        group.put("image",GetImageToString());

        couponcollection.add(group).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                startActivity(intent);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }

    private String GetImageToString()
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String imageString = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;
    }


    public void takePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);
    }
    public void takeFromGallery(View view)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 5);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
        if (requestCode == 5 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
           try {
               InputStream inputStream = getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
               bitmap = ResizeImage(bitmap);
               imageView.setImageBitmap(bitmap);
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           }
        }
    }

    public Bitmap ResizeImage(Bitmap bitmap)
    {
        int maxWidth = 550; // The maximum width of the shrunken image
        int maxHeight = 550; // The maximum height of the shrunken image

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();

        // Keep the image ratio
        float ratio = Math.min((float) maxWidth / originalWidth, (float) maxHeight / originalHeight);
        int newWidth = Math.round(originalWidth * ratio);
        int newHeight = Math.round(originalHeight * ratio);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    public void showDatePickerDialog(View view)
    {
        DialogFragment newFragment =  new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    public void delete(View view) {startActivity(new Intent(this, CouponsListsActivity.class)); }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(requireContext(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            datestring = day+"/"+(month+1)+"/"+year;
            date.setText(datestring);
        }
    }
}
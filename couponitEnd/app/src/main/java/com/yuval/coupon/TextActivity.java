package com.yuval.coupon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TextActivity extends AppCompatActivity {
    Intent intent;
    EditText couponname,coupontext;
    Button del;
    ImageButton back;
    static Button date;
    Coupon coupon;
    boolean already;
    static String datestring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textactivity);
        already =false;
        del = findViewById(R.id.delete);
        back = findViewById(R.id.imageButton2);
        date = findViewById(R.id.date);

        intent = new Intent(this, CouponsListsActivity.class);
        Intent intentthis = getIntent();
        couponname = findViewById(R.id.couponname);
        coupontext = findViewById(R.id.coupontext);

        if(intentthis!=null && intentthis.hasExtra("Coupon"))
        {
            del.setVisibility(View.VISIBLE);
            back.setVisibility(View.INVISIBLE);
            coupon= (Coupon) getIntent().getSerializableExtra("Coupon");
            couponname.setText(coupon.couponname);
            coupontext.setText(coupon.coupontext);
            date.setText(coupon.ExpiryDate);
            already = true;
        }
    }

    public void add(View view)
    {
        coupon = new Coupon(couponname.getText().toString(), coupontext.getText().toString(),date.getText().toString());

        SharedPreferences sp= getSharedPreferences("groups",0);
        SharedPreferences.Editor editor = sp.edit();
        String uid = sp.getString("groupname","");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference couponcollection = db.collection("groups").document(uid).collection("groupcoopons");
        Map<String, Object> group = new HashMap<>();
        group.put("couponname",couponname.getText().toString());
        group.put("coupontext",coupontext.getText().toString());
        group.put("ExpiryDate",date.getText());
        // Add a new document with a generated ID
        couponcollection.add(group).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        intent.putExtra("Coupon",coupon);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error adding document", e);
                    }
                });


    }

    public void back(View view)
    {
        startActivity(new Intent(this, CouponsListsActivity.class));
    }

    public void delete(View view) { startActivity(new Intent(this, CouponsListsActivity.class));}


    public void showDatePickerDialog(View view)
    {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

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
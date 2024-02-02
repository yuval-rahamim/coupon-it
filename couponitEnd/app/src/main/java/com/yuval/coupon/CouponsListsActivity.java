package com.yuval.coupon;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class CouponsListsActivity extends AppCompatActivity {
    ListView listView,PhotolistView;
    String coupontext, couponname;
    int couponid;
    String grouptext;
    static ArrayList<Coupon> MainCoupons;
    static ArrayList<CouponPhoto> MainCouponsPhoto;
    Intent intenteditText;
    Intent intenteditPhoto;

    AlarmManager alarmManager;

    SharedPreferences sp;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_couponslists);

                listView = (ListView) findViewById(R.id.listView);
                PhotolistView = (ListView) findViewById(R.id.PhotolistView);

                MainCoupons = new ArrayList<Coupon>();
                MainCouponsPhoto = new ArrayList<CouponPhoto>();

                Intent intent = getIntent();

                intenteditText = new Intent(this, TextActivity.class);
                intenteditPhoto = new Intent(this, PhotoActivity.class);

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("groups",0);
                String groupname = sharedPreferences.getString("groupname","");


                CollectionReference couponcollection = db.collection("groups").document(groupname).collection("groupcoopons");
                couponcollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Coupon coup = new Coupon(document.get("couponname").toString(), document.get("coupontext").toString(), document.get("ExpiryDate").toString(),document.getId());
                                MainCoupons.add(coup);
                                SortCouponList(MainCoupons);
                                CustomAdapterT customAdapter = new CustomAdapterT(getApplicationContext(), MainCoupons);
                                listView.setAdapter(customAdapter);
                            }

                        } else {

                        }
                    }
                });

                CollectionReference PhotoCouponCollection = db.collection("groups").document(groupname).collection("GroupPhotoCoupons");
                PhotoCouponCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                CouponPhoto couponPhoto = new CouponPhoto(document.get("couponname").toString(), document.get("ExpiryDate").toString(),document.get("image").toString(),document.getId());
                                MainCouponsPhoto.add(couponPhoto);
                                SortCouponPhotoList(MainCouponsPhoto);
                                CustomAdapterP customAdapterp = new CustomAdapterP(getApplicationContext(), MainCouponsPhoto);
                                PhotolistView.setAdapter(customAdapterp);
                            }
                            setAlarm();
                        } else {

                        }
                    }
                });



                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Coupon coupon = (Coupon) adapterView.getItemAtPosition(i);
                        intenteditText.putExtra("Coupon", coupon);

                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("groups",0);
                        String groupname = sharedPreferences.getString("groupname","");

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference couponcollection = db.collection("groups").document(groupname).collection("groupcoopons");
                        couponcollection.document(coupon.doucomentName).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(intenteditText);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });


                    }
                });
                PhotolistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        CouponPhoto couponP = (CouponPhoto) parent.getItemAtPosition(position);
                        intenteditPhoto.putExtra("CouponP", couponP);

                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("groups",0);
                        String groupname = sharedPreferences.getString("groupname","");

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference couponPcollection = db.collection("groups").document(groupname).collection("GroupPhotoCoupons");

                        couponPcollection.document(couponP.doucomentName).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(intenteditPhoto);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                startActivity(intenteditPhoto);
                            }
                        });

                    }
                });

            }


    private void setAlarm() {

        cancelAlarm();
        if (!MainCoupons.isEmpty()) {
            int i =0;
            Coupon firstCoupon = MainCoupons.get(i);
            Calendar calendar = firstCoupon.getCalender();
            Calendar today = Calendar.getInstance();
            if (today.before(calendar)) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }else{
                if (!MainCouponsPhoto.isEmpty()) {
                    i =0;
                    Coupon firstPhotoCoupon = MainCouponsPhoto.get(i);
                    Calendar Photocalendar = firstPhotoCoupon.getCalender();
                    if (today.before(Photocalendar)) {
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(this, AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, Photocalendar.getTimeInMillis(), pendingIntent);
                    }
                }
            }

        }

    }

    private void cancelAlarm() {
        try {
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);

            if (pendingIntent != null) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }catch (Exception ex){

        }
    }

//    private void cancelAlarm() {
//        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.cancel(pendingIntent);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item1:

                Toast.makeText(this, "log out Selected", Toast.LENGTH_SHORT).show();

                sp= getSharedPreferences("users",0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("stay",false);
                editor.commit();

                //delete

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("groups",0);
                String uid = sharedPreferences.getString("groupname","");

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                 db.collection("groups").document(uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void aVoid) {
                         Log.d(TAG, "DocumentSnapshot successfully deleted!");
                     }}).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 Log.w(TAG, "Error deleting document", e);
                             }});
                 Intent intent = new Intent(this, SignInUpActivity.class);
                intent.putExtra("out", true);
                startActivity(intent);
                break;
            case R.id.item2:
                Toast.makeText(getApplicationContext(), "group", Toast.LENGTH_LONG).show();
                showCustomDialog();
                break;
            case R.id.item4:
                sp= getSharedPreferences("users",0);
                SharedPreferences.Editor editor2 = sp.edit();
                editor2.putBoolean("stay",false);
                editor2.commit();
                Intent intent2 = new Intent(this, SignInUpActivity.class);
                intent2.putExtra("out", true);
                startActivity(intent2);
                break;

        }
        return true;
    }
            public void add(View view) {
                startActivity(new Intent(this, CouponTypeActivity.class));
            }

    void showCustomDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.groups);
        dialog.show();

        EditText groupname = dialog.findViewById(R.id.group_name);
        Button groupbutton = dialog.findViewById(R.id.group_button);

        groupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomAdapterP adapterP = (CustomAdapterP) PhotolistView.getAdapter();
                adapterP.clearData();
                adapterP.notifyDataSetChanged();
                CustomAdapterT adapterT = (CustomAdapterT) listView.getAdapter();
                adapterT.clearData();
                adapterT.notifyDataSetChanged();

                grouptext = groupname.getText().toString();

                sp= getSharedPreferences("groups",0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("groupname",grouptext);
                editor.commit();

                String groupname = grouptext;

                ArrayList<Coupon> coupons = new ArrayList<Coupon>();
                ArrayList<CouponPhoto> couponsPhoto = new ArrayList<CouponPhoto>();
                FirebaseFirestore detabase = FirebaseFirestore.getInstance();

                CollectionReference PhotoCouponCollection = detabase.collection("groups").document(groupname).collection("GroupPhotoCoupons");
                PhotoCouponCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                CouponPhoto couponPhoto = new CouponPhoto(document.get("couponname").toString(), document.get("ExpiryDate").toString(),document.get("image").toString(),document.getId());
                                couponsPhoto.add(couponPhoto);
                            }
                            SortCouponPhotoList(couponsPhoto);
                            CustomAdapterP customAdapterp = new CustomAdapterP(getApplicationContext(), couponsPhoto);
                            PhotolistView.setAdapter(customAdapterp);
                        } else { }
                    }
                });

                CollectionReference couponc = detabase.collection("groups").document(groupname).collection("groupcoopons");
                couponc.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Coupon coup = new Coupon(document.get("couponname").toString(), document.get("coupontext").toString(),document.get("ExpiryDate").toString() ,document.getId());
                                coupons.add(coup);

                            }
                            SortCouponList(coupons);
                            CustomAdapterT customAdapter = new CustomAdapterT(getApplicationContext(), coupons);
                            listView.setAdapter(customAdapter);
                        } else { }
                    }
                });

                dialog.dismiss(); }});
    }

    public void back(View view)
    {
        Intent intent = new Intent(this, SignInUpActivity.class);
        intent.putExtra("out", true);
        startActivity(intent);
    }
    public  void SortCouponList(ArrayList<Coupon> couponslist)
    {
        Collections.sort(couponslist, new Comparator<Coupon>() {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            @Override
            public int compare(Coupon c1, Coupon c2) {
                try {
                    Date d1 = dateFormat.parse(c1.ExpiryDate);
                    Date d2 = dateFormat.parse(c2.ExpiryDate);
                    return d1.compareTo(d2);
                }catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }
    public  void SortCouponPhotoList(ArrayList<CouponPhoto> couponslist)
    {
        Collections.sort(couponslist, new Comparator<Coupon>() {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            @Override
            public int compare(Coupon c1, Coupon c2) {
                try {
                    Date d1 = dateFormat.parse(c1.ExpiryDate);
                    Date d2 = dateFormat.parse(c2.ExpiryDate);
                    return d1.compareTo(d2);
                }catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }
}

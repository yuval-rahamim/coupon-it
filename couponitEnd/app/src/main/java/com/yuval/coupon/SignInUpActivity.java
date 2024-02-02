package com.yuval.coupon;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignInUpActivity extends AppCompatActivity {

    EditText useremail,password;
    Button login,register;
    ImageButton mute;
    CheckBox checkBox;
    String email, pass;
    ProgressBar progressbar;

    private FirebaseAuth mAuth;
    Intent intent;
    SharedPreferences sp;
    FirebaseFirestore db ;


   // String tv;
    SensorManager sm;
    SensorEventListener listener;
    Sensor light;
    private boolean isBrightnessSensorActive = false; // Flag to track the state of brightness sensor


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        createNotificationChannel();

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        light = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        sm.unregisterListener(listener);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        useremail = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        checkBox = findViewById(R.id.checkBox);
        progressbar = findViewById(R.id.progressbar);
        mute = findViewById(R.id.mutebutton);

        intent = new Intent(this, CouponsListsActivity.class);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("users",0);
        SharedPreferences sharedPreferencesGroups = getApplicationContext().getSharedPreferences("groups",0);
        String groupname = sharedPreferencesGroups.getString("groupname","");

        boolean ischecked = sharedPreferences.getBoolean("stay",false);
        if(ischecked)
        {
            boolean out = getIntent().getBooleanExtra("out",false);
            if (out==false){
                finish();
                startActivity(intent);

            }
        }
    }



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void signup(View view)
    {

        email = useremail.getText().toString();
        //סיסמא מוצפנת
        pass = getSHA512SecurePassword(password.getText().toString());
        if (password.getTextSize()<6){
            Toast.makeText(getApplicationContext(), "your password is to week", Toast.LENGTH_LONG).show();
        }
        if (!isEmpty(password) || !isEmpty(useremail)) {
            // show the visibility of progress bar to show loading
            progressbar.setVisibility(View.VISIBLE);
            // create new user or register new user
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful()) {

                                Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                                // hide the progress bar
                                progressbar.setVisibility(View.GONE);

                                SharedPreferences spgroups= getSharedPreferences("groups",0);
                                SharedPreferences.Editor editorgroups = spgroups.edit();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                editorgroups.putString("groupname",user.getUid());
                                editorgroups.commit();

                                sp= getSharedPreferences("users",0);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putBoolean("stay",checkBox.isChecked());
                                editor.commit();
                                finish();
                                startActivity(intent);
                            }
                            else {

                                // Registration failed
                                Toast.makeText(getApplicationContext(), "Registration failed!!" + " Please try again later", Toast.LENGTH_LONG).show();
                                // hide the progress bar
                                progressbar.setVisibility(View.GONE);
                            }
                        }
                    });



        }else{
        if (isEmpty(password))
            password.setError("enter a password!");
        if (isEmpty(useremail))
            useremail.setError("enter a user email!");
        }

    }

    public void signin(View view)
    {
        SharedPreferences setting = getSharedPreferences("users", 0);
        boolean ischecked = setting.getBoolean("stay",false);

        progressbar.setVisibility(View.VISIBLE);
        email = useremail.getText().toString();
        //סיסמא מוצפנת
        pass = getSHA512SecurePassword(password.getText().toString());

        if (isEmpty(password))
            password.setError("enter a password!");
        if (isEmpty(useremail))
            useremail.setError("enter a user name!");
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful()) { Toast.makeText(getApplicationContext(), "Login successful!!", Toast.LENGTH_LONG).show();
                                    // hide the progress bar
                                    progressbar.setVisibility(View.GONE);
                                    // if sign-in is successful
                                    // intent to home activity

                                    SharedPreferences sp= getSharedPreferences("groups",0);
                                    SharedPreferences.Editor editor = sp.edit();

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    editor.putString("groupname",user.getUid());
                                    editor.commit();

                                    sp= getSharedPreferences("users",0);
                                    SharedPreferences.Editor edit = sp.edit();
                                    edit.putBoolean("stay",checkBox.isChecked());
                                    edit.commit();
                                    finish();
                                    startActivity(intent);
                                } else {
                                    // sign-in failed
                                    Toast.makeText(getApplicationContext(), "Login failed!!", Toast.LENGTH_LONG).show();
                                    // hide the progress bar
                                    progressbar.setVisibility(View.GONE);
                                }
                            }
                        });
    }
    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    //הצפנה
    public static String getSHA512SecurePassword(String passwordToHash)
    {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update("everybreathyoutake".getBytes());
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public void guest(View view) {
        SharedPreferences sp= getSharedPreferences("groups",0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("groupname","guest");
        editor.commit();

        sp= getSharedPreferences("users",0);
        editor = sp.edit();
        editor.putBoolean("stay",checkBox.isChecked());
        editor.commit();


        startActivity(intent);
        finish();
    }

    public void mute(View view)
    {
        if (isMyServiceRunning(BackgroundMusicService.class)) {
            mute.setImageResource(android.R.drawable.ic_lock_silent_mode);
            stopService(new Intent(this, BackgroundMusicService.class));
        }else
            {
                mute.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
                startService(new Intent(this, BackgroundMusicService.class));
            }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void autoBright(View view) {


        if(!isBrightnessSensorActive) {

            isBrightnessSensorActive = true;

            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                startActivity(intent);
            }

            Toast.makeText(SignInUpActivity.this, "הבהירות משתנה בהתאם לסביבה שלך (:", Toast.LENGTH_SHORT).show();

            ContentResolver contentResolver = this.getApplicationContext().getContentResolver();
            listener = new SensorEventListener() {


                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    //Toast.makeText(SignInUpActivity.this, "accuracy changed!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSensorChanged(SensorEvent event) {
                    // tv = String.valueOf(event.values[0]);
                    //Toast.makeText(getApplicationContext(), tv, Toast.LENGTH_LONG).show();
                    Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, (int) event.values[0]);

                }
            };

            sm.registerListener(listener, light, SensorManager.SENSOR_DELAY_FASTEST);
        }else {
            sm.unregisterListener(listener);
            isBrightnessSensorActive = false;
            Toast.makeText(SignInUpActivity.this, "מצב בהירות אוטומטי נכבה ):", Toast.LENGTH_SHORT).show();
        }
    }
}
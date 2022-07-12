package com.example.iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.iot.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final int[] led = new int[3];
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();


        myRef.child("Bulb").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                led[0] = dataSnapshot.child("oneIsOn").getValue(Integer.class);
                if(led[0] == 1){
                    binding.BulbOne.setForeground(getDrawable(R.drawable.bulb_on));
                }else{
                    binding.BulbOne.setForeground(getDrawable(R.drawable.bulb_off));
                }

                led[1] = dataSnapshot.child("twoIsOn").getValue(Integer.class);
                if(led[1] == 1){
                    binding.BulbTwo.setForeground(getDrawable(R.drawable.bulb_on));
                }else{
                    binding.BulbTwo.setForeground(getDrawable(R.drawable.bulb_off));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Main", "Failed to read value.", error.toException());
            }
        });

        binding.BulbOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(led[0] == 1){
                    myRef.child("Bulb").child("oneIsOn").setValue(0);
                }else {
                    myRef.child("Bulb").child("oneIsOn").setValue(1);
                }
            }
        });
        binding.BulbTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(led[1] == 1){
                    myRef.child("Bulb").child("twoIsOn").setValue(0);
                }else {
                    myRef.child("Bulb").child("twoIsOn").setValue(1);
                }
            }
        });

        binding.colorpallete.setDrawingCacheEnabled(true);
        binding.colorpallete.buildDrawingCache(true);

        binding.colorpallete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try{
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE){
                        bitmap = binding.colorpallete.getDrawingCache();
                        int pixels = bitmap.getPixel((int)motionEvent.getX(),(int)motionEvent.getY());
                        int r = Color.red(pixels);
                        int g = Color.green(pixels);
                        int b = Color.blue(pixels);

                        myRef.child("RGB").child("Red").setValue(r);
                        myRef.child("RGB").child("Green").setValue(g);
                        myRef.child("RGB").child("Blue").setValue(b);

//                        String hex = "#"+Integer.toHexString(pixels);
                        if(led[2] == 1){
                            binding.view.setBackgroundColor(Color.rgb(r,g,b));
                        }

                    }
                }catch (Exception e){

                }

                return true;
            }
        });

        myRef.child("RGB").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               int r = snapshot.child("Red").getValue(Integer.class);
               int g = snapshot.child("Green").getValue(Integer.class);
               int b = snapshot.child("Blue").getValue(Integer.class);
               led[2] = snapshot.child("isOn").getValue(Integer.class);
               binding.view.setBackgroundColor(Color.rgb(r,g,b));
                if(led[2] == 1){
                    binding.view.setBackgroundColor(Color.rgb(r,g,b));
                }else{
                    binding.view.setBackgroundColor(Color.parseColor("#444442"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.RGB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(led[2] == 1){
                    myRef.child("RGB").child("isOn").setValue(0);
                }else {
                    myRef.child("RGB").child("isOn").setValue(1);
                }
            }
        });

    }
}
package com.example.app.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.app.R;
import com.example.app.fragment.customer_profile;

public class profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.holder,new customer_profile()).commit();


    }
}
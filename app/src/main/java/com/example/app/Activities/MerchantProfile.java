package com.example.app.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;

public class MerchantProfile extends AppCompatActivity
{
    private static final String TAG = "MerchantProfile";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_profile);
    }
}
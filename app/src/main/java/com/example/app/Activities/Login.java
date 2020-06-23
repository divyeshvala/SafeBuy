package com.example.app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.app.R;
import com.example.app.ui.login.SectionsPagerAdapter;

//Login Activity has fragments for both sigin and signup
public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                TextView textViewLogin = findViewById(R.id.loginText);
                TextView textViewSignUp = findViewById(R.id.signUpText);
                if(position == 0){
                    textViewLogin.setTextColor(getResources().getColor(R.color.white));
                    textViewSignUp.setTextColor(getResources().getColor(R.color.grey));
                }
                else{
                    textViewLogin.setTextColor(getResources().getColor(R.color.grey));
                    textViewSignUp.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            }
        }, 2000);

    }
}
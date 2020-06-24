package com.example.app.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.fragment.FragmentLogin;
import com.example.app.fragment.FragmentSignUp;
import com.example.app.fragment.LoginSignupFragmentAdapter;
import com.example.app.ui.login.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Login Activity has fragments for both sigin and signup
public class Login extends AppCompatActivity
{
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // This part whether the user is currently logged in

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseUser != null){
                    // if user is logged in
                    // goto main activity

                    Toast.makeText(Login.this, "you are logged in", Toast.LENGTH_LONG);
                    Intent intent = new Intent(Login.this, MainActivity.class);

                    startActivity(intent);
                }
                else{
                    Toast.makeText(Login.this, "Log in", Toast.LENGTH_LONG);
                }

            }
        };

        // if user is not logged  you need to login or signup

        LoginSignupFragmentAdapter adapter = new LoginSignupFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(FragmentLogin.newInstance(), "Login");
        adapter.addFragment(FragmentSignUp.newInstance(), "Signup");
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

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

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            }
        }, 5000);*/
    }
}
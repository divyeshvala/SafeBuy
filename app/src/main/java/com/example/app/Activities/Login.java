package com.example.app.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.fragment.FragmentLogin;
import com.example.app.fragment.FragmentSignUp;
import com.example.app.fragment.LoginSignupFragmentAdapter;
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

        RadioGroup selectUser = findViewById(R.id.id_radioGroup);
        final RadioButton customer = findViewById(R.id.id_customerUser);
        final RadioButton merchant = findViewById(R.id.id_merchantUser);

        final SharedPreferences settings = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        selectUser.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i)
            {
                Log.i("Login", "something selected");

                if(i==customer.getId())
                {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("userType", "customer");
                    editor.apply();
                    Log.i("Login", "Customer selected");
                }
                else
                {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("userType", "merchant");
                    editor.apply();
                    Log.i("Login", "merchant selected");
                }
            }
        });

        // This part whether the user is currently logged in
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        LoginSignupFragmentAdapter adapter = new LoginSignupFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(FragmentLogin.newInstance(), "Login");
        adapter.addFragment(FragmentSignUp.newInstance(), "SignUp");
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
    }
}
package com.example.app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.example.app.R;
import com.example.app.Utilities.EncryptionDecryption;
import com.example.app.fragment.CustomerMainFragmentAdapter;
import com.example.app.fragment.FragmentCustomers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.security.SecureRandom;

import javax.crypto.spec.IvParameterSpec;

public class MerchantMain extends AppCompatActivity
{
    private static final String TAG = "MerchantMain";
    private CustomerMainFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchantmain);

        ImageView profile = findViewById(R.id.p_image);
        SwitchCompat state = findViewById(R.id.swOnOff);

        adapter = new CustomerMainFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(FragmentCustomers.newInstance(), "Customers"); //todo

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        profile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MerchantMain.this, profile.class));
            }
        });

        state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                FirebaseDatabase.getInstance().getReference().child("merchants")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child("isOpen")
                        .setValue(b);
            }
        });
    }
}

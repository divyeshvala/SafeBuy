package com.example.app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.app.R;
import com.example.app.fragment.CustomerMainFragmentAdapter;
import com.example.app.fragment.FragmentCustomers;

public class MerchantMain extends AppCompatActivity {

    private CustomerMainFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchantmain);

        ImageView profile = findViewById(R.id.p_image);

        adapter = new CustomerMainFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(FragmentCustomers.newInstance(), "Customers"); //todo

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MerchantMain.this, MerchantProfile.class));
            }
        });
    }
}

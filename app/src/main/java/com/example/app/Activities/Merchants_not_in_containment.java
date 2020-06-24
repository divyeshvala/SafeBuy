package com.example.app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.app.R;
import com.example.app.fragment.CustomerMainFragmentAdapter;
import com.example.app.fragment.Frag_safe_merch;
import com.example.app.fragment.FragmentNearYou;
import com.example.app.fragment.FragmentVisited;

//whole code is cfm
public class Merchants_not_in_containment extends AppCompatActivity {
    private CustomerMainFragmentAdapter adapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchants_not_in_containment);
        adapter2 = new CustomerMainFragmentAdapter(getSupportFragmentManager());
        adapter2.addFragment(new Frag_safe_merch(), "Safe merchants near you");
        ViewPager viewPager2 = findViewById(R.id.view_pager);
        viewPager2.setAdapter(adapter2);
    }
}
package com.example.app.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.app.R;
import com.example.app.fragment.CustomerMainFragmentAdapter;
import com.example.app.fragment.FilterBottomSheetFragment;
import com.example.app.fragment.FragmentNearYou;
import com.example.app.fragment.FragmentVisited;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

//Main Screen of the customer
public class customer_main extends AppCompatActivity implements FilterBottomSheetFragment.BottomSheetListener {

    private CustomerMainFragmentAdapter adapter;
    FilterBottomSheetFragment bottomSheetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        Log.i("cusomer_main", "Inside customer main");

        //Two fragments are created, one for the merchants near you and the other for merchants that are already visited
        adapter = new CustomerMainFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(FragmentNearYou.newInstance(), "ATMs");
        adapter.addFragment(FragmentVisited.newInstance(), "Merchants");

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

//        floating action button for the filters
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetFragment = new FilterBottomSheetFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });
    }

    @Override
    public void onButtonClicked(String location, String distance)
    {
        Log.i("customer_main", "Here :\n"+location+"\n"+distance);
        bottomSheetFragment.dismiss();
    }
}
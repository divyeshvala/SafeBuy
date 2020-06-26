package com.example.app.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.app.R;
import com.example.app.fragment.CustomerMainFragmentAdapter;
import com.example.app.fragment.FilterBottomSheetFragment;
import com.example.app.fragment.FragmentNearYou;
import com.example.app.fragment.FragmentVisited;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import java.util.Arrays;

//Main Screen of the customer
public class customer_main extends AppCompatActivity implements FilterBottomSheetFragment.BottomSheetListener {

    private static final String TAG = "customer_main";
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

        findViewById(R.id.id_tempProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(customer_main.this, CustomerProfile.class));
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
package com.example.app.Activities;

import android.os.Bundle;

import com.example.app.R;
import com.example.app.fragment.CustomerMainFragmentAdapter;
import com.example.app.fragment.FilterBottomSheetFragment;
import com.example.app.fragment.FragmentATM;
import com.example.app.fragment.FragmentMerchant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

//Main Screen of the customer
public class CustomerMain extends AppCompatActivity implements FilterBottomSheetFragment.BottomSheetListener {

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
        adapter.addFragment(FragmentATM.newInstance(), "ATM");
        adapter.addFragment(FragmentMerchant.newInstance(), "Merchants");

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                TextView atmTextview = findViewById(R.id.atmTextview);
                TextView merchantTextview = findViewById(R.id.merchantTextview);
                if(position == 0){
                    atmTextview.setTextColor(getResources().getColor(R.color.black));
                    merchantTextview.setTextColor(getResources().getColor(R.color.grey));
                }
                else{
                    atmTextview.setTextColor(getResources().getColor(R.color.grey));
                    merchantTextview.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

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
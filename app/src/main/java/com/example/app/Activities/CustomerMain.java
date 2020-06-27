package com.example.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import com.example.app.R;
import com.example.app.fragment.CustomerMainFragmentAdapter;
import com.example.app.fragment.FilterBottomSheetFragment;
import com.example.app.fragment.FragmentATM;
import com.example.app.fragment.FragmentMerchant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app.Utilities.util;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomerMain extends AppCompatActivity implements FilterBottomSheetFragment.BottomSheetListener {

    private static final String TAG = "customer_main";
    private CustomerMainFragmentAdapter adapter;
    FilterBottomSheetFragment bottomSheetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        Log.i("cusomer_main", "Inside customer main");

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }

        RelativeLayout relativeLayout = findViewById(R.id.banner);
        relativeLayout.getBackground().setAlpha(200);

        //Two fragments are created, one for the merchants near you and the other for merchants that are already visited
        adapter = new CustomerMainFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(FragmentMerchant.newInstance(), "Merchants");
        adapter.addFragment(FragmentATM.newInstance(), "ATM");


        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                TextView atmTextview = findViewById(R.id.atmTextview);
                TextView merchantTextview = findViewById(R.id.merchantTextview);
                if(position == 0){
                    atmTextview.setTextColor(getResources().getColor(R.color.grey));
                    merchantTextview.setTextColor(getResources().getColor(R.color.black));
                }
                else{
                    atmTextview.setTextColor(getResources().getColor(R.color.black));
                    merchantTextview.setTextColor(getResources().getColor(R.color.grey));
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

        ImageView profile = findViewById(R.id.id_customerProfile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomerMain.this, profile.class));
            }
        });
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        util.listItems = sharedText;
        Toast.makeText(this, "Select a merchant", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onButtonClicked()
    {
        Log.i("customer_main", "Here :");
        bottomSheetFragment.dismiss();
    }
}
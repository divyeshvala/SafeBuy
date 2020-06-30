package com.example.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.example.app.R;
import com.example.app.Utilities.util;
import com.example.app.fragment.CustomerMainFragmentAdapter;
import com.example.app.fragment.FragmentATM;
import com.example.app.fragment.FragmentMerchants;

public class CustomerMain extends AppCompatActivity
{
    private static final String TAG = "customer_main";
    private CustomerMainFragmentAdapter adapter;
    public static int activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        Intent intent = getIntent();
        final String action = intent.getAction();
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
        adapter.addFragment(FragmentMerchants.newInstance(), "Merchants");
        adapter.addFragment(FragmentATM.newInstance(), "ATMs");

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        activeFragment = 0;

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                TextView atmTextview = findViewById(R.id.atmTextview);
                TextView merchantTextview = findViewById(R.id.merchantTextview);
                if(position == 0){
                    atmTextview.setTextColor(getResources().getColor(R.color.grey));
                    merchantTextview.setTextColor(getResources().getColor(R.color.black));
                    activeFragment = 0;
                }
                else{
                    atmTextview.setTextColor(getResources().getColor(R.color.black));
                    merchantTextview.setTextColor(getResources().getColor(R.color.grey));
                    activeFragment = 1;
                }
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

}
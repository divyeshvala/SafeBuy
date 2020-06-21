package com.example.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.app.Messaging.Customer.DisplayMerchantsActivity;
import com.example.app.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button phase1 = findViewById(R.id.id_phase1);
        Button phase2 = findViewById(R.id.id_phase2);

        phase1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        phase2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, DisplayMerchantsActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        Thread thread = new Thread()
//        {
//            @Override
//            public void run()
//            {
//                try
//                {
//                    sleep(2000);
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//                finally
//                {
//                    Intent intent = new Intent(StartActivity.this, DisplayMerchantsActivity.class);
//                    startActivity(intent);
//
//                    finish();
//                }
//            }
//        };
//        thread.start();
    }
}

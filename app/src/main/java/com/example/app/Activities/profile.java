package com.example.app.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.app.R;
import com.example.app.fragment.customer_profile;
import com.example.app.fragment.fragment_merchant_profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.holder,new customer_profile()).commit();

        final TextView userName = findViewById(R.id.id_userName);
        TextView email = findViewById(R.id.id_userEmail);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser!=null)
            email.setText(currentUser.getEmail());
        else
            email.setText("Not available");

        final SharedPreferences settings = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        FirebaseDatabase.getInstance().getReference().child(settings.getString("userType", "customer")+"s")
                .child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                userName.setText(String.format("%s %s", dataSnapshot.child("firstName").getValue(String.class), dataSnapshot.child("lastName").getValue(String.class)));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}
package com.example.app.Messaging.Merchant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.app.R;
import com.example.app.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DisplayOrders extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_orders);

        getOrdersList();
    }

    private void getOrdersList()
    {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        final DatabaseReference merchantsDB = FirebaseDatabase.getInstance().getReference()
                .child("merchants").child(currentUser.getUid()).child("orders");

        merchantsDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Order order = new Order();
                order.setCustomerName(dataSnapshot.child("customerName").getValue(String.class));
                order.setCustomerAddress(dataSnapshot.child("customerAddress").getValue(String.class));
                order.setCustomerPhone(dataSnapshot.child("customerPhone").getValue(String.class));
                order.setCustomerUid(dataSnapshot.child("customerUid").getValue(String.class));

                // todo: Add it to the order list.
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

}

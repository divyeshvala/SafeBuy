package com.example.app.Utilities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

public class PaymentHandler
{
    private static final String TAG = "PaymentHandler";
    private Context context;
    private String receiverPAN, senderPAN;
    private String paymentResponse;
    private String amount, transactionCurrencyCode;

    public PaymentHandler(Context context, String senderPAN, String receiverPAN,String amount,String transactionCurrencyCode, String paymentResponse){
        this.context = context;
        this.senderPAN = senderPAN;
        this.receiverPAN = receiverPAN;
        this.amount = amount;
        this.transactionCurrencyCode = transactionCurrencyCode;
        this.paymentResponse = paymentResponse;
    }

    public void getTransactionStatus()
    {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference().child("PaymentRequest").push();

        Map<String, Object> requestMessage = new HashMap<>();

        requestMessage.put("senderPAN", senderPAN);
        requestMessage.put("receiverPAN", receiverPAN);
        requestMessage.put("amount", amount);
        requestMessage.put("transactionCurrencyCode", transactionCurrencyCode);

        databaseReference.updateChildren(requestMessage);

        Log.i(TAG, "databaseReference.getKey : " + databaseReference.getKey());

        final DatabaseReference responseTable = FirebaseDatabase.getInstance()
                .getReference().child("PaymentRequest").child(databaseReference.getKey());

        // Got the Payment result
        responseTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("gotResponse").getValue(String.class)!=null)
                {
                    responseTable.removeValue();
                    paymentResponse = dataSnapshot.getValue(String.class);
                    Intent intent = new Intent("GOT_PAYMENT_RESPONSE");
                    intent.putExtra("amount", amount);
                    context.sendBroadcast(intent);
                }
            }

            @Override 
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

       
    }
}

package com.example.app.Utilities;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PaymentHandler {

    private static final String TAG = "PaymentHandler";
    private final Context context;
    private final String receiverPAN, senderPAN;

    public PaymentHandler(Context context, String senderPAN, String receiverPAN){
        this.context = context;
        this.senderPAN = senderPAN;
        this.receiverPAN = receiverPAN;
    }

    public void getTransactionStatus(){

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference().child("PaymentRequest").push();

        Map<String, Object> requestMessage = new HashMap<>();

        requestMessage.put("senderPAN", senderPAN);
        requestMessage.put("receiverPAN", receiverPAN);

        databaseReference.updateChildren(requestMessage);

        Log.i(TAG, "databaseReference.getKey : " + databaseReference.getKey());

        final DatabaseReference responseTable = FirebaseDatabase.getInstance()
                .getReference().child("PaymentRequest").child(databaseReference.getKey());

    }
}

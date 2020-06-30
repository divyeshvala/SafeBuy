package com.example.app.Utilities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class PaymentHandler
{
    private static final String TAG = "PaymentHandler";
    private Context context;
    private String receiverPAN, senderPAN;
    private String amount, transactionCurrencyCode;

    public PaymentHandler(Context context, String senderPAN, String receiverPAN,String amount,String transactionCurrencyCode){
        this.context = context;
        this.senderPAN = senderPAN;
        this.receiverPAN = receiverPAN;
        this.amount = amount;
        this.transactionCurrencyCode = transactionCurrencyCode;
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
        requestMessage.put("gotResponse", "false");

        databaseReference.updateChildren(requestMessage);

        Log.i(TAG, "databaseReference.getKey : " + databaseReference.getKey());

        final DatabaseReference responseTable = FirebaseDatabase.getInstance()
                .getReference().child("PaymentRequest").child(databaseReference.getKey());

        responseTable.child("Result").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    Log.i(TAG, "Got payment response :"+dataSnapshot.child("response").getValue(String.class));
                    Intent intent = new Intent("GOT_PAYMENT_RESPONSE");
                    intent.putExtra("amount", amount);
                    intent.putExtra("status", "success");
                    intent.putExtra("response", dataSnapshot.child("response").getValue(String.class));
                    context.sendBroadcast(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        responseTable.child("gotResponse").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists() && dataSnapshot.getValue(String.class).equals("success"))
                {
                    responseTable.removeValue();
                }
                else if(dataSnapshot.exists() && dataSnapshot.getValue(String.class).equals("failure"))
                {
                    Log.i(TAG, "Got payment response :"+dataSnapshot.child("response").getValue(String.class));
                    Intent intent = new Intent("GOT_PAYMENT_RESPONSE");
                    intent.putExtra("status", "failed");
                    context.sendBroadcast(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}

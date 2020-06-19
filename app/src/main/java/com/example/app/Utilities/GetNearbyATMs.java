package com.example.app.Utilities;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetNearbyATMs
{
    private static final String TAG = "GetNearbyATMs";
    private ArrayList<ATMObject> ATMsList;

    public GetNearbyATMs() {
        this.ATMsList = new ArrayList<>();
    }

    public ArrayList<ATMObject> getListOfATMs(String addressLine)
    {
        // send request
        DatabaseReference newRequestDB = FirebaseDatabase.getInstance()
                .getReference().child("NearbyATMRequest").push();

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("distance", "20");
        messageData.put("distanceUnit", "km");
        messageData.put("placeName", addressLine);
        newRequestDB.updateChildren(messageData);
        Log.i(TAG, "newRequestDB.getKey() :"+newRequestDB.getKey());


        // get response
        DatabaseReference responseDB = FirebaseDatabase.getInstance().getReference()
                .child("NearbyATMRequest").child(newRequestDB.getKey()).child("result");

        responseDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    long latitude = dataSnapshot.child("coordinates").child("latitude").getValue(Long.class);
                    long longitude = dataSnapshot.child("coordinates").child("longitude").getValue(Long.class);
                    String placeName = dataSnapshot.child("placeName").getValue(String.class);
                    Log.i(TAG, "Got response : "+latitude+" "+longitude+" "+placeName);

                    ATMsList.add(new ATMObject(latitude, longitude, placeName));
                }
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
        return ATMsList;
    }
}

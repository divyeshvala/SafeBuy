package com.example.app.Utilities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import androidx.annotation.NonNull;

import com.example.app.model.LocationObject;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GetNearbyATMs
{
    private static final String TAG = "GetNearbyATMs";
    private Context mContext;
    private ArrayList<LocationObject> ATMsList;
    private ArrayList<LocationObject> containmentZoneList;
    private boolean isUsingMyLocation;
    private String distance;

    public GetNearbyATMs(Context mContext, ArrayList<LocationObject> ATMsList,
                         ArrayList<LocationObject> containmentZoneList, boolean isUsingMyLocation, String distance) {
        this.mContext = mContext;
        this.ATMsList = ATMsList;
        this.containmentZoneList = containmentZoneList;
        this.isUsingMyLocation = isUsingMyLocation;
        this.distance = distance;
    }

    public void getListOfATMs(String addressLine, double latitude, double longitude)
    {
        // send request
        final DatabaseReference newRequestDB = FirebaseDatabase.getInstance()
                .getReference().child("NearbyATMRequest").push();

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("distance", distance);
        messageData.put("distanceUnit", "m");
        messageData.put("latitude", latitude);
        messageData.put("longitude", longitude);
        messageData.put("placeName", addressLine);
        messageData.put("resolvedATM", "false");
        messageData.put("resolvedContainment", "false");
        newRequestDB.updateChildren(messageData);
        Log.i(TAG, "newRequestDB.getKey() :"+newRequestDB.getKey());
        // get response
        final DatabaseReference responseDB = FirebaseDatabase.getInstance().getReference()
                .child("NearbyATMRequest").child(newRequestDB.getKey());

        responseDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String resolvedATM = dataSnapshot.child("resolvedATM").getValue(String.class);
                String resolvedContainment = dataSnapshot.child("resolvedContainment").getValue(String.class);

                if( resolvedATM!=null && resolvedContainment!=null )
                {
                    if( resolvedATM.equals("success") && (!resolvedContainment.equals("false")))
                    {
                        Intent intent = new Intent("ACTION_FOUND_ATM_LIST");
                        intent.putExtra("status", "success");
                        intent.putExtra("isUsingMyLocation", isUsingMyLocation);
                        mContext.sendBroadcast(intent);

                        FirebaseDatabase.getInstance().getReference()
                                .child("NearbyATMRequest").child(newRequestDB.getKey()).removeValue();
                    }
                    else if ( (resolvedATM.equals("failure")) && (!resolvedContainment.equals("false")))
                    {
                        Intent intent = new Intent("ACTION_FOUND_ATM_LIST");
                        intent.putExtra("status", "failed");
                        intent.putExtra("isUsingMyLocation", isUsingMyLocation);
                        mContext.sendBroadcast(intent);

                        FirebaseDatabase.getInstance().getReference()
                                .child("NearbyATMRequest").child(newRequestDB.getKey()).removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        responseDB.child("ATMResult").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    Double lat = dataSnapshot.child("coordinates").child("latitude").getValue(Double.class);
                    Double lon = dataSnapshot.child("coordinates").child("longitude").getValue(Double.class);
                    Log.i(TAG, "Got atmList response : "+lat+" "+lon+" ");

                    String placeName = dataSnapshot.child("placeName").getValue(String.class);
                    ATMsList.add(new LocationObject(lat, lon, placeName));
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

        // response for containment zones
        responseDB.child("ContainmentResult").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    int numberOfNearbyZones = dataSnapshot.child("numberOfNearbyZones").getValue(Integer.class);
                    if(numberOfNearbyZones>0) {
                        for (DataSnapshot places : dataSnapshot.child("containmentZoneNames").getChildren())
                        {
                            String place = places.getValue(String.class);
                            if(place!=null && !place.equals(""))
                            {
                                try {
                                    Geocoder geocoder = new Geocoder(mContext,
                                            Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocationName(
                                            place,
                                            1
                                    );
                                    Address address = addresses.get(0);
                                    Log.i(TAG, "Got containment response :" + address.getLatitude() + " " + address.getLongitude() + " ");

                                    containmentZoneList.add(new LocationObject(address.getLatitude(),
                                            address.getLongitude(), place));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            numberOfNearbyZones--;
                        }
                    }
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
    }


}

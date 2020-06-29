package com.example.app.Utilities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.app.model.LocationObject;
import com.example.app.model.MerchObject;
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

public class GetNearbyMerchs {
    private static final String TAG = "GetNearbyMerchs";
    private boolean gotResponseForContainmentZones;
    private boolean gotResponseForMerchs;
    private Context mContext;
    private ArrayList<MerchObject> MerchsList;
    private ArrayList<LocationObject> containmentZoneList;
    private boolean isUsingMyLocation;
    private String distance;
    private List<Integer> va = new ArrayList<>();
    private List<String> vb = new ArrayList<>();


    public GetNearbyMerchs(Context mContext, ArrayList<MerchObject> MerchsList, ArrayList<LocationObject> containmentZoneList, boolean isUsingMyLocation, String distance) {
        this.mContext = mContext;
        this.MerchsList = MerchsList;
        this.containmentZoneList = containmentZoneList;
        this.isUsingMyLocation = isUsingMyLocation;
        this.distance = distance;
    }
    public void getListOfMerchs(String addressLine, double latitude, double longitude,int categoryCode,int distance,String distance_unit)
    {
        //MerchsList= new ArrayList<>();
        Log.i(TAG, "Entering getnearbymerchs");
        gotResponseForMerchs = false;
        gotResponseForContainmentZones = true;
        va.add(categoryCode);
        // send request
        final DatabaseReference newRequestDB = FirebaseDatabase.getInstance()
                .getReference().child("NearbyMerchantRequest").push();

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("merchantCategoryCode", va);
        messageData.put("latitude", latitude);
        messageData.put("longitude", longitude);
        messageData.put("distance", distance);
        messageData.put("distanceUnit", distance_unit);
        messageData.put("placeName", addressLine);
        messageData.put("resolvedMerchant", "false");
        messageData.put("resolvedContainment", "false");
        newRequestDB.updateChildren(messageData);
        Log.i(TAG, "newRequestDB.getKey() :"+newRequestDB.getKey());

        // get response
        final DatabaseReference responseDB = FirebaseDatabase.getInstance().getReference()
                .child("NearbyMerchantRequest").child(newRequestDB.getKey());

        responseDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                System.out.println("Inside ondatachange");
                if( dataSnapshot.child("resolvedMerchant").getValue(String.class)!=null &&
                        dataSnapshot.child("resolvedContainment").getValue(String.class)!=null )
                {
                    System.out.println("Inside ondatachange2");
                    if( dataSnapshot.child("resolvedMerchant").getValue(String.class).equals("true") &&
                            dataSnapshot.child("resolvedContainment").getValue(String.class).equals("true"))
                    {
                        System.out.println("Inside ondatachange3");
                        FirebaseDatabase.getInstance().getReference()
                                .child("NearbyMerchantRequest").child(newRequestDB.getKey()).removeValue();
                        System.out.println("Inside ondatachange4");
                        Intent intent = new Intent("ACTION_FOUND_MERCH_LIST");
                        System.out.println("size of merchlist"+MerchsList.size());
                        mContext.sendBroadcast(intent);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        responseDB.child("MerchantResult").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    //System.out.println("Hello");
                    Double lat=Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                    Double lon =Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                    String storename=dataSnapshot.child("visaStoreName").getValue().toString();
                    vb= (List<String>) dataSnapshot.child("cateogary").getValue();
                    String categorydesc=vb.get(0);
                    String distancedesc=dataSnapshot.child("distance").getValue().toString();
                    MerchsList.add(new MerchObject(lat,lon,storename,categorydesc,distancedesc));
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

//         response for containment zones
//        responseDB.child("ContainmentResult").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
//            {
//                if(dataSnapshot.exists())
//                {
//                    int numberOfNearbyZones = dataSnapshot.child("numberOfNearbyZones").getValue(Integer.class);
//                    if(numberOfNearbyZones>0) {
//                        for (DataSnapshot places : dataSnapshot.child("containmentZoneNames").getChildren())
//                        {
//                            String place = places.getValue(String.class);
//                            if(place!=null && !place.equals(""))
//                            {
//                                try {
//                                    Geocoder geocoder = new Geocoder(mContext,
//                                            Locale.getDefault());
//                                    List<Address> addresses = geocoder.getFromLocationName(
//                                            place,
//                                            1
//                                    );
//                                    Address address = addresses.get(0);
//                                    Log.i(TAG, "Got containment response :" + address.getLatitude() + " " + address.getLongitude() + " ");
//
//                                    containmentZoneList.add(new LocationObject(address.getLatitude(),
//                                            address.getLongitude(), place));
//
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            numberOfNearbyZones--;
//                            if(numberOfNearbyZones<=0)
//                            {
//                                gotResponseForContainmentZones = true;
//                                if(gotResponseForMerchs) {
//                                    // deleting the request after getting the response.
//                                    FirebaseDatabase.getInstance().getReference()
//                                            .child("NearbyATMRequest").child(newRequestDB.getKey()).removeValue();
//
//                                    Intent intent = new Intent("ACTION_FOUND_ATM_LIST");
//                                    mContext.sendBroadcast(intent);
//                                    //responseDB.removeEventListener(this);
//                                }
//                            }
//                        }
//                    }
//                    else
//                    {
//                        gotResponseForContainmentZones = true;
//                        if(gotResponseForMerchs) {
//                            // deleting the request after getting the response.
//                            FirebaseDatabase.getInstance().getReference()
//                                    .child("NearbyATMRequest").child(newRequestDB.getKey()).removeValue();
//
//                            Intent intent = new Intent("ACTION_FOUND_ATM_LIST");
//                            mContext.sendBroadcast(intent);
//                            //responseDB.removeEventListener(this);
//                        }
//                    }
//                }
//            }
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) { }
//        });
    }
}

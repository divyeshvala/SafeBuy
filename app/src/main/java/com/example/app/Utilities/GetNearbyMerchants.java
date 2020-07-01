package com.example.app.Utilities;

import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetNearbyMerchants
{
    private static final String TAG = "GetNearbyMerchants";
    private Context mContext;
    private ArrayList<MerchObject> merchantsList;
    private ArrayList<LocationObject> containmentZoneList;
    private String distance;
    private List<String> vb = new ArrayList<>();
    
    public GetNearbyMerchants(Context mContext, ArrayList<MerchObject> merchantsList,
                              ArrayList<LocationObject> containmentZoneList,
                              String distance) {
        this.mContext = mContext;
        this.merchantsList = merchantsList;
        this.containmentZoneList = containmentZoneList;
        this.distance = distance;
    }
    
    public void getListOfMerchants(String addressLine, String latitude, String longitude, List<String> categoryCodes, String distance, String distance_unit)
    {
        // send request
        final DatabaseReference newRequestDB = FirebaseDatabase.getInstance()
                .getReference().child("NearbyMerchantRequest").push();
        System.out.println("Inside getListofMerchants");
        System.out.println(categoryCodes);
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("merchantCategoryCode", categoryCodes);
        messageData.put("latitude", latitude);
        messageData.put("longitude", longitude);
        messageData.put("distance", distance);
        messageData.put("distanceUnit", distance_unit);
        messageData.put("placeName", addressLine);
        messageData.put("resolvedMerchant", "false");
        messageData.put("resolvedContainment", "false");
        merchantsList.clear();
        newRequestDB.updateChildren(messageData);
        Log.i(TAG, "newRequestDB.getKey() :"+newRequestDB.getKey());

        // get response
        final DatabaseReference responseDB = FirebaseDatabase.getInstance().getReference()
                .child("NearbyMerchantRequest").child(newRequestDB.getKey());

        responseDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String resolvedMerchant = dataSnapshot.child("resolvedMerchant").getValue(String.class);
                String resolvedContainment = dataSnapshot.child("resolvedContainment").getValue(String.class);

                Log.i(TAG, "Response : "+resolvedMerchant+" "+resolvedContainment);
                if( resolvedMerchant!=null && resolvedContainment!=null )
                {
                    if( resolvedMerchant.equals("success") && (!resolvedContainment.equals("false")))
                    {
                        Intent intent = new Intent("ACTION_FOUND_MERCHANTS_LIST");
                        intent.putExtra("status", "success");
                        mContext.sendBroadcast(intent);
                        responseDB.removeValue();

                    }
                    else if ( (resolvedMerchant.equals("failure")) && (!resolvedContainment.equals("false")))
                    {
                        Intent intent = new Intent("ACTION_FOUND_MERCHANTS_LIST");
                        intent.putExtra("status", "failed");
                        mContext.sendBroadcast(intent);
                        responseDB.removeValue();
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
                    double lat=Double.parseDouble(dataSnapshot.child("latitude").getValue(String.class));
                    double lon =Double.parseDouble(dataSnapshot.child("longitude").getValue(String.class));
                    
                    String storename=dataSnapshot.child("visaStoreName").getValue().toString();
                    vb= (List<String>) dataSnapshot.child("category").getValue();
                    String categorydesc = "";
                    if(vb.size()>0){
                        categorydesc = vb.get(0);
                    }
                    String distancedesc = dataSnapshot.child("distance").getValue(String.class);
                    merchantsList.add(new MerchObject(lat, lon, storename, categorydesc, distancedesc,
                            dataSnapshot.child("StoreId").getValue(String.class)));
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

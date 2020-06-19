package com.example.app.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.app.R;
import com.example.app.Utilities.PermissionUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MapsActivity";
    private static final String DISTANCE_RANGE_METERS = "5000"; // todo: change it based on user input
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    private boolean gotResponseForContainmentZones;
    private boolean gotResponseForATMs;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        progressBar = findViewById(R.id.id_progress_bar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        IntentFilter intentFilter = new IntentFilter("ADDRESS_FOUND");
        registerReceiver(locationReceiver, intentFilter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        enableMyLocation();

    }

    private void getListOfATMs(String addressLine, double latitude, double longitude)
    {
        gotResponseForContainmentZones = false;
        gotResponseForATMs = false;

        // send request
        final DatabaseReference newRequestDB = FirebaseDatabase.getInstance()
                .getReference().child("NearbyATMRequest").push();

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("distance", DISTANCE_RANGE_METERS);
        messageData.put("distanceUnit", "m");
        messageData.put("latitude", latitude);
        messageData.put("longitude", longitude);
        messageData.put("placeName", addressLine);
        newRequestDB.updateChildren(messageData);
        Log.i(TAG, "newRequestDB.getKey() :"+newRequestDB.getKey());

        // get response
        final DatabaseReference responseDB = FirebaseDatabase.getInstance().getReference()
                .child("NearbyATMRequest").child(newRequestDB.getKey());

        responseDB.child("ATMResult").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    Double latitude = dataSnapshot.child("coordinates").child("latitude").getValue(Double.class);
                    Double longitude = dataSnapshot.child("coordinates").child("longitude").getValue(Double.class);
                    String placeName = dataSnapshot.child("placeName").getValue(String.class);
                    Log.i(TAG, "Got response : "+latitude+" "+longitude+" "+placeName);
                    if(latitude==-360 && longitude==-360)  // means it was last response
                    {
                        gotResponseForATMs = true;
                        if(gotResponseForContainmentZones) {
                            // deleting the request after getting the response.
                            FirebaseDatabase.getInstance().getReference()
                                    .child("NearbyATMRequest").child(newRequestDB.getKey()).removeValue();

                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                    else{
                        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(placeName));
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
                                    Geocoder geocoder = new Geocoder(MapsActivity.this,
                                            Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocationName(
                                            place,
                                            1
                                    );
                                    Address address = addresses.get(0);
                                    Log.i(TAG, "Got containment response :" + address.getLatitude() + " " + address.getLongitude() + " ");

                                    mMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude()))
                                            .title(place)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            numberOfNearbyZones--;
                            if(numberOfNearbyZones<=0)
                            {
                                gotResponseForContainmentZones = true;
                                if(gotResponseForATMs) {
                                    // deleting the request after getting the response.
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("NearbyATMRequest").child(newRequestDB.getKey()).removeValue();

                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
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

    private void onLocationReceived(String address, double latitude, double longitude)
    {
        getListOfATMs(address, latitude, longitude);
    }

    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            double lat = intent.getDoubleExtra("latitude", 0);
            double lon = intent.getDoubleExtra("longitude", 0);
            onLocationReceived(intent.getStringExtra("addressLine"), lat, lon);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon),13));

            unregisterReceiver(locationReceiver); // todo :
        }
    };

    // Todo: try COARSE_LOCATION too
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(locationReceiver);
        }catch (Exception e){ e.printStackTrace(); }
    }
}

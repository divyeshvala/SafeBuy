package com.example.app.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.Messaging.Chat;
import com.example.app.R;
import com.example.app.Utilities.GetDirections;
import com.example.app.Utilities.PermissionUtils;
import com.example.app.model.LocationObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    private LatLng mOrigin, mDestination;
    private ArrayList<String> containmentZoneLatLngs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        String queryType = getIntent().getStringExtra("queryType");
        mOrigin = new LatLng(getIntent().getDoubleExtra("originLatitude", 0), getIntent().getDoubleExtra("originLongitude", 0));
        mDestination = new LatLng(getIntent().getDoubleExtra("destinationLatitude", 0), getIntent().getDoubleExtra("destinationLongitude", 0));
        containmentZoneLatLngs = getIntent().getStringArrayListExtra("containmentZoneLatLngs");

        TextView title = findViewById(R.id.textViewTitle);
        title.setText(queryType);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin,14));

        enableMyLocation();

        // todo: testing
        for(String object : containmentZoneLatLngs)
        {
            LatLng latLng = getLatLng(object);
            mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(50)     // containment zone radius.
                    .strokeWidth(0f)
                    .fillColor(0x83E53935));
        }

        GetDirections getDirections = new GetDirections(MapsActivity.this, mMap);
        getDirections.getDirectionsToThisLocation(mOrigin, mDestination);
    }

    private LatLng getLatLng(String object)
    {
        int i;
        for(i=0; i<object.length(); i++)
        {
            if(object.charAt(i)=='_')
                break;
        }
        return new LatLng(Double.parseDouble(object.substring(0, i)),
                Double.parseDouble(object.substring(i+1)));
    }

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
}

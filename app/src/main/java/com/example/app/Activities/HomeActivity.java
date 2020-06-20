package com.example.app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.app.R;
import com.example.app.Utilities.GetNearbyATMs;
import com.example.app.Utilities.LocationObject;
import com.example.app.Utilities.MyLocationListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int containmentZoneRadius = 1000;
    public ArrayList<LocationObject> ATMsList;
    public ArrayList<LocationObject> containmentZonesList;
    private GetNearbyATMs getNearbyATMs;
    private TextView safeList, unsafeList;
    private EditText addressText, radiusText;
    private Button searchBTN;
    private ProgressBar progressBar;

    private LocationObject mLocation;
    private boolean doesUserWantOurLocationsATMs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        safeList = findViewById(R.id.id_safeATMList);
        unsafeList = findViewById(R.id.id_unsafeATMList);
        progressBar = findViewById(R.id.id_progress_bar);

        addressText = findViewById(R.id.id_address);
        radiusText = findViewById(R.id.id_radius);
        searchBTN = findViewById(R.id.id_searchBTN);

        ATMsList = new ArrayList<>();
        containmentZonesList = new ArrayList<>();
        getNearbyATMs = new GetNearbyATMs(HomeActivity.this, ATMsList, containmentZonesList);

        mLocation = null;
        doesUserWantOurLocationsATMs = false;

        IntentFilter intentFilter1 = new IntentFilter("ACTION_FOUND_ATM_LIST");
        registerReceiver(ATMListReceiver, intentFilter1);

        IntentFilter intentFilter = new IntentFilter("ADDRESS_FOUND");
        registerReceiver(locationReceiver, intentFilter);
        setupLocationAPI();

        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                safeList.setText("Safe ATMs\n");
                unsafeList.setText("Unsafe ATMs\n");
                String radius = radiusText.getText().toString();
                if(radius.equals(""))
                    radius = "5000";
                if(addressText.getText().toString().equals("Your location"))
                {
                    progressBar.setVisibility(View.VISIBLE);
                    if(mLocation!=null)
                    {
                        getNearbyATMs.getListOfATMs(mLocation.getPlaceName(), mLocation.getLatitude(), mLocation.getLongitude());
                    }
                    else
                    {
                        doesUserWantOurLocationsATMs = true;
                    }
                }
                else if(addressText.getText().toString().equals("") || addressText.getText().toString().equals("Please enter your location"))
                {
                    addressText.setText("Please enter your location");
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    List<Address> addresses = null;
                    try {
                        Geocoder geocoder = new Geocoder(HomeActivity.this,
                                Locale.getDefault());
                        addresses = geocoder.getFromLocationName(
                                addressText.getText().toString(),
                                1
                        );
                        Address address = addresses.get(0);

                        Log.i(TAG, "address : "+address.getAddressLine(0)+"\n"+
                                address.getLatitude()+" "+address.getLongitude());

                        getNearbyATMs.getListOfATMs(address.getAddressLine(0), address.getLatitude(), address.getLongitude());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // todo:
        //getNearbyATMs.getListOfATMs("vile parle, mumbai", 19.0968, 72.8517);
    }

    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            double lat = intent.getDoubleExtra("latitude", 0);
            double lon = intent.getDoubleExtra("longitude", 0);
            String addressLine = intent.getStringExtra("addressLine");
            unregisterReceiver(locationReceiver);

            mLocation = new LocationObject(lat, lon, addressLine);

            if(doesUserWantOurLocationsATMs)
            {
                getNearbyATMs.getListOfATMs(addressLine, lat, lon);
                doesUserWantOurLocationsATMs = false;
            }

        }
    };

    private final BroadcastReceiver ATMListReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "inside ATMListReceiver ");
            findSafeAndUnsafeATMs();
        }
    };

    private void findSafeAndUnsafeATMs()
    {
        if(ATMsList.size()==0){
            unsafeList.setText(unsafeList.getText().toString()+"\nNo ATMs found");
            safeList.setText(unsafeList.getText().toString()+"\nNo ATMs found");
        }
        // check which ATMs are in safe area.
        for(LocationObject atmObject : ATMsList)
        {
            for(LocationObject zoneObject : containmentZonesList )
            {
                float distance = getDistance(atmObject.getLatitude(), atmObject.getLongitude(),
                        zoneObject.getLatitude(), zoneObject.getLongitude());

                if(distance<=containmentZoneRadius)  // in containment zone.
                {
                    unsafeList.setText(unsafeList.getText().toString()+"\n"+atmObject.getPlaceName());
                }
                else
                {
                    safeList.setText(safeList.getText().toString()+"\n"+atmObject.getPlaceName());
                }
            }
            if(containmentZonesList.size()==0)
                safeList.setText(safeList.getText().toString()+"\n"+atmObject.getPlaceName());
        }
        progressBar.setVisibility(View.INVISIBLE);
        unsafeList.setMovementMethod(new ScrollingMovementMethod());
        safeList.setMovementMethod(new ScrollingMovementMethod());
    }

    private float getDistance(double latitude, double longitude, double latitude1, double longitude1)
    {
        Location start = new Location("");
        start.setLatitude(latitude);
        start.setLongitude(longitude);
        Location dest = new Location("");
        dest.setLatitude(latitude1);
        dest.setLongitude(longitude1);

        return start.distanceTo(dest);
    }

    private void setupLocationAPI()
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener(HomeActivity.this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
            }
        }
        else
        {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            unregisterReceiver(locationReceiver);
            unregisterReceiver(ATMListReceiver);
        }catch (Exception e){ e.printStackTrace(); }
    }
}

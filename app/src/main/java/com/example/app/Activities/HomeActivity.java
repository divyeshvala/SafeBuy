package com.example.app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.example.app.R;
import com.example.app.Utilities.GetNearbyATMs;
import com.example.app.Utilities.LocationObject;
import com.example.app.Utilities.MyLocationListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int containmentZoneRadius = 1000;

    public ArrayList<LocationObject> ATMsList;
    public ArrayList<LocationObject> containmentZonesList;
    private GetNearbyATMs getNearbyATMs;

    private TextView safeList, unsafeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        safeList = findViewById(R.id.id_safeATMList);
        unsafeList = findViewById(R.id.id_unsafeATMList);

        ATMsList = new ArrayList<>();
        containmentZonesList = new ArrayList<>();
        getNearbyATMs = new GetNearbyATMs(HomeActivity.this, ATMsList, containmentZonesList);

        IntentFilter intentFilter1 = new IntentFilter("ACTION_FOUND_ATM_LIST");
        registerReceiver(ATMListReceiver, intentFilter1);

        IntentFilter intentFilter = new IntentFilter("ADDRESS_FOUND");
        registerReceiver(locationReceiver, intentFilter);
        setupLocationAPI();

        // todo
        //getNearbyATMs.getListOfATMs("vile parle, mumbai", 19.0968, 72.8517);
    }

    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            double lat = intent.getDoubleExtra("latitude", 0);
            double lon = intent.getDoubleExtra("longitude", 0);
            String addressLine = intent.getStringExtra("addressLine");
            unregisterReceiver(locationReceiver); // todo :

            getNearbyATMs.getListOfATMs(addressLine, lat, lon);
        }
    };

    private final BroadcastReceiver ATMListReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            findSafeAndUnsafeATMs();
        }
    };

    private void findSafeAndUnsafeATMs()
    {
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
        }
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
                        LocationManager.GPS_PROVIDER, 30000, 2, locationListener);
            }
        }
        else
        {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 300000, 2, locationListener);
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

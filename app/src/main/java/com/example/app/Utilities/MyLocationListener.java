package com.example.app.Utilities;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/*  Todo : To be included in Activity
LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
LocationListener locationListener = new MyLocationListener();

if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
{
    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 120000, 20, locationListener);
    }
}
else
{
    locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 120000, 20, locationListener);
}
 */

public class MyLocationListener implements LocationListener {

    private static final String TAG = "MyLocationListener";
    @Override
    public void onLocationChanged(Location location)
    {
        Log.i(TAG, "inside onLocationChanged");
        // send broadcast to Activity.
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "Provider disabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "Provider enabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.i(TAG, "inside onStatusChanged :"+status);
    }
}

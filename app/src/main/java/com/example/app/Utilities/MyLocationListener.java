package com.example.app.Utilities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyLocationListener implements LocationListener {

    private static final String TAG = "MyLocationListener";

    private Context mContext;
    public MyLocationListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.i(TAG, "inside onLocationChanged");
        // send broadcast to Activity.
        getAddress(location);
    }

    @Override
    public void onProviderDisabled(String provider) { Log.i(TAG, "Provider disabled"); }

    @Override
    public void onProviderEnabled(String provider) { Log.i(TAG, "Provider enabled"); }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { Log.i(TAG, "inside onStatusChanged :"+status); }

    private void getAddress(Location location)
    {
        String addressLine = "";
        try {
            Geocoder geocoder = new Geocoder(mContext,
                    Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(),
                    1
            );

            Address address = addresses.get(0);
            addressLine = address.getAddressLine(0);

            Log.i(TAG, addressLine);

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        Intent intent = new Intent("ADDRESS_FOUND");
        intent.putExtra("addressLine", addressLine);
        mContext.sendBroadcast(intent);
    }
}

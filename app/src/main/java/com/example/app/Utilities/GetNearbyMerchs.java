package com.example.app.Utilities;

import android.content.Context;

import com.example.app.model.LocationObject;

import java.util.ArrayList;

public class GetNearbyMerchs {
    private static final String DISTANCE_RANGE_METERS = "5000";
    private static final String TAG = "GetNearbyMerchs";
    private boolean gotResponseForContainmentZones;
    private boolean gotResponseForMerchs;
    private Context mContext;
    private ArrayList<LocationObject> MerchsList;
    private ArrayList<LocationObject> containmentZoneList;
    public GetNearbyMerchs(Context mContext, ArrayList<LocationObject> MerchsList, ArrayList<LocationObject> containmentZoneList) {
        this.mContext = mContext;
        this.MerchsList = MerchsList;
        this.containmentZoneList = containmentZoneList;
    }
}

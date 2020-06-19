package com.example.app.Utilities;

public class ATMObject
{
    private long latitude;
    private long longitude;
    private String placeName ;

    public ATMObject(long latitude, long longitude, String placeName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeName = placeName;
    }

    public long getLatitude() {
        return latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public String getPlaceName() {
        return placeName;
    }
}

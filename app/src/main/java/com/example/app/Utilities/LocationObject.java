package com.example.app.Utilities;

public class LocationObject {

    private double latitude;
    private double longitude;
    private String placeName;

    public LocationObject(double latitude, double longitude, String placeName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeName = placeName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPlaceName() {
        return placeName;
    }
}

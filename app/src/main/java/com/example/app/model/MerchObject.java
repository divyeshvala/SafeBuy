package com.example.app.model;

public class MerchObject {
    private double lat;
    private double lon;
    private String storename;
    private String categorydesc;
    private String distancedesc;

    public MerchObject(double lat, double lon, String storename, String categorydesc, String distancedesc){
        this.lat=lat;
        this.lon=lon;
        this.storename=storename;
        this.categorydesc=categorydesc;
        this.distancedesc=distancedesc;
    }
    public double getLat(){return lat;}
    public double getLon(){return lon;}
    public String getStoreName(){return storename;}
    public String getCategoryDesc(){return categorydesc;}
    public String getDistanceDesc(){return distancedesc;}

}

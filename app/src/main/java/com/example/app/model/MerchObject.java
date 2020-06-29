package com.example.app.model;

public class MerchObject
{
    private String merchantId;
    private double lat;
    private double lon;
    private String storename;
    private String categorydesc;
    private String distancedesc;
    private String address;

    public MerchObject(double lat, double lon, String storename, String categorydesc, String distancedesc, String merchantId){
        this.lat=lat;
        this.lon=lon;
        this.storename=storename;
        this.categorydesc=categorydesc;
        this.distancedesc=distancedesc;
        this.merchantId = merchantId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat(){return lat;}
    public double getLon(){return lon;}
    public String getStoreName(){return storename;}
    public String getCategoryDesc(){return categorydesc;}
    public String getDistanceDesc(){return distancedesc;}

    public String getMerchantId() {
        return merchantId;
    }
}

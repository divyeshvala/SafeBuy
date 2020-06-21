package com.example.app.model;

public class Merchant {
    String name;
    String address;
    boolean open;
    String distance;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Merchant(String name, String address, boolean open, String distance) {
        this.name = name;
        this.address = address;
        this.open = open;
        this.distance = distance;
    }
}

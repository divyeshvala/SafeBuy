package com.example.app.model;

public class Order 
{
    private String customerUid;
    private String customerName;
    private String customerPhone;
    private String customerAddress;

    public void setCustomerUid(String customerUid) {
        this.customerUid = customerUid;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerUid() {
        return customerUid;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }
}

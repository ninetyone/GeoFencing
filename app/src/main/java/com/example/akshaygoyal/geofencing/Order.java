package com.example.akshaygoyal.geofencing;

/**
 * Created by akshaygoyal on 9/25/15.
 */
public class Order {

    private String orderNumber;
    private float orderAmount;

    public Order(String orderNumber, float orderAmount) {
        this.orderNumber = orderNumber;
        this.orderAmount = orderAmount;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setOrderAmount(float orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public float getOrderAmount() {
        return orderAmount;
    }


}

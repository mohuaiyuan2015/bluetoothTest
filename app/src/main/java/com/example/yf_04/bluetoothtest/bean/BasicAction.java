package com.example.yf_04.bluetoothtest.bean;

/**
 * Created by YF-04 on 2017/7/31.
 */

public class BasicAction {
    private int id;
    private String  name;
    private String  textId;
    private String orderId;

    public BasicAction(){

    }
    public BasicAction(int id){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTextId() {
        return textId;
    }

    public void setTextId(String textId) {
        this.textId = textId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

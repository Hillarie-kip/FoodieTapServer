package com.techkip.foodietapserver.model;

import java.util.List;

/**
 * Created by hillarie on 29/05/2018.
 */

public class Request {
    private String phone,name,address,total,status,comment;
    private List<Order> foods;

    public Request() {
    }

    public Request(String phone, String name, String address, String total,  String comment,List<Order> foods) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = "0"; //0 is placed 1 shipping 3 shipped
        this.comment = comment; //0 is placed 1 shipping 3 shipped
        this.foods = foods;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}

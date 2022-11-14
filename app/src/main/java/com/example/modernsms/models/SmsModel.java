package com.example.modernsms.models;

public class SmsModel {
    private String address;
    private String body;
    private String Date;
    private String time;
    private String day;
    private String displayDate;
    private int type;

    public SmsModel(String address, String body, String date, String time) {
        this.address = address;
        this.body = body;
        Date = date;
        this.time = time;
    }

    public SmsModel(String address, String body) {
        this.address = address;
        this.body = body;
    }

    public SmsModel(String address, String body, String date, String time, int type) {
        this.address = address;
        this.body = body;
        Date = date;
        this.time = time;
        this.type = type;
    }

    public SmsModel(String address, String body, String date, String time, String day, int type) {
        this.address = address;
        this.body = body;
        Date = date;
        this.time = time;
        this.day = day;
        this.type = type;
    }

    public SmsModel(String address, String body, String date, String time, String day, String displayDate, int type) {
        this.address = address;
        this.body = body;
        Date = date;
        this.time = time;
        this.day = day;
        this.displayDate = displayDate;
        this.type = type;
    }

    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

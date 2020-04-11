package com.example.yourweather;

public class LocalResponse {
    String city;
    String temp;
    String status;
    String timestamp;

    public LocalResponse(String city, String temp, String status, String timestamp) {
        this.city = city;
        this.temp = temp;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

package com.example.magisterka;

public class Acc_value {
    private String currentTime, currentDate;
    private float value;

    public Acc_value(String currentTime, String currentDate, float value) {
        this.currentTime = currentTime;
        this.value = value;
        this.currentDate = currentDate;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public float getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

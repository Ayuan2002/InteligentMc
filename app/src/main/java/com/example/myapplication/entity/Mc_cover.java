package com.example.myapplication.entity;

public class Mc_cover {
    private String ID;
    private char cover_state;
    private float power_state;
    private char open_state;
    private float waterPosition_state;
    private float temperature_state;
    private float moisture_state;
    private float gas_state;

    public Mc_cover(String ID, char cover_state, float power_state, char open_state, float waterPosition_state, float temperature_state, float moisture_state, float gas_state, double latitude, double longitude) {
        this.ID = ID;
        this.cover_state = cover_state;
        this.power_state = power_state;
        this.open_state = open_state;
        this.waterPosition_state = waterPosition_state;
        this.temperature_state = temperature_state;
        this.moisture_state = moisture_state;
        this.gas_state = gas_state;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public char getCover_state() {
        return cover_state;
    }

    public void setCover_state(char cover_state) {
        this.cover_state = cover_state;
    }

    public float getPower_state() {
        return power_state;
    }

    public void setPower_state(float power_state) {
        this.power_state = power_state;
    }

    public char getOpen_state() {
        return open_state;
    }

    public void setOpen_state(char open_state) {
        this.open_state = open_state;
    }

    public float getWaterPosition_state() {
        return waterPosition_state;
    }

    public void setWaterPosition_state(float waterPosition_state) {
        this.waterPosition_state = waterPosition_state;
    }

    public float getTemperature_state() {
        return temperature_state;
    }

    public void setTemperature_state(float temperature_state) {
        this.temperature_state = temperature_state;
    }

    public float getMoisture_state() {
        return moisture_state;
    }

    public void setMoisture_state(float moisture_state) {
        this.moisture_state = moisture_state;
    }

    public float getGas_state() {
        return gas_state;
    }

    public void setGas_state(float gas_state) {
        this.gas_state = gas_state;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double latitude;
    private double longitude;
}

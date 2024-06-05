package com.example.myapplication.entity;

public class Mc_cover {
    private String ID;
    private double longitude;
    private double latitude;
    private double water_level;
    private double harmful_gas_concentration;
    private double pitch_angle;
    private double roll_angle;
    private double yaw_angle;

    public Mc_cover(String ID, double longitude, double latitude, double water_level, double harmful_gas_concentration, double pitch_angle, double roll_angle, double yaw_angle) {
        this.ID = ID;
        this.longitude = longitude;
        this.latitude = latitude;
        this.water_level = water_level;
        this.harmful_gas_concentration = harmful_gas_concentration;
        this.pitch_angle = pitch_angle;
        this.roll_angle = roll_angle;
        this.yaw_angle = yaw_angle;
    }

    public Mc_cover() {

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getWater_level() {
        return water_level;
    }

    public void setWater_level(double water_level) {
        this.water_level = water_level;
    }

    public double getHarmful_gas_concentration() {
        return harmful_gas_concentration;
    }

    public void setHarmful_gas_concentration(double harmful_gas_concentration) {
        this.harmful_gas_concentration = harmful_gas_concentration;
    }

    public double getPitch_angle() {
        return pitch_angle;
    }

    public void setPitch_angle(double pitch_angle) {
        this.pitch_angle = pitch_angle;
    }

    public double getRoll_angle() {
        return roll_angle;
    }

    public void setRoll_angle(double roll_angle) {
        this.roll_angle = roll_angle;
    }

    public double getYaw_angle() {
        return yaw_angle;
    }

    public void setYaw_angle(double yaw_angle) {
        this.yaw_angle = yaw_angle;
    }
}
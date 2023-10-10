package com.example.myapplication.entity;
public class Manhole {
    private String mcId;
    private float mpvalue;
    private float yaww;
    private float pitchh;
    private float rolll;
    private double latitude;
    private double longitude;

    public Manhole() {
    }

    public Manhole(String mcId, float mpvalue, float yaww, float pitchh, float rolll, double latitude, double longitude) {
        this.mcId = mcId;
        this.mpvalue = mpvalue;
        this.yaww = yaww;
        this.pitchh = pitchh;
        this.rolll = rolll;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setMcid(String deviceName){
        this.mcId=deviceName;
    }
    public String getMcId() {
        return mcId;
    }

    public void setMcId(String mcId) {
        this.mcId = mcId;
    }

    public float getMpvalue() {
        return mpvalue;
    }

    public void setMpvalue(float mpvalue) {
        this.mpvalue = mpvalue;
    }

    public float getYaww() {
        return yaww;
    }

    public void setYaww(float yaww) {
        this.yaww = yaww;
    }

    public float getPitchh() {
        return pitchh;
    }

    public void setPitchh(float pitchh) {
        this.pitchh = pitchh;
    }

    public float getRolll() {
        return rolll;
    }

    public void setRolll(float rolll) {
        this.rolll = rolll;
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
}
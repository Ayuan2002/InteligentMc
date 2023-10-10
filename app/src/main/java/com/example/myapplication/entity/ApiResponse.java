package com.example.myapplication.entity;

import java.util.List;

public class ApiResponse {
    private int statusCode;
    private String message;
    private List<Manhole> data;

    public ApiResponse() {
    }

    public ApiResponse(int statusCode, String message, List<Manhole> data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Manhole> getData() {
        return data;
    }

    public void setData(List<Manhole> data) {
        this.data = data;
    }
}

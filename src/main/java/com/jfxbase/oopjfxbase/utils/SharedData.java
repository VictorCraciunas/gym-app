package com.jfxbase.oopjfxbase.utils;

// with this class we share different variables throughout application
public class SharedData {
    private static SharedData instance;
    private int data;
    private int month;


    private SharedData() {
        // Private constructor to prevent instantiation
    }

    public static synchronized SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}


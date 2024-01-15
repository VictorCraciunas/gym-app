package com.jfxbase.oopjfxbase.utils;

public class Admin extends User{
    private static Admin instance;

    public Admin(){
        super.password="";
        super.username="";
        super.userId=100;
    }

    public static synchronized Admin getInstance() {
        if (instance == null) {
            instance = new Admin();
        }
        return instance;
    }
}

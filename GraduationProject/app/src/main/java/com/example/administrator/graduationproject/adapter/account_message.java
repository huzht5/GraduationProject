package com.example.administrator.graduationproject.adapter;

/**
 * Created by Administrator on 2019-2-21.
 */

public class account_message {
    public String name;
    public String id;
    public String time;
    public String message;
    public String state;

    public account_message(String name, String id, String time, String message, String state){
        this.name = name;
        this.id = id;
        this.time = time;
        this.message = message;
        this.state = state;
    }
}

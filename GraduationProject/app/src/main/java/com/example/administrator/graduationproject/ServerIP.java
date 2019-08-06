package com.example.administrator.graduationproject;

/**
 * Created by Administrator on 2019-1-23.
 */
//这个类用于设置和提供服务器IP地址，便于修改
public class ServerIP {
    static String ip;
    public ServerIP(){
        ip = "172.18.159.228";
    }

    public String getIp(){
        return ip;
    }
}

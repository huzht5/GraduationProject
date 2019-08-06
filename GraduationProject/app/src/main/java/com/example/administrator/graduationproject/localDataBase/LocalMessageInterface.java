package com.example.administrator.graduationproject.localDataBase;

import android.content.Context;

import com.example.administrator.graduationproject.adapter.account_message;
import com.example.administrator.graduationproject.adapter.message_data;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019-2-21.
 */
//这个类是本地聊天记录数据库管理的接口类
public class LocalMessageInterface {
    static LocalMessageManager local_message;

    public LocalMessageInterface(Context context){
        local_message=new LocalMessageManager(context);
    }

    //在table里增加一项
    public void AddMessage(String account, String friend, String name, String sendorget, String time, String content, String state){
        local_message.AddMessage(account, friend, name, sendorget, time, content, state);
    }

    //获取消息列表
    public ArrayList<account_message> getAccountList(String account){
        return local_message.getAccountList(account);
    }

    //删除对话
    public void deleteDialog(String account, String friend){
        local_message.deleteDialog(account, friend);
    }

    //获取消息记录
    public ArrayList<message_data> getMessageList(String account, String friend){
        return local_message.getMessageList(account, friend);
    }

    //修改已读状态
    public void changeState(String account, String friend, String state){
        local_message.changeState(account, friend, state);
    }

    //判断是否全部已读
    public boolean isAllread(String account){
        return local_message.isAllread(account);
    }
}

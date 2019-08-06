package com.example.administrator.graduationproject.localDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.graduationproject.adapter.account_message;
import com.example.administrator.graduationproject.adapter.message_data;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019-2-21.
 */
//这个类用于管理本地聊天记录数据库
public class LocalMessageManager {
    private LocalMessageDataBase dbHelper;

    public LocalMessageManager(Context context){
        dbHelper = new LocalMessageDataBase(context,null,1);
    }

    //在table里增加一项
    public void AddMessage(String account, String friend, String name, String sendorget, String time, String content, String state){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //组装数据
        String rid = account + friend;
        values.put("rid", rid);
        values.put("account", account);
        values.put("friend", friend);
        values.put("name", name);
        values.put("sendorget", sendorget);
        values.put("time", time);
        values.put("content", content);
        values.put("state", state);
        //插入数据
        db.insert("message", null, values);
        values.clear();
    }

    //获取消息列表
    public ArrayList<account_message> getAccountList(String account){
        ArrayList<account_message> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("message", null, "account = ?", new String[]{account}, null, null, "time desc", null);
        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String id = cursor.getString(cursor.getColumnIndex("friend"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String state = cursor.getString(cursor.getColumnIndex("state"));
            boolean flag = true;
            for (int i = 0; i < list.size(); i++){
                if (id.equals(list.get(i).id)){
                    flag = false;
                    break;
                }
            }
            if (flag){
                account_message am = new account_message(name, id, time, content, state);
                list.add(am);
            }
        }
        cursor.close();
        return list;
    }

    //删除对话
    public void deleteDialog(String account, String friend){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String rid = account + friend;
        db.delete("message", "rid = ?", new String[]{rid});
    }

    //获取消息记录
    public ArrayList<message_data> getMessageList(String account, String friend){
        ArrayList<message_data> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String rid = account + friend;
        Cursor cursor = db.query("message", null, "rid = ?", new String[]{rid}, null, null, "time", null);
        while (cursor.moveToNext()){
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String sendorget = cursor.getString(cursor.getColumnIndex("sendorget"));
            message_data am = new message_data(time, content, sendorget);
            list.add(am);
        }
        cursor.close();
        return list;
    }

    //修改已读状态
    public void changeState(String account, String friend, String state){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String rid = account + friend;
        String []args = {rid};
        ContentValues values=new ContentValues();
        values.put("state", state);
        db.update("message", values, "rid = ?", args);
    }

    //判断是否全部已读
    public boolean isAllread(String account){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("message", null, "account = ?", new String[]{account}, null, null, null, null);
        while (cursor.moveToNext()){
            String state = cursor.getString(cursor.getColumnIndex("state"));
            if (state.equals("1")) return false;
        }
        cursor.close();
        return true;
    }
}

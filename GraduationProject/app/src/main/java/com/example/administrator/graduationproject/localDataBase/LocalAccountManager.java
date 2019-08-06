package com.example.administrator.graduationproject.localDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2019-1-22.
 */
//这个类用于管理本地账号数据库
public class LocalAccountManager {
    private LocalAccountDataBase dbHelper;

    public LocalAccountManager(Context context){
        dbHelper = new LocalAccountDataBase(context,null,1);
    }

    //在table里增加一项
    public void AddAccount(String account,int state){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //组装数据
        values.put("account",account);
        values.put("state", state);
        values.put("message", 0);
        //插入数据
        db.insert("account", null, values);
        values.clear();
    }

    //查找本地处于登录状态的账号，若有则返回账号字符串，若没有则返回空字符串
    public String GetTheAccount(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("account", new String[]{"account"}, "state = ?", new String[]{"1"}, null, null, null, null);
        int a = cursor.getCount();
        if (cursor.getCount() == 0) {
            cursor.close();
            return "";
        }
        else {
            String account = "";
            while (cursor.moveToNext()){
                account = cursor.getString(cursor.getColumnIndex("account"));
            }
            cursor.close();
            return account;
        }
    }

    //清空本地账号数据库
    public void deleteAllAccounts(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("account", null, null);
    }

    //改变某个账号的状态，如果table中没有这个账号，则增加一项并把state设置为1。参数分别为：账号，状态
    public void ChangeState(String account,int state){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String []args = {account};
        Cursor cursor = db.query("account", null, "account = ?", args, null, null, null, null);
        if (cursor.getCount() == 0) {
            AddAccount(account,1);
            cursor.close();
        }
        else{
            ContentValues values=new ContentValues();
            values.put("state", state);
            db.update("account", values, "account = ?", args);
        }
        cursor.close();
    }

    //改变某个账号的消息标志。参数分别为：账号，标志
    public void setMessage(String account,int message){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String []args = {account};
        ContentValues values=new ContentValues();
        values.put("message", message);
        db.update("account", values, "account = ?", args);
    }

    //获取消息标志位
    public int getMessage(String account){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String []args = {account};
        Cursor cursor = db.query("account", null, "account = ?", args, null, null, null, null);
        int message = 0;
        while (cursor.moveToNext()){
            message = cursor.getInt(cursor.getColumnIndex("state"));
        }
        cursor.close();
        return message;
    }
}



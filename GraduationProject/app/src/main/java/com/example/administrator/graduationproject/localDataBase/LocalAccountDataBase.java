package com.example.administrator.graduationproject.localDataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2019-1-22.
 */
//这个类用于在本地储存登录过的账号
public class LocalAccountDataBase extends SQLiteOpenHelper {
    private Context myContent;
    static String TABLE_NAME="account";

    //table里的每一行有三列：第一列是account，即账号，用作主键；第二列是state，即状态，用于标记账号是否处于登录状态，1表示已登录，0表示未登录；第三列用来标记消息界面是否要更改，1表示要，0表示不用
    public static final String CREATE_BOOK = "CREATE TABLE "+TABLE_NAME+" ("
            + "account text PRIMARY KEY,"
            + "state integer ,"
            + "message integer)";
    /**
     * integer：整形
     * real：浮点型
     * text：文本类型
     * blob：二进制类型
     * PRIMARY KEY将id列设置为主键
     * AutoIncrement关键字表示id列是自动增长的
     */

    public LocalAccountDataBase(Context context, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, TABLE_NAME, factory, version);
        myContent = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建数据库的同时创建Book表
        sqLiteDatabase.execSQL(CREATE_BOOK);
        Log.e(TAG, "成功创建了"+TABLE_NAME);
        //提示数据库创建成功
        //Toast.makeText(myContent, "数据库创建成功", Toast.LENGTH_SHORT).show();
    }
}


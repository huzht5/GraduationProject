package com.example.administrator.graduationproject.localDataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2019-2-21.
 */
//这个类用于在本地储存聊天记录
public class LocalMessageDataBase extends SQLiteOpenHelper {
    private Context myContent;
    static String TABLE_NAME="message";

    //table里的每一行有五列：第1列是唯一标识，第2列是我的账号，第3列是对方的账号；第4列是对方的用户昵称；第5列是标志发送还是接收；第6列是消息时间；第7列是消息内容；第8列标志是否已读
    public static final String CREATE_BOOK = "CREATE TABLE "+TABLE_NAME+" ("
            + "rid text ,"
            + "account text ,"
            + "friend text ,"
            + "name text ,"
            + "sendorget text ,"
            + "time text ,"
            + "content text ,"
            + "state text)";
    /**
     * integer：整形
     * real：浮点型
     * text：文本类型
     * blob：二进制类型
     * PRIMARY KEY将id列设置为主键
     * AutoIncrement关键字表示id列是自动增长的
     */

    public LocalMessageDataBase(Context context, SQLiteDatabase.CursorFactory factory, int version) {
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

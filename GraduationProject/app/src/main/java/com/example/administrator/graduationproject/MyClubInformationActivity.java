package com.example.administrator.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019-2-13.
 */
//查看我的社团详情界面
public class MyClubInformationActivity extends AppCompatActivity {
    private ServerIP sIP;    //用于获取服务器的IP地址
    private TextView information;    //社团简介

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_club_information);
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        Bundle bundle = getIntent().getExtras();
        String club_name =bundle.getString("club_name");    //获取上一个页面传递的参数，即社团名
        String account =bundle.getString("account");    //获取上一个页面传递的参数，即账号
        Button change_button=(Button) findViewById(R.id.change_button);    //获取修改社团简介按钮
        Button club_blog_button=(Button) findViewById(R.id.club_blog_button);    //获取查看社团公告按钮
        Button create_blog_button=(Button) findViewById(R.id.create_blog_button);    //获取发布社团公告按钮
        Button activity_sign_button=(Button) findViewById(R.id.activity_sign_button);    //获取查看签到情况按钮
        Button create_activity_button=(Button) findViewById(R.id.create_activity_button);    //获取发布签到活动按钮
        information = (TextView) findViewById(R.id.information);
        //设置监听器
        setListeners(change_button, club_blog_button, create_blog_button, activity_sign_button, create_activity_button, club_name);
        getClubInformation(ip, club_name, account);    //获取社团信息
    }

    //获取社团信息。参数分别为：服务器ip地址，社团名
    public void getClubInformation(String ip, String club_name, String account){
        TextView txt_club_name = (TextView) findViewById(R.id.club_name);
        txt_club_name.setText(club_name);
        //向请求体中插入数据：社团名
        Map<String, String> params = new HashMap<String, String>();
        params.put("name",club_name);
        params.put("followerid",account);
        String content = getRequestData(params, "UTF-8").toString();
        String murl = "http://" + ip + ":8000/query/get_club_information/";
        AsynNetUtils.post(murl, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                String[] strs = response.split("/");
                information.setText(strs[0]);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (requestCode == 1) {
                Bundle bundle = data.getExtras();
                String txt_information =bundle.getString("information");    //获取回传的参数
                information.setText(txt_information);
            }
        }
    }

    //设置监听器
    public void setListeners(Button change_button, Button club_blog_button, Button create_blog_button, Button activity_sign_button, Button create_activity_button, String club_name){
        setChangeButtonListener(change_button, club_name);
        setClubBlogButtonListener(club_blog_button);
        setCreateBlogButtonListener(create_blog_button);
        setActivitySignButtonListener(activity_sign_button);
        setCreateActivityButtonListener(create_activity_button);
    }

    //设置修改社团简介按钮的监听器
    public void setChangeButtonListener(Button change_button, final String club_name){
        change_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle=new Bundle();
                TextView txt_information = (TextView) findViewById(R.id.information);
                String information = txt_information.getText().toString();
                bundle.putString("club_name", club_name);
                bundle.putString("information", information);
                intent.setClass(MyClubInformationActivity.this,ChangeMyClubInformationActivity.class);    //跳转到修改社团简介界面
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
            }
        });
    }

    //设置查看社团公告按钮的监听器
    public void setClubBlogButtonListener(Button club_blog_button){
        club_blog_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = getIntent().getExtras();
                intent.setClass(MyClubInformationActivity.this,MyClubBlogActivity.class);    //跳转到查看社团公告界面
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //设置发布社团公告按钮的监听器
    public void setCreateBlogButtonListener(Button create_blog_button){
        create_blog_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = getIntent().getExtras();
                intent.setClass(MyClubInformationActivity.this,CreateClubBlogActivity.class);    //跳转到发布社团公告界面
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //设置查看签到活动按钮的监听器
    public void setActivitySignButtonListener(Button activity_sign_button){
        activity_sign_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = getIntent().getExtras();
                intent.setClass(MyClubInformationActivity.this,MyClubSignInActivity.class);    //跳转到查看签到活动界面
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //设置发布签到活动按钮的监听器
    public void setCreateActivityButtonListener(Button create_activity_button){
        create_activity_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = getIntent().getExtras();
                intent.setClass(MyClubInformationActivity.this,CreateClubSignInActivity.class);    //跳转到发布签到活动界面
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //构造请求体
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }
}

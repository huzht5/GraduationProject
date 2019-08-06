package com.example.administrator.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.graduationproject.adapter.club_blog;
import com.example.administrator.graduationproject.adapter.club_blog_adapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019-2-1.
 */
//社团详情界面
public class ClubInformationActivity extends AppCompatActivity {
    private ServerIP sIP;    //用于获取服务器的IP地址
    private int follow;    //用于标识是否已关注此用户，0表示未关注，1表示关注
    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private club_blog_adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_information);    //加载社团详情界面
        Bundle bundle= this.getIntent().getExtras();
        String account = bundle.getString("account");    //获取上一个页面传递的参数，即账号
        String club_name = bundle.getString("club_name");    //获取上一个页面传递的参数，即社团名字
        String owner_name = bundle.getString("owner_name");    //获取上一个页面传递的参数，即社长名字
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        TextView txt_owner_name = (TextView) findViewById(R.id.owner);
        txt_owner_name.setText(owner_name);    //设置社长名字文本内容
        TextView txt_name = (TextView) findViewById(R.id.name);
        txt_name.setText(club_name);    //设置社团名文本内容
        Button sign_button=(Button) findViewById(R.id.sign_button);    //获取签到按钮
        Button follow_button=(Button) findViewById(R.id.follow_button);    //获取关注/取消关注按钮
        mLayoutManager = new LinearLayoutManager(ClubInformationActivity.this, LinearLayoutManager.VERTICAL, false);
        setListeners(sign_button, follow_button, txt_owner_name, account, club_name, ip);    //设置监听器
        getInformation(account, club_name, ip, follow_button);    //获取社团信息
        initdata(club_name, ip);    //加载社团公告
    }

    //设置监听器。参数分别为：签到按钮，关注/取消关注按钮，账号，社团名，服务器ip地址
    public void setListeners(Button sign_button, final Button follow_button, TextView txt_owner_name, final String account, final String club_name, final String ip){
        //设置签到按钮的监听器
        sign_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("account", account);
                bundle.putString("club_name", club_name);
                intent.setClass(ClubInformationActivity.this,SignInActivity.class);    //跳转到签到界面
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //设置关注/取消关注按钮的监听器
        follow_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (follow == 0) {
                    Follow(account, club_name, ip);
                    follow_button.setText("取消关注");
                }
                else if (follow == 1) {
                    cancelFollow(account, club_name, ip);
                    follow_button.setText("关注");
                }
            }
        });
        //设置社长名字文本的监听器
        txt_owner_name.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                Bundle bundle1= getIntent().getExtras();
                String owner_id = bundle1.getString("owner_id");    //获取上一个页面传递的参数，即社长账号
                Bundle bundle = new Bundle();
                bundle.putString("account", account);    //向下一个页面传递参数：我的账号
                bundle.putString("account_id", owner_id);    //向下一个页面传递参数：对方的账号
                intent.setClass(ClubInformationActivity.this,AccountInformationActivity.class);    //跳转到用户详情界面
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //关注。参数分别为：账号，社团名，服务器ip地址
    private void Follow(String account, String club_name, String ip){
        //向请求体中插入数据：账号和社团名
        Map<String, String> params = new HashMap<String, String>();
        params.put("followerid",account);
        params.put("name",club_name);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/insert/follow_club/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                if (response.equals("followclub")) {
                    Toast.makeText(ClubInformationActivity.this, "关注成功!", Toast.LENGTH_SHORT).show();
                    follow = 1;    //标志已关注
                }
            }
        });
    }

    //取消关注。参数分别为：账号，社团名，服务器ip地址
    private void cancelFollow(String account, String club_name, String ip){
        //向请求体中插入数据：账号和社团名
        Map<String, String> params = new HashMap<String, String>();
        params.put("followerid",account);
        params.put("name",club_name);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/delete/cancel_follow_club/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                if (response.equals("cancelfollowclub")) {
                    Toast.makeText(ClubInformationActivity.this, "取消关注成功!", Toast.LENGTH_SHORT).show();
                    follow = 0;    //标志未关注
                }
            }
        });
    }

    //获取用户信息。参数分别为：账号，社团名，服务器ip地址，关注/取消关注按钮
    public void getInformation(String account, String club_name, String ip, final Button follow_button){
        //向请求体中插入数据：账号和社团名
        Map<String, String> params = new HashMap<String, String>();
        params.put("followerid",account);
        params.put("name",club_name);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/query/get_club_information/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                String[] strs = response.split("/");
                TextView txt_information = (TextView) findViewById(R.id.information);    //设置社团简介文本内容
                txt_information.setText(strs[0]);
                follow = Integer.parseInt(strs[1]);    //获取是否已关注
                if (follow == 0) {
                    follow_button.setText("关注");    //修改按钮文本
                }
                else if (follow == 1) {
                    follow_button.setText("取消关注");    //修改按钮文本
                }
            }
        });
    }

    //加载社团公告。参数分别为：社团名，服务器ip地址
    public void initdata(String club_name, String ip){
        //向请求体中插入数据：社团名
        Map<String, String> params = new HashMap<String, String>();
        params.put("name",club_name);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/query/get_club_blog/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取社团公告的ArrayList
                ArrayList<club_blog> flist = analysisArray(response, club_blog.class);
                mAdapter = new club_blog_adapter(flist);
                initview();    //加载界面
            }
        });
    }

    //加载界面
    public void initview(){
        mRecycleView = (RecyclerView) findViewById(R.id.my_club_blog_recycleview);
        // 设置布局管理器
        mRecycleView.addItemDecoration(new DividerDecoration(ClubInformationActivity.this));    //添加item间的分界线
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setAdapter(mAdapter);    //设置adapter
    }

    //构造请求体信息
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

    //解析字符串中的JSON数据，并返回JSON数组
    public static ArrayList<club_blog> analysisArray(String json, Type type) {
        ArrayList<club_blog> mlist = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                club_blog o = new Gson().fromJson(String.valueOf(jsonArray.get(i)), type);
                mlist.add(o);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mlist;
    }
}

package com.example.administrator.graduationproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.graduationproject.adapter.account_blog;
import com.example.administrator.graduationproject.adapter.account_blog_adapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019-2-2.
 */
//用户详情界面
public class AccountInformationActivity extends AppCompatActivity {
    private ServerIP sIP;    //用于获取服务器的IP地址
    private int follow;    //用于标识是否已关注此用户，0表示未关注，1表示关注
    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private account_blog_adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information);    //加载用户详情界面
        Bundle bundle= this.getIntent().getExtras();
        String account = bundle.getString("account");    //获取上一个页面传递的参数，即我的账号
        String account_id = bundle.getString("account_id");    //获取上一个页面传递的参数，即对方的账号
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        Button club_button=(Button) findViewById(R.id.club_button);    //获取查看共同关注社团按钮
        Button chat_button=(Button) findViewById(R.id.chat_button);    //获取聊天按钮
        Button follow_button=(Button) findViewById(R.id.follow_button);    //获取关注/取消关注按钮
        mLayoutManager = new LinearLayoutManager(AccountInformationActivity.this, LinearLayoutManager.VERTICAL, false);
        setListeners(club_button, follow_button, chat_button, account, account_id, ip);    //设置监听器
        getInformation(account, account_id, ip, follow_button);    //获取用户信息
        initdata(account_id, ip);    //加载用户动态
    }

    //设置监听器。参数分别为：查看共同关注社团按钮，关注/取消关注按钮，我的账号，对方的账号，服务器ip地址
    public void setListeners(Button club_button, final Button follow_button, Button chat_button, final String account, final String account_id, final String ip){
        //设置查看共同关注社团按钮的监听器
        club_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("account", account);    //向下一个页面传递参数：我的账号
                bundle.putString("account_id", account_id);    //向下一个页面传递参数：对方的账号
                intent.setClass(AccountInformationActivity.this,BothClubsActivity.class);    //跳转到共同关注社团界面
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //设置聊天按钮的监听器
        chat_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("account", account);    //向下一个页面传递参数：我的账号
                bundle.putString("account_id", account_id);    //向下一个页面传递参数：对方的账号
                TextView txt_name = (TextView) findViewById(R.id.account_name);
                String name = txt_name.getText().toString();
                bundle.putString("name", name);    //向下一个页面传递参数：对方的用户昵称
                intent.setClass(AccountInformationActivity.this,ChatActivity.class);    //跳转到聊天界面
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //设置关注/取消关注按钮的监听器
        follow_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //如果未关注
                if (follow == 0) {
                    Follow(account, account_id, ip);    //关注
                    follow_button.setText("取消关注");    //修改按钮文本
                }
                //如果已关注
                else if (follow == 1) {
                    cancelFollow(account, account_id, ip);    //取消关注
                    follow_button.setText("关注");    //修改按钮文本
                }
            }
        });
    }

    //关注。参数分别为：我的账号，对方的账号，服务器ip地址
    private void Follow(String account, String account_id, String ip){
        //向请求体中插入数据：账号和对方的账号
        Map<String, String> params = new HashMap<String, String>();
        params.put("followerid",account);
        params.put("accountid",account_id);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/insert/follow_account/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                if (response.equals("followaccount")) {
                    Toast.makeText(AccountInformationActivity.this, "关注成功!", Toast.LENGTH_SHORT).show();
                    follow = 1;    //标志已关注
                }
            }
        });
        /*String url = "http://" + ip + ":8000/follow_account/" + account + "/" + account_id + "/";
        AsynNetUtils.get(url, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                if (response.equals("followaccount")) {
                    Toast.makeText(AccountInformationActivity.this, "关注成功!", Toast.LENGTH_SHORT).show();
                    follow = 1;    //标志已关注
                }
            }
        });*/
    }

    //取消关注。参数分别为：我的账号，对方的账号，服务器ip地址
    private void cancelFollow(String account, String account_id, String ip){
        //向请求体中插入数据：账号和对方的账号
        Map<String, String> params = new HashMap<String, String>();
        params.put("followerid",account);
        params.put("accountid",account_id);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/delete/cancel_follow_account/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                if (response.equals("cancelfollowaccount")) {
                    Toast.makeText(AccountInformationActivity.this, "取消关注成功!", Toast.LENGTH_SHORT).show();
                    follow = 0;    //标志未关注
                }
            }
        });
        /*String url = "http://" + ip + ":8000/cancel_follow_account/" + account + "/" + account_id + "/";
        AsynNetUtils.get(url, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                if (response.equals("cancelfollowaccount")) {
                    Toast.makeText(AccountInformationActivity.this, "取消关注成功!", Toast.LENGTH_SHORT).show();
                    follow = 0;    //标志未关注
                }
            }
        });*/
    }

    //获取用户信息。参数分别为：我的账号，对方的账号，服务器ip地址，关注/取消关注按钮
    public void getInformation(String account, String account_id, String ip, final Button follow_button){
        TextView txt_account = (TextView) findViewById(R.id.account);
        txt_account.setText(account_id);    //设置对方的账号文本内容
        //向请求体中插入数据：对方的账号、账号
        Map<String, String> params = new HashMap<String, String>();
        params.put("accountid",account_id);
        params.put("followerid",account);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/query/get_account_information/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                String[] strs = response.split("/");
                TextView txt_name = (TextView) findViewById(R.id.account_name);
                txt_name.setText(strs[0]);    //设置对方的用户昵称文本内容
                TextView txt_information = (TextView) findViewById(R.id.information);
                txt_information.setText(strs[1]);    //设置对方的个人简介文本内容
                follow = Integer.parseInt(strs[2]);    //获取是否已关注
                if (follow == 0) {
                    follow_button.setText("关注");    //修改按钮文本
                }
                else if (follow == 1) {
                    follow_button.setText("取消关注");    //修改按钮文本
                }
            }
        });
        /*String url = "http://" + ip + ":8000/get_account_information/" + account_id + "/" + account + "/";
        AsynNetUtils.get(url, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                String[] strs = response.split("/");
                TextView txt_name = (TextView) findViewById(R.id.account_name);
                txt_name.setText(strs[0]);    //设置对方的用户昵称文本内容
                TextView txt_information = (TextView) findViewById(R.id.information);
                txt_information.setText(strs[1]);    //设置对方的个人简介文本内容
                follow = Integer.parseInt(strs[2]);    //获取是否已关注
                if (follow == 0) {
                    follow_button.setText("关注");    //修改按钮文本
                }
                else if (follow == 1) {
                    follow_button.setText("取消关注");    //修改按钮文本
                }
            }
        });*/
    }

    //加载用户动态。参数分别为：账号，服务器ip地址
    public void initdata(String account, String ip){
        //向请求体中插入数据：账号
        Map<String, String> params = new HashMap<String, String>();
        params.put("accountid",account);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/query/get_account_blog/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取用户动态的ArrayList
                ArrayList<account_blog> flist = analysisArray(response, account_blog.class);
                mAdapter = new account_blog_adapter(flist);
                initview();    //加载界面
            }
        });
        /*String url = "http://" + ip + ":8000/get_account_blog/" + account + "/";
        AsynNetUtils.get(url, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取用户动态的ArrayList
                ArrayList<account_blog> flist = analysisArray(response, account_blog.class);
                mAdapter = new account_blog_adapter(flist);
                initview();    //加载界面
            }
        });*/
    }

    //加载界面
    public void initview(){
        mRecycleView = (RecyclerView) findViewById(R.id.my_account_blog_recycleview);
        // 设置布局管理器
        mRecycleView.addItemDecoration(new DividerDecoration(AccountInformationActivity.this));    //添加item间的分界线
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setAdapter(mAdapter);    //设置adapter
    }

    //解析字符串中的JSON数据，并返回JSON数组
    public static ArrayList<account_blog> analysisArray(String json, Type type) {
        ArrayList<account_blog> mlist = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                account_blog o = new Gson().fromJson(String.valueOf(jsonArray.get(i)), type);
                mlist.add(o);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mlist;
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
}

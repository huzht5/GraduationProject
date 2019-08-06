package com.example.administrator.graduationproject;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.graduationproject.adapter.account_message;
import com.example.administrator.graduationproject.adapter.message_data;
import com.example.administrator.graduationproject.adapter.message_data_adapter;
import com.example.administrator.graduationproject.localDataBase.LocalAccountInterface;
import com.example.administrator.graduationproject.localDataBase.LocalMessageInterface;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019-2-22.
 */
//聊天界面
public class ChatActivity extends AppCompatActivity {
    private ServerIP sIP;    //用于获取服务器的IP地址
    private LocalAccountInterface the_local_account;    //用户管理本地数据库
    private LocalMessageInterface the_local_message;    //聊天记录本地数据库
    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private message_data_adapter mAdapter;
    private boolean timerFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_chat);    //加载用户详情界面
        Bundle bundle= this.getIntent().getExtras();
        the_local_account = new LocalAccountInterface(getBaseContext());
        the_local_message = new LocalMessageInterface(getBaseContext());
        String account = bundle.getString("account");    //获取上一个页面传递的参数，即我的账号
        String account_id = bundle.getString("account_id");    //获取上一个页面传递的参数，即对方的账号
        String name = bundle.getString("name");    //获取上一个页面传递的参数，即对方的用户昵称
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        TextView name_txt = (TextView) findViewById(R.id.name);
        name_txt.setText(name);
        Button send_button = (Button) findViewById(R.id.send);    //获取发送按钮
        mLayoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        setListeners(name_txt, send_button, account, account_id, ip, name);    //设置监听器
        initdata(account, account_id);    //加载聊天记录
    }

    public void setListeners(TextView name_txt, Button send_button, final String account, final String account_id, final String ip, final String name){
        //点击用户名可跳转到用户详情界面
        name_txt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("account", account);    //向下一个页面传递参数：我的账号
                bundle.putString("account_id", account_id);    //向下一个页面传递参数：对方的账号
                intent.setClass(ChatActivity.this,AccountInformationActivity.class);    //跳转到用户详情界面
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //设置发送按钮的监听器
        send_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                EditText message_txt = (EditText) findViewById(R.id.message);
                String message = message_txt.getText().toString();
                if(!message.equals("")) {
                    message_txt.setText("");
                    sendMessage(account, account_id, message, ip, name);    //发送消息
                }
            }
        });
    }

    //发送消息。参数分别为：我的账号，对方的账号，内容，服务器ip地址
    public void sendMessage(final String account, final String account_id, final String message, String ip, final String name){
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        final String str_time = sdf.format(dt);    //获取当前时间
        //向请求体中插入数据：社团名，时间，内容
        Map<String, String> params = new HashMap<String, String>();
        params.put("senderid",account);
        params.put("receiverid",account_id);
        params.put("time",str_time);
        params.put("message",message);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String murl = "http://" + ip + ":8000/insert/send_message/";
        AsynNetUtils.post(murl, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                if (response.equals("sendmessage")) {
                    the_local_account.setMessage(account, 1);
                    the_local_message.AddMessage(account, account_id, name, "1", str_time, message, "0");
                    message_data md = new message_data(str_time, message, "1");
                    mAdapter.mData.add(md);
                    mAdapter.notifyDataSetChanged();
                    mRecycleView.scrollToPosition(mAdapter.getItemCount() - 1);
                }
            }
        });
    }

    //加载聊天记录
    public void initdata(String account, String account_id){
        ArrayList<message_data> flist = the_local_message.getMessageList(account, account_id);
        mAdapter = new message_data_adapter(flist);
        mRecycleView = (RecyclerView) findViewById(R.id.my_recycleview);
        // 设置布局管理器
        final LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        ViewTreeObserver observer = layout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                layout.getWindowVisibleDisplayFrame(rect);
                int screenHeight=layout.getRootView().getHeight();//获取屏幕高度
                int heightDiff=screenHeight-(rect.bottom-rect.top);//获取高度之差

                if (heightDiff>50){//50是因为我设置的editText高度为40，这个数值可以随时调整的
                    //此时软键盘弹出
                    mRecycleView.scrollToPosition(mAdapter.getItemCount() - 1);
                }

            }
        });
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        //linearLayoutManager.setStackFromEnd(true); //关键 设置此项，当软键盘弹出时，布局会自动顶上去
        //mRecycleView.setLayoutManager(linearLayoutManager);
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setAdapter(mAdapter);    // 设置adapter
        mRecycleView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        getMessage();
    }

    public void getMessage(){
        Bundle bundle = this.getIntent().getExtras();
        final String account = bundle.getString("account");    //获取上一个页面传递的参数，即账号
        String ip = sIP.getIp();    //服务器ip地址
        //向请求体中插入数据：账号
        Map<String, String> params = new HashMap<String, String>();
        params.put("accountid",account);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/query/get_message/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取用户消息ArrayList
                ArrayList<account_message> flist = analysisArray(response, account_message.class);
                setLocalMessage(flist, account);
            }
        });
        /*String url = "http://" + ip + ":8000/get_message/" + account + "/";
        AsynNetUtils.get(url, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取用户消息ArrayList
                ArrayList<account_message> flist = analysisArray(response, account_message.class);
                setLocalMessage(flist, account);
            }
        });*/
    }

    public void setLocalMessage(ArrayList<account_message> flist, String account){
        if (flist.size() > 0){
            the_local_account.setMessage(account, 1);
            Bundle bundle= this.getIntent().getExtras();
            String account_id = bundle.getString("account_id");    //获取上一个页面传递的参数，即对方的账号
            for (int i = 0; i < flist.size(); i++){
                String name = flist.get(i).name;
                String id = flist.get(i).id;
                String time = flist.get(i).time;
                String message = flist.get(i).message;
                String state = flist.get(i).state;
                if (account_id.equals(id)){
                    state = "0";
                    message_data md = new message_data(time, message, "0");
                    mAdapter.mData.add(md);
                    mAdapter.notifyDataSetChanged();
                }
                the_local_message.AddMessage(account, id, name, "0", time, message, state);
            }
        }
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

    //解析字符串中的JSON数据，并返回JSON数组
    public static ArrayList<account_message> analysisArray(String json, Type type) {
        ArrayList<account_message> mlist = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                account_message o = new Gson().fromJson(String.valueOf(jsonArray.get(i)), type);
                mlist.add(o);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mlist;
    }
}

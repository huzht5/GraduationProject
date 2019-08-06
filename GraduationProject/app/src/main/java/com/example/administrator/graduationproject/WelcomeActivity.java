package com.example.administrator.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.administrator.graduationproject.localDataBase.LocalAccountInterface;

//欢迎界面
public class WelcomeActivity extends AppCompatActivity {
    private LocalAccountInterface the_local_account;    //用户管理本地数据库
    private ServerIP sIP;    //用于获取服务器的IP地址
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);    //加载欢迎界面
        the_local_account = new LocalAccountInterface(getBaseContext());
        final String account = the_local_account.GetTheAccount();    //查找本地数据库中已登录的账号
        sIP = new ServerIP();
        String ip = sIP.getIp();    //获取服务器的IP地址
        //如果本地数据库中没有已登录的账号，则跳转到登录/注册界面
        if (account.equals("")) {
            final Intent intent=new Intent();
            intent.setClass(WelcomeActivity.this,LoginAndRegisterActivity.class);
            Jump(intent);
        }
        //如果本地数据库中有已登录的账号，则向服务器发送请求，确认能否连接到服务器
        else {
            url = "http://" + ip + ":8000/query/login_no_password/";
            AsynNetUtils.get(url, new AsynNetUtils.Callback() {
                @Override
                public void onResponse(String response) {
                    PrepareToJump(response, account);
                }
            });
        }
    }

    //分析服务器回复的信息，并据此跳转到相应的界面。参数分别为：服务器应答信息，已登录的账号，服务器IP地址
    private void PrepareToJump(String response, String account){
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putString("account", account);    //向下一个页面传递参数account，即已登录的账号
        //如果服务器回复登录成功，则跳转到主界面
        if (response.equals("succeed")){
            intent.setClass(WelcomeActivity.this,MainPageActivity.class);
        }
        //若服务器没有回复，则跳转到连接服务器失败界面
        else {
            intent.setClass(WelcomeActivity.this,NoConnectionActivity.class);
        }
        intent.putExtras(bundle);
        Jump(intent);
    }

    //界面停滞两秒后再跳转，然后finish这个界面
    private void Jump(final Intent intent){
        Integer time = 2000;    //设置等待时间，单位为毫秒
        Handler handler = new Handler();
        //当计时结束时，跳转
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }, time);
    }

    public String getUrl(){
        return url;
    }
}

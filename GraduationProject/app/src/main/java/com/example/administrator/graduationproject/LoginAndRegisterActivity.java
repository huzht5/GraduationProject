package com.example.administrator.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.graduationproject.localDataBase.LocalAccountInterface;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019-1-23.
 */
//登录/注册界面
public class LoginAndRegisterActivity extends AppCompatActivity {
    private LocalAccountInterface the_local_account;    //用户管理本地数据库
    private ServerIP sIP;    //用于获取服务器的IP地址
    private boolean flag = false;    //用于标识密码可不可见，初始为不可见

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_register);    //加载登录/注册界面
        //获取账号编辑框和密码编辑框，并设置最大输入长度为40个字符
        EditText mNumberText = (EditText) findViewById(R.id.text_userid), mPasswordText = (EditText) findViewById(R.id.text_userpwd);
        mNumberText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        mPasswordText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        Button register_button=(Button)findViewById(R.id.register_button);    //获取注册按钮
        Button login_button=(Button)findViewById(R.id.login_button);    //获取登录按钮
        Button clear_button=(Button)findViewById(R.id.clear_button);    //获取清除按钮
        ImageView img_psw = (ImageView)findViewById(R.id.password);    //获取切换密码可见性的图片按钮
        sIP = new ServerIP();
        the_local_account = new LocalAccountInterface(getBaseContext());
        setListener(register_button, login_button, clear_button, mNumberText, mPasswordText, img_psw);    //设置监听器
    }

    //设置监听器，参数分别为：注册按钮，登录按钮，清除按钮，账号编辑框，密码编辑框，切换图片
    private void setListener(Button register_button, Button login_button, Button clear_button, final EditText mNumberText, final EditText mPasswordText, ImageView img_psw){
        //设置注册按钮的监听器
        register_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //先检查两个编辑框是否为空
                checkText(0, mNumberText, mPasswordText);
            }
        });
        //设置登录按钮的监听器
        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //先检查两个编辑框是否为空
                checkText(1, mNumberText, mPasswordText);
            }
        });
        //设置清除按钮的监听器
        clear_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //清除本地数据库的所有账号
                the_local_account.deleteAllAccounts();
                Toast.makeText(LoginAndRegisterActivity.this, "清除成功！", Toast.LENGTH_SHORT).show();
            }
        });
        //设置注册按钮的监听器
        img_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag) {
                    mPasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//密码可见
                    //img_psw.setImageResource(R.mipmap.ic_psw_yes);
                } else {
                    mPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());//密码不可见
                    //img_psw.setImageResource(R.mipmap.ic_psw_no);
                }
                flag = !flag;
                mPasswordText.postInvalidate();
                //防止每次切换密码可见与不可见后光标会回到行首的情况
                CharSequence text = mPasswordText.getText();
                if (text instanceof Spannable) {
                    Spannable spanText = (Spannable)text;
                    Selection.setSelection(spanText, text.length());
                }
            }
        });
    }

    //判断两个编辑框是否为空。若至少有一个为空则报错；若都不为空，则向服务器发送请求。参数分别为：区分注册还是登录的int型参数，账号编辑框，密码编辑框
    private void checkText(int RorL, EditText mNumberText, EditText mPasswordText){
        String ip = sIP.getIp();    //服务器ip地址
        final String number = mNumberText.getText().toString();    //账号
        String password = mPasswordText.getText().toString();    //密码
        //账号未填写
        if(TextUtils.isEmpty(number)) mNumberText.setError("账号不能为空！");
        //密码未填写
        else if(TextUtils.isEmpty(password)) mPasswordText.setError("密码不能为空！");
        else {
            String url = "";
            //向请求体中插入数据：账号和密码
            Map<String, String> params = new HashMap<String, String>();
            params.put("accountid",number);
            params.put("password",password);
            //如果是注册，则设置好url，再插入数据：用户名、简介和粉丝数
            if (RorL == 0) {
                url = "http://" + ip + ":8000/insert/register/";
                params.put("name","新用户");
                params.put("information","这个人很懒!什么也没有写!=.=");
                params.put("count","0");
            }
            //如果是登录，则不用再插入其他数据
            else if (RorL == 1) {
                url = "http://" + ip + ":8000/query/login_with_password/";
            }
            String content = getRequestData(params, "UTF-8").toString();    //生成请求体
            AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
                @Override
                public void onResponse(String response) {
                    int succeedOrNot;    //用于标识是否成功连接服务器
                    //若服务器回复注册成功，则自动登录
                    if (response.equals("registered")) {
                        Toast.makeText(LoginAndRegisterActivity.this, "账号注册成功！", Toast.LENGTH_SHORT).show();
                        succeedOrNot = 1;    //连接服务器成功
                        the_local_account.ChangeState(number, 1);    //改变本地账号数据库的账号状态，因为注册成功后自动登录
                        PrepareToJump(succeedOrNot, number);
                    }
                    //若服务器回复此账号已被注册，则提示
                    else if (response.equals("fail")) Toast.makeText(LoginAndRegisterActivity.this, "此账号已被注册！", Toast.LENGTH_SHORT).show();
                    //若服务器回复密码错误，则提示
                    else if (response.equals("wrongpassword")) Toast.makeText(LoginAndRegisterActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                    //若服务器回复此账号未注册，则提示
                    else if (response.equals("unregistered")) Toast.makeText(LoginAndRegisterActivity.this, "此账号未注册！", Toast.LENGTH_SHORT).show();
                    //若服务器回复登录成功
                    else if (response.equals("succeed")) {
                        Toast.makeText(LoginAndRegisterActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                        succeedOrNot = 1;    //连接服务器成功
                        the_local_account.ChangeState(number, 1);    //改变本地账号数据库的账号状态
                        PrepareToJump(succeedOrNot, number);
                    }
                    //若服务器没有回复，则连接服务器失败，跳转到连接服务器失败界面
                    else {
                        succeedOrNot = 0;
                        PrepareToJump(succeedOrNot, number);
                    }
                }
            });
        }
    }

    //根据服务器回复的信息跳转到相应的界面。参数分别为：是否连接服务器成功的标志，账号
    private void PrepareToJump(int succeedOrNot, String account){
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putString("account", account);    //向下一个页面传递参数：账号
        //如果连接服务器成功并且跳转界面，则肯定登录成功，跳转到主界面
        if (succeedOrNot == 1){
            intent.setClass(LoginAndRegisterActivity.this,MainPageActivity.class);
        }
        //如果连接服务器失败，则跳转到连接服务器失败界面
        else if (succeedOrNot == 0) {
            intent.setClass(LoginAndRegisterActivity.this,NoConnectionActivity.class);
        }
        intent.putExtras(bundle);
        startActivity(intent);
        LoginAndRegisterActivity.this.finish();  //finish登录/注册界面
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

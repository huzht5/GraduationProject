package com.example.administrator.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2019-2-7.
 */
//创建社团界面
public class CreateClubActivity extends AppCompatActivity {
    private ServerIP sIP;    //用于获取服务器的IP地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);    //加载创建社团界面
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        Bundle bundle = getIntent().getExtras();
        String account =bundle.getString("account");    //获取上一个页面传递的参数，即账号
        EditText txt_name = (EditText) findViewById(R.id.club_name);
        setEditTextInhibitInputSpaChat(txt_name);    //限制输入框的输入内容
        EditText txt_information = (EditText) findViewById(R.id.information);
        setEditTextInhibitInputSpaChat(txt_information);    //限制输入框的输入内容
        Button create_button = (Button) findViewById(R.id.create);
        setListener(txt_name, txt_information, create_button, account, ip);    //设置监听器
    }

    //设置监听器。参数分别为：社团名，社团简介，创建按钮，账号，服务器ip地址
    public void setListener(final EditText txt_name, final EditText txt_information, Button create_button, final String account, final String ip){
        create_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String club_name = txt_name.getText().toString();
                String information = txt_information.getText().toString();
                //判断输入框是否为空
                if(club_name.equals("")) Toast.makeText(CreateClubActivity.this, "社团名不能为空！", Toast.LENGTH_SHORT).show();
                else if(information.equals("")) Toast.makeText(CreateClubActivity.this, "简介不能为空！", Toast.LENGTH_SHORT).show();
                else createClub(account, club_name, information, ip);    //创建社团
            }
        });
    }

    //创建社团。参数分别为：账号，社团名，社团简介，服务器ip地址
    public void createClub(String account, String club_name, String information, String ip){
        Bundle bundle = getIntent().getExtras();
        String account_name =bundle.getString("account_name");    //获取上一个页面传递的参数，即用户昵称
        //向请求体中插入数据：账号，用户昵称，社团名，社团简介，粉丝数
        Map<String, String> params = new HashMap<String, String>();
        params.put("ownerid",account);
        params.put("ownername",account_name);
        params.put("name",club_name);
        params.put("information",information);
        params.put("count","0");
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/insert/create_club/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                if (response.equals("fail")) {
                    Toast.makeText(CreateClubActivity.this, "此社团名已被使用!", Toast.LENGTH_SHORT).show();
                }
                else if (response.equals("createclub")) {
                    Toast.makeText(CreateClubActivity.this, "创建社团成功!", Toast.LENGTH_SHORT).show();
                    CreateClubActivity.this.finish();
                }
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

    //限制输入框的输入内容
    public void setEditTextInhibitInputSpaChat(EditText editText) {
        InputFilter filter_noEnter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                String speChat = "[\n /]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(charSequence.toString());
                if (matcher.find()) {
                    Toast.makeText(CreateClubActivity.this, "禁止使用的符号!", Toast.LENGTH_SHORT).show();
                    return "";
                }
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter_noEnter, new InputFilter.LengthFilter(40)});
    }
}

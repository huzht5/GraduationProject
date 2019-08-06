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
 * Created by Administrator on 2019-2-13.
 */
//修改社团信息界面
public class ChangeMyClubInformationActivity extends AppCompatActivity {
    private ServerIP sIP;    //用于获取服务器的IP地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_my_club_information);    //加载修改社团信息界面
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        Bundle bundle = getIntent().getExtras();
        String club_name =bundle.getString("club_name");    //获取上一个页面传递的参数，即社团名
        String information =bundle.getString("information");    //获取上一个页面传递的参数，即社团简介
        EditText txt_information = (EditText) findViewById(R.id.information);
        setEditTextInhibitInputSpaChat(txt_information);    //限制输入框的输入内容
        txt_information.setText(information);
        Button change_button = (Button) findViewById(R.id.change);
        setListener(txt_information, change_button, club_name, ip);    //设置监听器
    }

    //设置监听器。参数分别为：社团简介，修改按钮，社团名，服务器ip地址
    public void setListener(final EditText txt_information, Button change_button, final String club_name, final String ip){
        change_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String information = txt_information.getText().toString();
                //判断输入框是否为空
                if(information.equals("")) Toast.makeText(ChangeMyClubInformationActivity.this, "社团简介不能为空！", Toast.LENGTH_SHORT).show();
                else changeInformation(club_name, information, ip);    //修改社团信息
            }
        });
    }

    //修改社团信息。参数分别为：社团名，社团简介，服务器ip地址
    public void changeInformation(String club_name, final String information, String ip){
        String murl = "http://" + ip + ":8000/update/change_club_information/";
        //向请求体中插入数据：社团名，社团简介
        Map<String, String> params = new HashMap<String, String>();
        params.put("name",club_name);
        params.put("information",information);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        AsynNetUtils.post(murl, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                if (response.equals("changeclubinformation")) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("information", information);    //回传参数：社团简介
                    intent.putExtras(bundle);
                    setResult(1, intent);
                    Toast.makeText(ChangeMyClubInformationActivity.this, "修改社团简介成功!", Toast.LENGTH_SHORT).show();
                    ChangeMyClubInformationActivity.this.finish();
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
                String speChat = "[/]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(charSequence.toString());
                if (matcher.find()) {
                    Toast.makeText(ChangeMyClubInformationActivity.this, "禁止使用的符号!", Toast.LENGTH_SHORT).show();
                    return "";
                }
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter_noEnter, new InputFilter.LengthFilter(200)});
    }
}

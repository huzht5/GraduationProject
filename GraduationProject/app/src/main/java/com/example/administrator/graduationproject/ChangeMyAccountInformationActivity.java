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
//修改个人信息界面
public class ChangeMyAccountInformationActivity extends AppCompatActivity {
    private ServerIP sIP;    //用于获取服务器的IP地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_my_information);    //加载修改个人信息界面
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        Bundle bundle = getIntent().getExtras();
        String account =bundle.getString("account");    //获取上一个页面传递的参数，即账号
        String account_name =bundle.getString("account_name");    //获取上一个页面传递的参数，即用户昵称
        String information =bundle.getString("information");    //获取上一个页面传递的参数，即个人简介
        EditText txt_name = (EditText) findViewById(R.id.account_name);
        setEditTextInhibitInputSpaChat(txt_name);    //限制输入框的输入内容
        EditText txt_information = (EditText) findViewById(R.id.information);
        setEditTextInhibitInputSpaChat1(txt_information);    //限制输入框的输入内容
        txt_name.setText(account_name);    //设置用户昵称的文本内容
        txt_information.setText(information);    //设置个人简介的文本内容
        Button change_button = (Button) findViewById(R.id.change);    //获取修改按钮
        setListener(txt_name, txt_information, change_button, account, ip);    //设置监听器
    }

    //设置监听器。参数分别为：用户昵称，个人简介，修改按钮，账号，服务器ip地址
    public void setListener(final EditText txt_name, final EditText txt_information, Button change_button, final String account, final String ip){
        change_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String account_name = txt_name.getText().toString();
                String information = txt_information.getText().toString();
                //判断输入框是否为空
                if(account_name.equals("")) Toast.makeText(ChangeMyAccountInformationActivity.this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
                else if(information.equals("")) Toast.makeText(ChangeMyAccountInformationActivity.this, "简介不能为空！", Toast.LENGTH_SHORT).show();
                else changeInformation(account, account_name, information, ip);    //修改个人信息
            }
        });
    }

    //修改个人信息。参数分别为：账号，用户昵称，个人简介，服务器ip地址
    public void changeInformation(String account, final String account_name, final String information, String ip){
        String url = "http://" + ip + ":8000/update/change_account_information/";
        //向请求体中插入数据：账号，用户昵称，个人简介
        Map<String, String> params = new HashMap<String, String>();
        params.put("accountid",account);
        params.put("name",account_name);
        params.put("information",information);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                if (response.equals("changeaccountinformation")) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("account_name", account_name);    //回传参数：用户昵称
                    bundle.putString("information", information);    //回传参数：个人简介
                    intent.putExtras(bundle);
                    setResult(1, intent);
                    Toast.makeText(ChangeMyAccountInformationActivity.this, "修改个人信息成功!", Toast.LENGTH_SHORT).show();
                    ChangeMyAccountInformationActivity.this.finish();
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
                    Toast.makeText(ChangeMyAccountInformationActivity.this, "禁止使用的符号!", Toast.LENGTH_SHORT).show();
                    return "";
                }
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter_noEnter, new InputFilter.LengthFilter(40)});
    }

    //限制输入框的输入内容
    public void setEditTextInhibitInputSpaChat1(EditText editText) {
        InputFilter filter_noEnter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                String speChat = "[/]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(charSequence.toString());
                if (matcher.find()) {
                    Toast.makeText(ChangeMyAccountInformationActivity.this, "禁止使用的符号!", Toast.LENGTH_SHORT).show();
                    return "";
                }
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter_noEnter, new InputFilter.LengthFilter(200)});
    }
}

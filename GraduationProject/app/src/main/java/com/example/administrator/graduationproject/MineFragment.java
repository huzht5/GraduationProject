package com.example.administrator.graduationproject;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.graduationproject.localDataBase.LocalAccountInterface;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019-2-3.
 */
//我的个人信息fragment界面
public class MineFragment extends Fragment {
    private LocalAccountInterface the_local_account;    //用于退出当前账号时修改本地账号数据库
    private ServerIP sIP;    //用于获取服务器的IP地址
    private TextView txt_name;    //用户昵称
    private TextView txt_information;    //个人简介

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_mine,container,false);    //加载我的个人信息fragment界面
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        the_local_account = new LocalAccountInterface(getActivity().getBaseContext());
        Button change_button=(Button) view.findViewById(R.id.change_button);    //获取修改个人信息按钮
        Button out_button=(Button) view.findViewById(R.id.out_button);    //获取退出当前账号按钮
        Button my_blog_button=(Button) view.findViewById(R.id.my_blog_button);    //获取查看个人动态按钮
        Button create_blog_button=(Button) view.findViewById(R.id.create_blog_button);    //获取发布个人动态按钮
        Button my_club_button=(Button) view.findViewById(R.id.my_club_button);    //获取查看我的社团按钮
        Button create_club_button=(Button) view.findViewById(R.id.create_club_button);    //获取创建我的社团按钮
        txt_name = (TextView) view.findViewById(R.id.account_name);    //获取用户昵称
        txt_information = (TextView) view.findViewById(R.id.information);    //获取个人简介
        //设置监听器
        setListeners(txt_name, txt_information, change_button, out_button, my_blog_button, create_blog_button, my_club_button, create_club_button);    //设置四个按钮的监听器
        getMyInformation(ip, view);    //从服务器获取个人信息
        return view;
    }

    //从修改个人信息界面跳转回来时更新用户昵称和个人简介
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (requestCode == 1) {
                Bundle bundle = data.getExtras();
                String name =bundle.getString("account_name");    //获取传递的参数：用户昵称
                String information =bundle.getString("information");    //获取传递的参数：个人简介
                txt_name.setText(name);    //更新
                txt_information.setText(information);    //更新
            }
        }
    }

    //设置监听器。参数分别为：用户昵称，个人简介，修改个人信息按钮，退出当前账号按钮，查看个人动态按钮，发布个人动态按钮，查看我的社团按钮，创建我的社团按钮
    public void setListeners(TextView txt_name, TextView txt_information, Button change_button, Button out_button, Button my_blog_button, Button create_blog_button, Button my_club_button, Button create_club_button){
        setChangeButtonListener(txt_name, txt_information, change_button);    //设置修改个人信息按钮的监听器
        setOutButtonListener(out_button);    //设置退出当前账号按钮的监听器
        setMyBlogButtonListener(my_blog_button);    //设置查看个人动态按钮的监听器
        setCreateBlogButtonListener(create_blog_button);    //设置发布个人动态按钮的监听器
        setMyClubButtonListener(my_club_button);    //设置查看我的社团按钮的监听器
        setCreateClubButtonListener(txt_name, create_club_button);    //设置创建我的社团按钮的监听器
    }

    //设置修改个人信息按钮的监听器。参数分别为：用户昵称，个人简介，修改个人信息按钮
    public void setChangeButtonListener(final TextView txt_name, final TextView txt_information, Button change_button){
        change_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle1 = getActivity().getIntent().getExtras();
                String account =bundle1.getString("account");    //获取上一个页面传递的参数，即账号
                Bundle bundle=new Bundle();
                String account_name = txt_name.getText().toString();
                String information = txt_information.getText().toString();
                bundle.putString("account", account);    //向下一个页面传递参数：账号
                bundle.putString("account_name", account_name);    //向下一个页面传递参数：用户昵称
                bundle.putString("information", information);    //向下一个页面传递参数：个人简介
                intent.setClass(getActivity(),ChangeMyAccountInformationActivity.class);    //跳转到修改个人信息界面
                intent.putExtras(bundle);
                startActivityForResult(intent,1);    //可回传参数的跳转
            }
        });
    }

    //设置退出当前账号按钮的监听器。参数为：退出当前账号按钮
    public void setOutButtonListener(Button out_button){
        out_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("退出当前账号？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Bundle bundle = getActivity().getIntent().getExtras();
                                String account =bundle.getString("account");    //获取上一个页面传递的参数，即账号
                                the_local_account.ChangeState(account, 0);    //把本地数据库中的账号状态变为未登录
                                Intent intent=new Intent();
                                intent.setClass(getActivity(),LoginAndRegisterActivity.class);    //跳转到登录/注册界面
                                startActivity(intent);
                                getActivity().finish();    //finish主界面
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
    }

    //设置查看个人动态按钮的监听器。参数为：查看个人动态按钮
    public void setMyBlogButtonListener(Button my_blog_button){
        my_blog_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = getActivity().getIntent().getExtras();
                intent.setClass(getActivity(),MyAccountBlogActivity.class);    //跳转到查看个人动态界面
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //设置发布个人动态按钮的监听器。参数为：发布个人动态按钮
    public void setCreateBlogButtonListener(Button create_blog_button){
        create_blog_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = getActivity().getIntent().getExtras();
                intent.setClass(getActivity(),CreateAccountBlogActivity.class);    //跳转到发布个人动态界面
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //设置查看我的社团按钮的监听器。参数为：查看我的社团按钮
    public void setMyClubButtonListener(Button my_club_button){
        my_club_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = getActivity().getIntent().getExtras();
                intent.setClass(getActivity(),MyClubActivity.class);    //跳转到查看我的社团界面
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //设置创建我的社团按钮的监听器。参数为：创建我的社团按钮
    public void setCreateClubButtonListener(final TextView txt_name, Button create_club_button){
        create_club_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = getActivity().getIntent().getExtras();
                String account_name = txt_name.getText().toString();
                bundle.putString("account_name", account_name);
                intent.setClass(getActivity(),CreateClubActivity.class);    //跳转到创建我的社团界面
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //从服务器获取个人信息。参数分别为：服务器ip地址，view
    public void getMyInformation(String ip, View view){
        Bundle bundle = getActivity().getIntent().getExtras();
        String account =bundle.getString("account");    //获取上一个页面传递的参数，即账号
        TextView txt_account = (TextView) view.findViewById(R.id.account);
        txt_account.setText(account);    //设置账号文本内容
        //向请求体中插入数据：账号
        Map<String, String> params = new HashMap<String, String>();
        params.put("accountid",account);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/query/get_my_account_information/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                String[] strs = response.split("/");
                txt_name.setText(strs[0]);    //设置用户昵称文本内容
                txt_information.setText(strs[1]);    //设置个人简介文本内容
            }
        });
        /*final String murl = "http://" + ip + ":8000/get_my_account_information/" + account + "/";
        AsynNetUtils.get(murl, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                String[] strs = response.split("/");
                txt_name.setText(strs[0]);    //设置用户昵称文本内容
                txt_information.setText(strs[1]);    //设置个人简介文本内容
            }
        });*/
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

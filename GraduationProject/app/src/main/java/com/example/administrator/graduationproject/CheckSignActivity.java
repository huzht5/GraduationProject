package com.example.administrator.graduationproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.graduationproject.adapter.account_sign_in;
import com.example.administrator.graduationproject.adapter.account_sign_in_adapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019-2-20.
 */
//查看签到人员界面
public class CheckSignActivity extends AppCompatActivity {
    private ServerIP sIP;    //用于获取服务器的IP地址
    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private account_sign_in_adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);    //加载界面
        TextView txt_topbar = (TextView) findViewById(R.id.txt_topbar);
        txt_topbar.setText("已签到的人员");    //设置标题
        Bundle bundle=this.getIntent().getExtras();
        String club_name = bundle.getString("club_name");    //获取上一个页面传递的参数，即社团名字
        String time = bundle.getString("time");    //获取上一个页面传递的参数
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        mLayoutManager = new LinearLayoutManager(CheckSignActivity.this, LinearLayoutManager.VERTICAL, false);
        initdata(club_name, time, ip);    //加载签到人员
    }

    //加载签到人员
    public void initdata(final String club_name, String time, final String ip){
        //向请求体中插入数据：社团名，时间
        Map<String, String> params = new HashMap<String, String>();
        params.put("name",club_name);
        params.put("time",time);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/query/check_sign/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取我的社团公告的ArrayList
                ArrayList<account_sign_in> flist = analysisArray(response, account_sign_in.class);
                mAdapter = new account_sign_in_adapter(flist);
                initview(club_name, ip);    //加载界面
            }
        });
    }

    //加载界面
    public void initview(String club_name, String ip){
        mRecycleView = (RecyclerView) findViewById(R.id.my_recycleview);
        // 设置布局管理器
        mRecycleView.addItemDecoration(new DividerDecoration(CheckSignActivity.this));    //添加item间的分界线
        mRecycleView.setLayoutManager(mLayoutManager);
        setAdapter(club_name, ip);    //设置adapter
    }

    //设置adapter
    public void setAdapter(final String club_name, final String ip){
        mAdapter.setOnItemClickListener(new account_sign_in_adapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Jump(position);    //跳转到用户详情界面
            }
        });
        mRecycleView.setAdapter(mAdapter);    // 设置adapter
        ifNoItem();    //如果item数为0
    }

    //跳转
    public void Jump(int position){
        Intent intent = new Intent();
        Bundle bundle1 = getIntent().getExtras();
        String account =bundle1.getString("account");    //获取上一个页面传递的参数，即账号
        Bundle bundle = new Bundle();
        //向下一个页面传递参数：账号、对方的账号
        bundle.putString("account", account);
        bundle.putString("account_id", mAdapter.mData.get(position).id);
        intent.setClass(CheckSignActivity.this,AccountInformationActivity.class);    //跳转到用户详情界面
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //如果item数为0
    public void ifNoItem(){
        if (mAdapter.getItemCount() == 0) {
            final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(CheckSignActivity.this);
            alertDialogBuilder.setTitle("暂时无人签到！")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CheckSignActivity.this.finish();
                        }
                    }).show();
        }
    }

    //解析字符串中的JSON数据，并返回JSON数组
    public static ArrayList<account_sign_in> analysisArray(String json, Type type) {
        ArrayList<account_sign_in> mlist = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                account_sign_in o = new Gson().fromJson(String.valueOf(jsonArray.get(i)), type);
                mlist.add(o);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mlist;
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

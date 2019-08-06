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
import android.widget.Toast;

import com.example.administrator.graduationproject.adapter.club_data;
import com.example.administrator.graduationproject.adapter.club_data_adapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019-2-3.
 */
//查看我的社团界面
public class MyClubActivity extends AppCompatActivity {
    private ServerIP sIP;    //用于获取服务器的IP地址
    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private club_data_adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);    //加载界面
        TextView txt_topbar = (TextView) findViewById(R.id.txt_topbar);
        txt_topbar.setText("我的社团");    //设置标题
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        mLayoutManager = new LinearLayoutManager(MyClubActivity.this, LinearLayoutManager.VERTICAL, false);
        getMyClubs(ip);    //获取我的社团
    }

    //获取我的社团
    public void getMyClubs(final String ip){
        Bundle bundle = getIntent().getExtras();
        final String account =bundle.getString("account");    //获取上一个页面传递的参数，即账号
        //向请求体中插入数据：账号
        Map<String, String> params = new HashMap<String, String>();
        params.put("accountid",account);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/query/get_my_clubs_data/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取我的社团的ArrayList
                ArrayList<club_data> flist = analysisArray(response, club_data.class);
                mAdapter = new club_data_adapter(flist);
                initview(ip, account);    //加载界面
            }
        });
        /*final String url = "http://" + ip + ":8000/get_my_clubs_data/" + account + "/";
        AsynNetUtils.get(url, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取我的社团的ArrayList
                ArrayList<club_data> flist = analysisArray(response, club_data.class);
                mAdapter = new club_data_adapter(flist);
                initview(ip, account);    //加载界面
            }
        });*/
    }

    //加载界面
    public void initview(String ip, String account){
        mRecycleView = (RecyclerView) findViewById(R.id.my_recycleview);
        // 设置布局管理器
        mRecycleView.addItemDecoration(new DividerDecoration(MyClubActivity.this));    //添加item间的分界线
        mRecycleView.setLayoutManager(mLayoutManager);
        setAdapter(ip, account);    //设置adapter
    }

    //设置adapter
    private void setAdapter(final String ip, final String account){
        //设置可选课程列表item的点击事件
        mAdapter.setOnItemClickListener(new club_data_adapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MyClubActivity.this);
                alertDialogBuilder.setTitle("查看这个社团的详情？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent();
                                Bundle bundle=new Bundle();
                                bundle.putString("club_name", mAdapter.mData.get(position).name);
                                bundle.putString("account", account);
                                intent.setClass(MyClubActivity.this,MyClubInformationActivity.class);    //跳转到查看我的社团详情界面
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
        //设置可选课程列表item的长按事件
        mAdapter.setOnLongItemClickListener(new club_data_adapter.OnRecyclerViewLongItemClickListener() {
            @Override
            public void onLongItemClick(View view,final int position) {
                final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MyClubActivity.this);
                alertDialogBuilder.setTitle("删除这个社团？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定",  new DialogInterface.OnClickListener() {// 积极
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteClub(position, ip);    //删除社团
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
        mRecycleView.setAdapter(mAdapter);    // 设置adapter
        ifNoItem();    //如果item数为0
    }

    //删除社团
    private void deleteClub(final int position, String ip){
        String club_name = String.valueOf(mAdapter.mData.get(position).name);
        Map<String, String> params = new HashMap<String, String>();
        params.put("name",club_name);
        String content = getRequestData(params, "UTF-8").toString();
        final String murl = "http://" + ip + ":8000/delete/delete_club/";
        AsynNetUtils.post(murl, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                if (response.equals("deleteclub")) {
                    Toast.makeText(MyClubActivity.this, "删除社团成功!", Toast.LENGTH_SHORT).show();
                    mAdapter.mData.remove(position);    //item数量减一
                    mAdapter.notifyDataSetChanged();    //更新列表
                }
            }
        });
    }

    //如果item数为0
    public void ifNoItem(){
        if (mAdapter.getItemCount() == 0) {
            final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MyClubActivity.this);
            alertDialogBuilder.setTitle("你还没创建过社团呢，快去创建一个吧")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyClubActivity.this.finish();
                        }
                    }).show();
        }
    }

    //解析字符串中的JSON数据，并返回JSON数组
    public static ArrayList<club_data> analysisArray(String json, Type type) {
        ArrayList<club_data> mlist = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                club_data o = new Gson().fromJson(String.valueOf(jsonArray.get(i)), type);
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

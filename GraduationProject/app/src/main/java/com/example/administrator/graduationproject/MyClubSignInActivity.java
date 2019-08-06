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

import com.example.administrator.graduationproject.adapter.club_sign_in;
import com.example.administrator.graduationproject.adapter.club_sign_in_adapter;
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
//查看社团签到活动界面
public class MyClubSignInActivity extends AppCompatActivity {
    private ServerIP sIP;    //用于获取服务器的IP地址
    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private club_sign_in_adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);    //加载界面
        TextView txt_topbar = (TextView) findViewById(R.id.txt_topbar);
        txt_topbar.setText("签到活动");    //设置标题
        Bundle bundle=this.getIntent().getExtras();
        String club_name=bundle.getString("club_name");    //获取上一个页面传递的参数，即社团名字
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        mLayoutManager = new LinearLayoutManager(MyClubSignInActivity.this, LinearLayoutManager.VERTICAL, false);
        initdata(club_name, ip);    //加载签到活动
    }

    //加载签到活动
    public void initdata(final String club_name, final String ip){
        //向请求体中插入数据：社团名
        Map<String, String> params = new HashMap<String, String>();
        params.put("name",club_name);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/query/get_club_sign/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取签到活动的ArrayList
                ArrayList<club_sign_in> flist = analysisArray(response, club_sign_in.class);
                mAdapter = new club_sign_in_adapter(flist);
                initview(club_name, ip);    //加载界面
            }
        });
    }

    //加载界面
    public void initview(String club_name, String ip){
        mRecycleView = (RecyclerView) findViewById(R.id.my_recycleview);
        // 设置布局管理器
        mRecycleView.addItemDecoration(new DividerDecoration(MyClubSignInActivity.this));    //添加item间的分界线
        mRecycleView.setLayoutManager(mLayoutManager);
        setAdapter(club_name, ip);    //设置adapter
    }

    //设置adapter
    public void setAdapter(final String club_name, final String ip){
        mAdapter.setOnItemClickListener(new club_sign_in_adapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MyClubSignInActivity.this);
                alertDialogBuilder.setTitle("请选择：")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("停止签到",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stopSign(position, club_name, ip);    //停止签到
                            }
                        }).setNegativeButton("查看签到人员",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkSign(position, club_name);    //查看签到人员
                            }
                        }).show();
            }
        });
        mAdapter.setOnLongItemClickListener(new club_sign_in_adapter.OnRecyclerViewLongItemClickListener() {
            @Override
            public void onLongItemClick(View view,final int position) {
                final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MyClubSignInActivity.this);
                alertDialogBuilder.setTitle("删除这个签到活动？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteSign(position, club_name, ip);    //删除签到活动
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
        mRecycleView.setAdapter(mAdapter);    // 设置adapter
        ifNoItem();    //如果item数为0
    }

    //停止签到
    public void stopSign(final int position, String club_name, String ip){
        String state = String.valueOf(mAdapter.mData.get(position).state);
        //如果已经结束，就提示已结束，不必访问服务器
        if (state.equals("签到结束")){
            Toast.makeText(MyClubSignInActivity.this, "签到活动已结束!", Toast.LENGTH_SHORT).show();
            return;
        }
        String time = String.valueOf(mAdapter.mData.get(position).time);
        //向请求体中插入数据：社团名，时间
        Map<String, String> params = new HashMap<String, String>();
        params.put("name",club_name);
        params.put("time",time);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/update/stop_club_sign/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                mAdapter.mData.get(position).state = "签到结束";    //修改签到活动状态
                mAdapter.notifyItemChanged(position); ;    //更新界面
                if (response.equals("stopclubsign")) Toast.makeText(MyClubSignInActivity.this, "签到活动结束!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //查看签到人员
    public void checkSign(int position, String club_name){
        String time = String.valueOf(mAdapter.mData.get(position).time);
        Intent intent = new Intent();
        Bundle bundle1 = getIntent().getExtras();
        String account = bundle1.getString("account");    //获取上一个页面传递的参数，即账号
        Bundle bundle = new Bundle();
        bundle.putString("club_name", club_name);
        bundle.putString("time", time);
        bundle.putString("account", account);
        intent.setClass(MyClubSignInActivity.this,CheckSignActivity.class);    //跳转到查看签到人员界面
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //删除签到活动
    public void deleteSign(final int position, String club_name, String ip){
        String time = String.valueOf(mAdapter.mData.get(position).time);
        //向请求体中插入数据：社团名，时间
        Map<String, String> params = new HashMap<String, String>();
        params.put("name",club_name);
        params.put("time",time);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/delete/delete_club_sign/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                mAdapter.mData.remove(position);    //item数量减一
                mAdapter.notifyDataSetChanged();    //更新列表
                if (response.equals("deleteclubsign")) Toast.makeText(MyClubSignInActivity.this, "删除签到活动成功!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //如果item数为0
    public void ifNoItem(){
        if (mAdapter.getItemCount() == 0) {
            final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MyClubSignInActivity.this);
            alertDialogBuilder.setTitle("暂无签到活动")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyClubSignInActivity.this.finish();
                        }
                    }).show();
        }
    }

    //解析字符串中的JSON数据，并返回JSON数组
    public static ArrayList<club_sign_in> analysisArray(String json, Type type) {
        ArrayList<club_sign_in> mlist = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                club_sign_in o = new Gson().fromJson(String.valueOf(jsonArray.get(i)), type);
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

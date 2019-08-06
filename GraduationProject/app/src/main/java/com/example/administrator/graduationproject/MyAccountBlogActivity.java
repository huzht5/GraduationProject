package com.example.administrator.graduationproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.graduationproject.adapter.account_blog;
import com.example.administrator.graduationproject.adapter.account_blog_adapter;
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
//查看我的动态界面
public class MyAccountBlogActivity extends AppCompatActivity {
    private ServerIP sIP;    //用于获取服务器的IP地址
    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private account_blog_adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);    //加载查看我的动态界面
        TextView txt_topbar = (TextView) findViewById(R.id.txt_topbar);
        txt_topbar.setText("我的动态");    //设置标题
        Bundle bundle=this.getIntent().getExtras();
        String account=bundle.getString("account");    //获取上一个页面传递的参数，账号
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        mLayoutManager = new LinearLayoutManager(MyAccountBlogActivity.this, LinearLayoutManager.VERTICAL, false);
        initdata(account, ip);    //加载我的个人动态
    }

    //加载我的个人动态。参数分别为：账号，服务器ip地址
    public void initdata(final String account, final String ip){
        //向请求体中插入数据：账号
        Map<String, String> params = new HashMap<String, String>();
        params.put("accountid",account);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/query/get_account_blog/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取我的动态的ArrayList
                ArrayList<account_blog> flist = analysisArray(response, account_blog.class);
                mAdapter = new account_blog_adapter(flist);
                initview(account, ip);    //加载界面
            }
        });
        /*String url = "http://" + ip + ":8000/get_account_blog/" + account + "/";
        AsynNetUtils.get(url, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取我的动态的ArrayList
                ArrayList<account_blog> flist = analysisArray(response, account_blog.class);
                mAdapter = new account_blog_adapter(flist);
                initview(account, ip);    //加载界面
            }
        });*/
    }

    //加载界面
    public void initview(String account, String ip){
        mRecycleView = (RecyclerView) findViewById(R.id.my_recycleview);
        // 设置布局管理器
        mRecycleView.addItemDecoration(new DividerDecoration(MyAccountBlogActivity.this));    //添加item间的分界线
        mRecycleView.setLayoutManager(mLayoutManager);
        setAdapter(account, ip);    //设置adapter
    }

    //设置adapter
    public void setAdapter(final String account, final String ip){
        mAdapter.setOnItemClickListener(new account_blog_adapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MyAccountBlogActivity.this);
                alertDialogBuilder.setTitle("删除这个动态？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteBlog(position, account, ip);    //删除动态
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
        mRecycleView.setAdapter(mAdapter);    // 设置adapter
        ifNoItem();    //如果item数为0
    }

    //删除动态
    public void deleteBlog(final int position, String account, String ip){
        String time = String.valueOf(mAdapter.mData.get(position).time);
        //向请求体中插入数据：账号和时间
        Map<String, String> params = new HashMap<String, String>();
        params.put("accountid",account);
        params.put("time",time);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/delete/delete_account_blog/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                mAdapter.mData.remove(position);    //item数量减一
                mAdapter.notifyDataSetChanged();    //更新列表
                if (response.equals("deleteaccountblog")) Toast.makeText(MyAccountBlogActivity.this, "删除动态成功!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //如果item数为0
    public void ifNoItem(){
        if (mAdapter.getItemCount() == 0) {
            final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MyAccountBlogActivity.this);
            alertDialogBuilder.setTitle("你还没发过动态呢，快去发一个吧")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyAccountBlogActivity.this.finish();    //返回上一界面
                        }
                    }).show();
        }
    }

    //解析字符串中的JSON数据，并返回JSON数组
    public static ArrayList<account_blog> analysisArray(String json, Type type) {
        ArrayList<account_blog> mlist = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                account_blog o = new Gson().fromJson(String.valueOf(jsonArray.get(i)), type);
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

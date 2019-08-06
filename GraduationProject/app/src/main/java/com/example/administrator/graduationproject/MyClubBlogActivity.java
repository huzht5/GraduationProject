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

import com.example.administrator.graduationproject.adapter.club_blog;
import com.example.administrator.graduationproject.adapter.club_blog_adapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019-2-13.
 */
//查看我的社团公告界面
public class MyClubBlogActivity extends AppCompatActivity {
    private ServerIP sIP;    //用于获取服务器的IP地址
    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private club_blog_adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);    //加载界面
        TextView txt_topbar = (TextView) findViewById(R.id.txt_topbar);
        txt_topbar.setText("社团公告");    //设置标题
        Bundle bundle=this.getIntent().getExtras();
        String club_name=bundle.getString("club_name");    //获取上一个页面传递的参数，即社团名字
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        mLayoutManager = new LinearLayoutManager(MyClubBlogActivity.this, LinearLayoutManager.VERTICAL, false);
        initdata(club_name, ip);    //加载社团公告
    }

    //加载社团公告
    public void initdata(final String club_name, final String ip){
        //向请求体中插入数据：社团名
        Map<String, String> params = new HashMap<String, String>();
        params.put("name",club_name);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/query/get_club_blog/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取我的社团公告的ArrayList
                ArrayList<club_blog> flist = analysisArray(response, club_blog.class);
                mAdapter = new club_blog_adapter(flist);
                initview(club_name, ip);    //加载界面
            }
        });
    }

    //加载界面
    public void initview(String club_name, String ip){
        mRecycleView = (RecyclerView) findViewById(R.id.my_recycleview);
        // 设置布局管理器
        mRecycleView.addItemDecoration(new DividerDecoration(MyClubBlogActivity.this));    //添加item间的分界线
        mRecycleView.setLayoutManager(mLayoutManager);
        setAdapter(club_name, ip);    //设置adapter
    }

    //设置adapter
    public void setAdapter(final String club_name, final String ip){
        mAdapter.setOnItemClickListener(new club_blog_adapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MyClubBlogActivity.this);
                alertDialogBuilder.setTitle("删除这个公告？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteBlog(position, club_name, ip);    //删除公告
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
        mRecycleView.setAdapter(mAdapter);    // 设置adapter
        ifNoItem();    //如果item数为0
    }

    //删除公告
    public void deleteBlog(final int position, String club_name, String ip){
        String time = String.valueOf(mAdapter.mData.get(position).time);
        //向请求体中插入数据：社团名，时间
        Map<String, String> params = new HashMap<String, String>();
        params.put("name",club_name);
        params.put("time",time);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/delete/delete_club_blog/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                mAdapter.mData.remove(position);    //item数量减一
                mAdapter.notifyDataSetChanged();    //更新列表
                if (response.equals("deleteclubblog")) Toast.makeText(MyClubBlogActivity.this, "删除动态成功!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //如果item数为0
    public void ifNoItem(){
        if (mAdapter.getItemCount() == 0) {
            final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MyClubBlogActivity.this);
            alertDialogBuilder.setTitle("你还没发过公告呢，快去发一个吧")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyClubBlogActivity.this.finish();
                        }
                    }).show();
        }
    }

    //解析字符串中的JSON数据，并返回JSON数组
    public static ArrayList<club_blog> analysisArray(String json, Type type) {
        ArrayList<club_blog> mlist = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                club_blog o = new Gson().fromJson(String.valueOf(jsonArray.get(i)), type);
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

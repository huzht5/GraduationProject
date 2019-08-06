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
 * Created by Administrator on 2019-2-15.
 */
//社团搜索结果界面
public class FindClubResultActivity extends AppCompatActivity {
    private ServerIP sIP;    //用于获取服务器的IP地址
    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private club_data_adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);    //加载社团搜索结果界面
        TextView txt_topbar = (TextView) findViewById(R.id.txt_topbar);
        txt_topbar.setText("搜索结果");    //设置标题
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        mLayoutManager = new LinearLayoutManager(FindClubResultActivity.this, LinearLayoutManager.VERTICAL, false);
        getResult(ip);    //从服务器获取搜索结果
    }

    //从服务器获取搜索结果，参数为：服务器ip地址
    public void getResult(String ip){
        Bundle bundle = this.getIntent().getExtras();
        final String account =bundle.getString("account");    //获取上一个页面传递的参数，即账号
        String search_txt =bundle.getString("search_txt");    //获取上一个页面传递的参数，即搜索内容
        //向请求体中插入数据：搜索内容
        Map<String, String> params = new HashMap<String, String>();
        params.put("mcontent",search_txt);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/query/find_club/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取社团搜索结果的ArrayList
                ArrayList<club_data> flist = analysisArray(response, club_data.class);
                mAdapter = new club_data_adapter(flist);
                initview(account);    //初始化界面
            }
        });
    }

    //初始化界面。参数为：账号
    public void initview(String account){
        mRecycleView = (RecyclerView) findViewById(R.id.my_recycleview);
        // 设置布局管理器
        mRecycleView.addItemDecoration(new DividerDecoration(FindClubResultActivity.this));    //添加item间的分界线
        mRecycleView.setLayoutManager(mLayoutManager);
        setAdapter(account);    //设置adapter
    }

    //设置adapter
    public void setAdapter(final String account){
        mAdapter.setOnItemClickListener(new club_data_adapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Jump(position, account);    //跳转到社团详情界面
            }
        });
        mRecycleView.setAdapter(mAdapter);    // 设置adapter
        ifNoItem();    //如果item数为0
    }

    //跳转。参数分别为：item的位置，账号
    public void Jump(int position, String account){
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        //向下一个页面传递参数：账号、社团名、社长名、社长账号
        bundle.putString("account", account);
        bundle.putString("club_name", mAdapter.mData.get(position).name);
        bundle.putString("owner_name", mAdapter.mData.get(position).ownername);
        bundle.putString("owner_id", mAdapter.mData.get(position).ownerid);
        intent.setClass(FindClubResultActivity.this,ClubInformationActivity.class);    //跳转到社团详情界面
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //如果item数为0
    public void ifNoItem(){
        if (mAdapter.getItemCount() == 0) {
            final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(FindClubResultActivity.this);
            alertDialogBuilder.setTitle("没有找到符合的社团，换个搜索词吧！")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FindClubResultActivity.this.finish();    //返回上一界面
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

package com.example.administrator.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.graduationproject.adapter.account_data;
import com.example.administrator.graduationproject.adapter.account_data_adapter;
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
//发现更多用户界面
public class FindMoreAccountsActivity extends AppCompatActivity {
    private ServerIP sIP;    //用于获取服务器的IP地址
    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private account_data_adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_more);    //加载发现更多用户界面
        TextView txt_topbar = (TextView) findViewById(R.id.txt_topbar);
        txt_topbar.setText("更多用户");    //设置标题
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("推荐用户");    //设置标题
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        mLayoutManager = new LinearLayoutManager(FindMoreAccountsActivity.this, LinearLayoutManager.VERTICAL, false);
        EditText searchText = (EditText) findViewById(R.id.search_text);    //获取搜索内容编辑框
        Button search_button = (Button) findViewById(R.id.search_button);    //获取搜索按钮
        setListerner(searchText, search_button);    //设置监听器
        getAdviceAccounts(ip);    //从服务器获取推荐用户
    }

    //设置监听器。参数分别为：搜索内容编辑框，搜索按钮
    public void setListerner(final EditText searchText, Button search_button){
        //设置搜索按钮的监听器
        search_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String search_txt = searchText.getText().toString();
                //先检查搜索内容是否为空
                if(search_txt.equals("")) Toast.makeText(FindMoreAccountsActivity.this, "搜索内容不能为空！", Toast.LENGTH_SHORT).show();
                else search(search_txt);
            }
        });
    }

    //跳转到用户搜索结果界面
    public void search(String search_txt){
        Intent intent = new Intent();
        Bundle bundle1 = this.getIntent().getExtras();
        String account =bundle1.getString("account");    //获取上一个页面传递的参数，即账号
        Bundle bundle = new Bundle();
        //向下一个页面传递参数：账号，搜索内容
        bundle.putString("account", account);
        bundle.putString("search_txt", search_txt);
        intent.setClass(FindMoreAccountsActivity.this,FindAccountResultActivity.class);    //跳转到用户搜索结果界面
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //从服务器获取推荐社团
    public void getAdviceAccounts(String ip){
        Bundle bundle = this.getIntent().getExtras();
        final String account =bundle.getString("account");    //获取上一个页面传递的参数，即账号
        //向请求体中插入数据：账号
        Map<String, String> params = new HashMap<String, String>();
        params.put("accountid",account);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/query/get_advice_accounts_data/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取推荐用户的ArrayList
                ArrayList<account_data> alist = analysisArray(response, account_data.class);
                mAdapter = new account_data_adapter(alist);
                initview(account);    //初始化界面
            }
        });
        /*String url = "http://" + ip + ":8000/get_advice_accounts_data/" + account + "/";
        AsynNetUtils.get(url, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取推荐用户的ArrayList
                ArrayList<account_data> alist = analysisArray(response, account_data.class);
                mAdapter = new account_data_adapter(alist);
                initview(account);    //初始化界面
            }
        });*/
    }

    //初始化界面。参数为：账号
    public void initview(String account){
        mRecycleView = (RecyclerView) findViewById(R.id.advice_recycleview);
        // 设置布局管理器
        mRecycleView.addItemDecoration(new DividerDecoration(FindMoreAccountsActivity.this));    //添加item间的分界线
        mRecycleView.setLayoutManager(mLayoutManager);
        setAdapter(account);    //设置adapter
    }

    //设置adapter
    public void setAdapter(final String account){
        //设置每个item的点击事件
        mAdapter.setOnItemClickListener(new account_data_adapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Jump(position, account);    //跳转到用户详情界面
            }
        });
        mRecycleView.setAdapter(mAdapter);    // 设置adapter
    }

    //跳转。参数分别为：item的位置，账号
    public void Jump(int position, String account){
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        //向下一个页面传递参数：我的账号、对方的账号
        bundle.putString("account", account);
        bundle.putString("account_id", mAdapter.mData.get(position).id);
        intent.setClass(FindMoreAccountsActivity.this,AccountInformationActivity.class);    //跳转到用户详情界面
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //解析字符串中的JSON数据，并返回JSON数组
    public static ArrayList<account_data> analysisArray(String json, Type type) {
        ArrayList<account_data> mlist = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                account_data o = new Gson().fromJson(String.valueOf(jsonArray.get(i)), type);
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

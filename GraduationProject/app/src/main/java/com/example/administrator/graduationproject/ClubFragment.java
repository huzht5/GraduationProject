package com.example.administrator.graduationproject;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
 * Created by Administrator on 2019-1-27.
 */
//社团公告fragment界面
public class ClubFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private ServerIP sIP;    //用于获取服务器的IP地址
    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private club_blog_adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_club,container,false);    //加载社团公告fragment界面
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        SwipeRefreshLayout mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
        mSwipeLayout.setColorSchemeColors(Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.RED);
        // 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setDistanceToTriggerSync(300);
        // 设定下拉圆圈的背景
        mSwipeLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        // 设置圆圈的大小
        mSwipeLayout.setSize(SwipeRefreshLayout.LARGE);
        //设置下拉刷新的监听
        mSwipeLayout.setOnRefreshListener(this);
        Button followed_club_button=(Button) view.findViewById(R.id.followed_club_button);    //获取查看已关注社团按钮
        Button find_club_button=(Button) view.findViewById(R.id.find_club_button);    //获取发现更多社团按钮
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        setListeners(followed_club_button, find_club_button);    //设置监听器
        getFollowedClubBlogs(ip, view);    //获取已关注的社团的公告
        return view;
    }

    //设置监听器。参数分别为：查看已关注社团按钮，发现更多社团按钮
    public void setListeners(Button followed_club_button, Button find_club_button){
        //设置查看已关注社团按钮的监听器
        followed_club_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle1 = getActivity().getIntent().getExtras();
                String account =bundle1.getString("account");    //获取上一个页面传递的参数，即账号
                Bundle bundle = new Bundle();
                bundle.putString("account", account);    //向下一个页面传递参数：账号
                intent.setClass(getActivity(),MyFollowedClubsActivity.class);    //跳转到查看我关注的社团界面
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //设置发现更多社团按钮的监听器
        find_club_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle1 = getActivity().getIntent().getExtras();
                String account =bundle1.getString("account");    //获取上一个页面传递的参数，即账号
                Bundle bundle = new Bundle();
                bundle.putString("account", account);    //向下一个页面传递参数：账号
                intent.setClass(getActivity(),FindMoreClubsActivity.class);    //跳转到发现更多社团界面
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //获取已关注的社团的公告。参数分别为：服务器ip地址，view
    public void getFollowedClubBlogs(String ip, final View view){
        Bundle bundle = getActivity().getIntent().getExtras();
        String account = bundle.getString("account");    //获取上一个页面传递的参数，即账号
        //向请求体中插入数据：账号
        Map<String, String> params = new HashMap<String, String>();
        params.put("accountid",account);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/query/get_followed_clubs_blog/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取已关注社团的公告的ArrayList
                ArrayList<club_blog> flist = analysisArray(response, club_blog.class);
                mAdapter = new club_blog_adapter(flist);
                initview(view);    //初始化界面
            }
        });
        /*String url = "http://" + ip + ":8000/get_followed_clubs_blog/" + account + "/";
        AsynNetUtils.get(url, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取已关注社团的公告的ArrayList
                ArrayList<club_blog> flist = analysisArray(response, club_blog.class);
                mAdapter = new club_blog_adapter(flist);
                initview(view);    //初始化界面
            }
        });*/
    }

    //初始化界面
    public void initview(View view){
        mRecycleView = (RecyclerView) view.findViewById(R.id.my_club_blog_recycleview);
        // 设置布局管理器
        mRecycleView.addItemDecoration(new DividerDecoration(getActivity()));    //添加item间的分界线
        mRecycleView.setLayoutManager(mLayoutManager);
        setAdapter();    //设置adapter
    }

    //设置adapter
    public void setAdapter(){
        //设置每个item的点击事件
        mAdapter.setOnItemClickListener(new club_blog_adapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("查看这个社团的详情？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Jump(position);    //跳转到社团详情界面
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
        mRecycleView.setAdapter(mAdapter);    // 设置adapter
        ifNoItem();    //如果item数为0
    }

    //跳转，但不finish主界面
    public void Jump(int position){
        Intent intent=new Intent();
        Bundle bundle1 = getActivity().getIntent().getExtras();
        String account =bundle1.getString("account");    //获取上一个页面传递的参数，即账号
        Bundle bundle = new Bundle();
        //向下一个页面传递参数：账号、社团名、社长名、社长账号
        bundle.putString("account", account);
        bundle.putString("club_name", mAdapter.mData.get(position).name);
        bundle.putString("owner_name", mAdapter.mData.get(position).ownername);
        bundle.putString("owner_id", mAdapter.mData.get(position).ownerid);
        intent.setClass(getActivity(),ClubInformationActivity.class);    //跳转到社团详情界面
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //如果item数为0
    public void ifNoItem(){
        if (mAdapter.getItemCount() == 0) {
            final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("暂无社团公告，去关注更多社团吧！")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        }
    }

    //下拉刷新
    public void onRefresh() {
        MainPageActivity ma = (MainPageActivity) getActivity();
        ma.refresh(1);
        Toast.makeText(getActivity(), "刷新成功！", Toast.LENGTH_SHORT).show();
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

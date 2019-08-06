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
import android.widget.Toast;

import com.example.administrator.graduationproject.adapter.account_message;
import com.example.administrator.graduationproject.adapter.account_message_adapter;
import com.example.administrator.graduationproject.localDataBase.LocalMessageInterface;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019-2-21.
 */
//用户消息界面
public class MessageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private ServerIP sIP;    //用于获取服务器的IP地址
    private LocalMessageInterface the_local_message;    //聊天记录本地数据库
    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private account_message_adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_message,container,false);    //加载用户动态fragment界面
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        the_local_message = new LocalMessageInterface(getActivity().getBaseContext());
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
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        getAccountList(view);    //获取已关注的用户的动态
        return view;
    }

    public void getAccountList(View view){
        Bundle bundle = getActivity().getIntent().getExtras();
        String account = bundle.getString("account");    //获取上一个页面传递的参数，即账号
        ArrayList<account_message> flist = the_local_message.getAccountList(account);
        mAdapter = new account_message_adapter(flist);
        mRecycleView = (RecyclerView) view.findViewById(R.id.message_recycleview);
        // 设置布局管理器
        mRecycleView.addItemDecoration(new DividerDecoration(getActivity()));    //添加item间的分界线
        mRecycleView.setLayoutManager(mLayoutManager);
        setAdapter(account);    //设置adapter
    }

    //设置adapter
    public void setAdapter(final String account){
        //设置每个item的点击事件
        mAdapter.setOnItemClickListener(new account_message_adapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                the_local_message.changeState(account, mAdapter.mData.get(position).id, "0");
                if (the_local_message.isAllread(account)){
                    MainPageActivity ma = (MainPageActivity) getActivity();
                    ma.setRed(false);
                }
                Jump(position);    //跳转到用户详情界面
            }
        });
        mAdapter.setOnLongItemClickListener(new account_message_adapter.OnRecyclerViewLongItemClickListener() {
            @Override
            public void onLongItemClick(View view,final int position) {
                final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("删除对话记录？一旦删除不可恢复！")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                the_local_message.deleteDialog(account, mAdapter.mData.get(position).id);    //删除对话记录
                                mAdapter.mData.remove(position);    //item数量减一
                                mAdapter.notifyDataSetChanged();    //更新列表
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
        mRecycleView.setAdapter(mAdapter);    // 设置adapter
    }

    //跳转，但不finish主界面
    public void Jump(int position){
        Intent intent=new Intent();
        Bundle bundle1 = getActivity().getIntent().getExtras();
        String account =bundle1.getString("account");    //获取上一个页面传递的参数，即账号
        Bundle bundle = new Bundle();
        //向下一个页面传递参数：我的账号、对方的账号
        bundle.putString("account", account);
        bundle.putString("account_id", mAdapter.mData.get(position).id);
        bundle.putString("name", mAdapter.mData.get(position).name);
        intent.setClass(getActivity(),ChatActivity.class);    //跳转到聊天界面
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //下拉刷新
    public void onRefresh() {
        MainPageActivity ma = (MainPageActivity) getActivity();
        ma.getAndSetMessage();
    }
}


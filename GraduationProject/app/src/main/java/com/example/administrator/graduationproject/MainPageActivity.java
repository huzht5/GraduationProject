package com.example.administrator.graduationproject;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.administrator.graduationproject.adapter.account_message;
import com.example.administrator.graduationproject.localDataBase.LocalAccountInterface;
import com.example.administrator.graduationproject.localDataBase.LocalMessageInterface;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019-1-23.
 */
//主界面
public class MainPageActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private ServerIP sIP;    //用于获取服务器的IP地址
    private LocalMessageInterface the_local_message;    //聊天记录本地数据库
    private LocalAccountInterface the_local_account;    //用户管理本地数据库
    private RadioGroup rg_tab_bar;    //用于实现切换fragment
    private RadioButton rb_mine;
    private int whichfg;
    private long exitTime;
    //private GestureDetector mDetector;
    //private final static int MIN_MOVE = 200;   //最小距离
    //private MyGestureListener mgListener;    //手势监听器

    //Fragment Object
    private ClubFragment fg1;    //社团公告界面
    private AccountFragment fg2;    //用户动态界面
    private MessageFragment fg3;    //用户消息界面
    private MineFragment fg4;    //我的个人界面
    private FragmentManager fManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);    //加载主界面
        //实例化SimpleOnGestureListener与GestureDetector对象
        //mgListener = new MyGestureListener();
        //mDetector = new GestureDetector(this, mgListener);
        sIP = new ServerIP();
        the_local_message = new LocalMessageInterface(getBaseContext());
        the_local_account = new LocalAccountInterface(getBaseContext());
        setRed(false);
        fManager = getFragmentManager();
        rg_tab_bar = (RadioGroup) findViewById(R.id.rg_tab_bar);
        rg_tab_bar.setOnCheckedChangeListener(MainPageActivity.this);
        //获取第三个单选按钮，并设置其为选中状态
        rb_mine = (RadioButton) findViewById(R.id.rb_mine);
        rb_mine.setChecked(true);
        whichfg = 4;
        getMessage();
    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }
    */

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);    //隐藏所有fragment
        switch (checkedId){
            case R.id.rb_club:
                if(fg1 == null){
                    fg1 = new ClubFragment();
                    fTransaction.add(R.id.ly_content,fg1);    //如果fragment为空，则add
                }else{
                    fTransaction.show(fg1);    //如果fragment不为空，则show
                }
                whichfg = 1;
                break;
            case R.id.rb_account:
                if(fg2 == null){
                    fg2 = new AccountFragment();
                    fTransaction.add(R.id.ly_content,fg2);    //如果fragment为空，则add
                }else{
                    fTransaction.show(fg2);    //如果fragment不为空，则show
                }
                whichfg = 2;
                break;
            case R.id.rb_message:
                if(fg3 == null){
                    fg3 = new MessageFragment();
                    fTransaction.add(R.id.ly_content,fg3);    //如果fragment为空，则add
                }else{
                    fTransaction.show(fg3);    //如果fragment不为空，则show
                }
                whichfg = 3;
                break;
            case R.id.rb_mine:
                if(fg4 == null){
                    fg4 = new MineFragment();
                    fTransaction.add(R.id.ly_content,fg4);    //如果fragment为空，则add
                }else{
                    fTransaction.show(fg4);    //如果fragment不为空，则show
                }
                whichfg = 4;
                break;
        }
        fTransaction.commit();
    }

    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(fg1 != null)fragmentTransaction.hide(fg1);
        if(fg2 != null)fragmentTransaction.hide(fg2);
        if(fg3 != null)fragmentTransaction.hide(fg3);
        if(fg4 != null)fragmentTransaction.hide(fg4);
    }

    //刷新fragment的界面
    public void refresh(int fg){
        FragmentTransaction fTransaction = fManager.beginTransaction();
        if (fg == 1){
            fTransaction.detach(fg1);
            fTransaction.attach(fg1);
        }
        else if (fg == 2){
            fTransaction.detach(fg2);
            fTransaction.attach(fg2);
        }
        else if (fg == 3){
            fTransaction.detach(fg3);
            fTransaction.attach(fg3);
        }
        else if (fg == 4){
            fTransaction.detach(fg4);
            fTransaction.attach(fg4);
        }
        fTransaction.commit();
    }

    public void getMessage(){
        Bundle bundle = this.getIntent().getExtras();
        final String account = bundle.getString("account");    //获取上一个页面传递的参数，即账号
        String ip = sIP.getIp();    //服务器ip地址
        //向请求体中插入数据：账号
        Map<String, String> params = new HashMap<String, String>();
        params.put("accountid",account);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String url = "http://" + ip + ":8000/query/get_message/";
        AsynNetUtils.post(url, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取用户消息ArrayList
                ArrayList<account_message> flist = analysisArray(response, account_message.class);
                setLocalMessage(flist, account);
            }
        });
        /*String url = "http://" + ip + ":8000/get_message/" + account + "/";
        AsynNetUtils.get(url, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //重写onResponse(String response)方法，response为服务器的响应字符串，即JSON数据
                //解析response中的JSON数据获取用户消息ArrayList
                ArrayList<account_message> flist = analysisArray(response, account_message.class);
                setLocalMessage(flist, account);
            }
        });*/
    }

    public void setLocalMessage(ArrayList<account_message> flist, String account){
        if (flist.size() > 0){
            the_local_account.setMessage(account, 1);
            setRed(true);
        }
        for (int i = 0; i < flist.size(); i++){
            String name = flist.get(i).name;
            String id = flist.get(i).id;
            String time = flist.get(i).time;
            String message = flist.get(i).message;
            String state = flist.get(i).state;
            the_local_message.AddMessage(account, id, name, "0", time, message, state);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getAndSetMessage();
    }

    public void getAndSetMessage(){
        getMessage();
        Bundle bundle = this.getIntent().getExtras();
        String account = bundle.getString("account");    //获取上一个页面传递的参数，即账号
        if (fg3 != null && the_local_account.getMessage(account) == 1){
            refresh(3);
            the_local_account.setMessage(account, 0);
        }
    }

    public void setRed(boolean flag){
        ImageView red = (ImageView) findViewById(R.id.red);
        if (flag) red.setVisibility(View.VISIBLE);
        else red.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime)>2000 ){
            Toast.makeText(this, "再按一次退出校园社团APP", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
            return;
        }
        super.onBackPressed();
    }

    //解析字符串中的JSON数据，并返回JSON数组
    public static ArrayList<account_message> analysisArray(String json, Type type) {
        ArrayList<account_message> mlist = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                account_message o = new Gson().fromJson(String.valueOf(jsonArray.get(i)), type);
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

    //下滑手势：刷新三个fragment
    /*
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v1) {
            if(e1.getY() - e2.getY()  < MIN_MOVE){
                FragmentTransaction fTransaction = fManager.beginTransaction();
                if(fg1 != null){
                    fTransaction.detach(fg1);
                    fTransaction.attach(fg1);
                }
                if(fg2 != null){
                    fTransaction.detach(fg2);
                    fTransaction.attach(fg2);
                }
                if(fg3 != null){
                    fTransaction.detach(fg3);
                    fTransaction.attach(fg3);
                }
                fTransaction.commit();
                Toast.makeText(MainPageActivity.this,"刷新成功！",Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }
    */
}

package com.example.administrator.graduationproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019-2-13.
 */
//发布社团公告界面
public class CreateClubBlogActivity extends AppCompatActivity {
    private ServerIP sIP;    //用于获取服务器的IP地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_blog);    //加载发布社团公告界面
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("发布社团公告");    //设置标题
        sIP = new ServerIP();
        String ip = sIP.getIp();    //服务器ip地址
        Bundle bundle = getIntent().getExtras();
        String club_name =bundle.getString("club_name");    //获取上一个页面传递的参数，即社团名
        EditText message_txt = (EditText) findViewById(R.id.message);
        message_txt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});    //限制输入框的输入长度
        message_txt.setFocusable(true);
        message_txt.setFocusableInTouchMode(true);
        message_txt.requestFocus();
        //显示软键盘
        CreateClubBlogActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //如果上面的代码没有弹出软键盘 可以使用下面另一种方式
        //InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.showSoftInput(editText, 0);
        Button create_button = (Button) findViewById(R.id.create);
        setListener(message_txt, create_button, club_name, ip);    //设置监听器
    }

    //设置监听器。参数分别为：内容，发布按钮，社团名，服务器ip地址
    public void setListener(final EditText message_txt, Button create_button, final String club_name, final String ip){
        create_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String message = message_txt.getText().toString();
                if(message.equals("")) Toast.makeText(CreateClubBlogActivity.this, "公告内容不能为空！", Toast.LENGTH_SHORT).show();
                else createAccountBlog(club_name, message, ip);    //发布社团公告
            }
        });
    }

    //发布社团公告。参数分别为：社团名，内容，服务器ip地址
    public void createAccountBlog(String club_name, String message, String ip){
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String str_time = sdf.format(dt);    //获取当前时间
        //向请求体中插入数据：社团名，时间，内容
        Map<String, String> params = new HashMap<String, String>();
        params.put("name",club_name);
        params.put("time",str_time);
        params.put("message",message);
        String content = getRequestData(params, "UTF-8").toString();    //生成请求体
        String murl = "http://" + ip + ":8000/insert/create_club_blog/";
        AsynNetUtils.post(murl, content, new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                if (response.equals("createclubblog")) {
                    Toast.makeText(CreateClubBlogActivity.this, "发布公告成功!", Toast.LENGTH_SHORT).show();
                    CreateClubBlogActivity.this.finish();
                }
            }
        });
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

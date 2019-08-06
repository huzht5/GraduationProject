package com.example.administrator.graduationproject.localDataBase;

import android.content.Context;

/**
 * Created by Administrator on 2019-1-22.
 */
//这个类是本地账号数据库管理的接口类
public class LocalAccountInterface {
    static LocalAccountManager local_account;

    public LocalAccountInterface(Context context){
        local_account=new LocalAccountManager(context);
    }

    //获取已登录的账号
    public String GetTheAccount(){
        return local_account.GetTheAccount();
    }

    //清除所有账号
    public void deleteAllAccounts(){
        local_account.deleteAllAccounts();
    }

    //改变账号的登录状态
    public void ChangeState(String account,int state){
        local_account.ChangeState(account,state);
    }

    //改变某个账号的消息标志。参数分别为：账号，标志
    public void setMessage(String account,int message){
        local_account.setMessage(account, message);
    }

    //获取消息标志位
    public int getMessage(String account){
        return local_account.getMessage(account);
    }
}

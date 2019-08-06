package com.example.administrator.graduationproject;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Administrator on 2019-5-4.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginAndRegisterTest {
    @Rule
    public ActivityTestRule<LoginAndRegisterActivity> mActivityRule = new ActivityTestRule<>(
            LoginAndRegisterActivity.class);

    //测试未输入账号密码就点击注册或登录按钮，弹出错误提示
    @Test
    public void Nothing(){
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.login_button)).perform(click());
    }

    //测试当只输入账号就点击注册或登录按钮，弹出错误提示
    @Test
    public void noPassword(){
        onView(withId(R.id.text_userid)).perform(typeText("12345"), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.login_button)).perform(click());
    }

    //测试当只输入密码就点击注册或登录按钮，弹出错误提示
    @Test
    public void noAccount(){
        onView(withId(R.id.text_userpwd)).perform(typeText("12345"), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.login_button)).perform(click());
    }

    @Test
    //测试使用已注册账号能否注册成功。使用账号为1，密码为1的账号测试，结果应该为：注册失败并提示此账号已注册。
    public void Registered() {
        onView(withId(R.id.text_userid)).perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.text_userpwd)).perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());
    }

    @Test
    //测试使用未注册账号能否登录成功。使用账号为6，密码为6的账号测试，结果应该为：登录失败并提示此账号未注册。
    public void noRegister() {
        onView(withId(R.id.text_userid)).perform(typeText("6"), closeSoftKeyboard());
        onView(withId(R.id.text_userpwd)).perform(typeText("6"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
    }

    @Test
    //测试使用已注册账号，错误密码登录是否成功。使用账号为1，密码为2的账号测试，结果应该为：登录失败并提示密码错误。
    public void wrongPassword() {
        onView(withId(R.id.text_userid)).perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.text_userpwd)).perform(typeText("2"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
    }

    @Test
    //测试使用已注册账号，正确密码登录是否成功。使用账号为1，密码为1的账号测试，结果应该为：登录成功并跳转到主界面。
    public void loginSucceed() {
        onView(withId(R.id.text_userid)).perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.text_userpwd)).perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.rb_account)).perform(click(), closeSoftKeyboard());
        onView(withId(R.id.find_account_button)).perform(click());
        onView(withId(R.id.search_text)).perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.search_button)).perform(click());
    }
}

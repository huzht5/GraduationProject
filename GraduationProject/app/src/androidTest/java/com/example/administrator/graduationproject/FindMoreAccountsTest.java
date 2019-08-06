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
public class FindMoreAccountsTest {

    @Rule
    public ActivityTestRule<LoginAndRegisterActivity> mActivityRule = new ActivityTestRule<>(
            LoginAndRegisterActivity.class);

    //测试未输入搜索内容就点击搜索按钮，弹出错误提示
    @Test
    public void NoContent(){
        LoginAndGotoFindActivity();
        onView(withId(R.id.search_button)).perform(click());
    }

    //测试输入搜索内容“1”
    @Test
    public void Search1(){
        LoginAndGotoFindActivity();
        onView(withId(R.id.search_text)).perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.search_button)).perform(click());
    }

    //测试输入搜索内容“2”
    @Test
    public void Search2(){
        LoginAndGotoFindActivity();
        onView(withId(R.id.search_text)).perform(typeText("2"), closeSoftKeyboard());
        onView(withId(R.id.search_button)).perform(click());
    }

    //先登录账号为1的账号
    public void LoginAndGotoFindActivity(){
        onView(withId(R.id.text_userid)).perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.text_userpwd)).perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.rb_account)).perform(click(), closeSoftKeyboard());
        onView(withId(R.id.find_account_button)).perform(click());
    }
}

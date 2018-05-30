package com.hm.iou.msg.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hm.iou.msg.business.HelpCenterActivity;
import com.hm.iou.msg.business.feedback.view.FeedbackActivity;
import com.hm.iou.msg.business.feedback.view.HistoryFeedbackActivity;
import com.hm.iou.msg.business.message.view.MsgCenterActivity;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.sharedata.model.UserInfo;
import com.hm.iou.tools.ToastUtil;
import com.sina.weibo.sdk.utils.MD5;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HistoryFeedbackActivity.class));
            }
        });

        findViewById(R.id.btn_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
                startActivity(new Intent(MainActivity.this, HelpCenterActivity.class));
            }
        });

        findViewById(R.id.btn_msgCenter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MsgCenterActivity.class));
            }
        });
    }

    private void login() {
        String pwd = MD5.hexdigest("123456".getBytes());
        MobileLoginReqBean reqBean = new MobileLoginReqBean();
        reqBean.setMobile("15967132742");
        reqBean.setQueryPswd(pwd);
        HttpReqManager.getInstance().getService(JietiaoService.class)
                .mobileLogin(reqBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<UserInfo>>() {
                    @Override
                    public void accept(BaseResponse<UserInfo> userInfoBaseResponse) throws Exception {
                        ToastUtil.showMessage(MainActivity.this, "登录成功");
                        UserInfo userInfo = userInfoBaseResponse.getData();
                        UserManager.getInstance(MainActivity.this).updateOrSaveUserInfo(userInfo);
                        HttpReqManager.getInstance().setUserId(userInfo.getUserId());
                        HttpReqManager.getInstance().setToken(userInfo.getToken());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        t.printStackTrace();
                    }
                });
    }

}

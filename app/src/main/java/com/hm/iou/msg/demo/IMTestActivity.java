package com.hm.iou.msg.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * @author syl
 * @time 2019/4/3 2:22 PM
 */

public class IMTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_test);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void doLogin() {
//        LoginInfo info = new LoginInfo("111111", "22222"); // config...
//        RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
//            @Override
//            public void onSuccess(LoginInfo param) {
//
//            }
//
//            @Override
//            public void onFailed(int code) {
//
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//
//            }
//            // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
//        };
//        NIMClient.getService(AuthService.class).login(info).setCallback(callback);
    }
}

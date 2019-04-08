package com.hm.iou.msg.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hm.iou.logger.Logger;
import com.hm.iou.tools.Md5Util;
import com.hm.iou.tools.ToastUtil;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.session.activity.P2PMessageActivity;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

/**
 * @author syl
 * @time 2019/4/3 2:22 PM
 */

public class IMTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_test);

        findViewById(R.id.btn_login_nim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        findViewById(R.id.btn_chat_with_another).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactId = "p_58824674063828429";
                P2PMessageActivity.start(IMTestActivity.this, contactId, null, null);
            }
        });
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {

            }
        });
    }

    public void doLogin() {
        String account = "15267163669";
        String token = Md5Util.getMd5ByString("123456");
        LoginInfo loginInfo = new LoginInfo(account, token);
        RequestCallback requestCallback = new RequestCallback() {
            @Override
            public void onSuccess(Object o) {
                Logger.i("测试登陆接口", "login success");
                ToastUtil.showMessage(IMTestActivity.this, "登陆成功");

            }

            @Override
            public void onFailed(int code) {
                Logger.i("测试登陆接口", "login failed");
                if (code == 302 || code == 404) {
                    ToastUtil.showMessage(IMTestActivity.this, "账号或密码错误");
                } else {
                    ToastUtil.showMessage(IMTestActivity.this, "登录失败: " + code);
                }
            }

            @Override
            public void onException(Throwable throwable) {
                Logger.i("测试登陆接口", "login exception");
                ToastUtil.showMessage(IMTestActivity.this, "无效输入");
            }
        };
        NimUIKit.login(loginInfo, requestCallback);
    }
}

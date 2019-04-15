package com.hm.iou.msg.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hm.iou.logger.Logger;
import com.hm.iou.tools.ToastUtil;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

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
//                String account = ((EditText) findViewById(R.id.et_another_account)).getText().toString();
                NimUIKit.startP2PSession(IMTestActivity.this, "1000294");//方超
//                NimUIKit.startP2PSession(IMTestActivity.this, "1000393");//石磊
            }
        });
        findViewById(R.id.btn_get_chat_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChatList();
            }
        });
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {

            }
        });
//        MsgCenterAppLike.getInstance().initIM();
    }

    public void doLogin() {
//        String account = ((EditText) findViewById(R.id.et_account)).getText().toString();
//        String token = ((EditText) findViewById(R.id.et_token)).getText().toString();
        String account = "1000393";//石磊
        String token = "3ee5b627b483df7dad89ca1eeb5371bc";
//        String account = "1000294";//方超
//        String token = "5a5c5dd3f195bb0bf05e0de00b2237f6";
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

    public void getChatList() {
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onResult(int code, List<RecentContact> recents, Throwable e) {
                if (recents == null) {
                    return;
                }
                // recents参数即为最近联系人列表（最近会话列表）
                for (int i = 0; i < recents.size(); i++) {
                    Logger.d("getFromNick===" + recents.get(i).getFromNick());
                    Logger.d("getFromAccount===" + recents.get(i).getFromAccount());
                }
            }
        });
    }


}

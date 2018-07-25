package com.hm.iou.msg.demo;

import android.app.Application;

import com.hm.iou.base.BaseBizAppLike;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.MsgCenterAppLike;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.network.HttpRequestConfig;
import com.hm.iou.router.Router;
import com.hm.iou.sharedata.UserManager;
import com.orm.SugarContext;

/**
 * Created by hjy on 18/5/11.<br>
 */

public class MsgCenterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init(this, true);
        Router.init(this);
        BaseBizAppLike appLike = new BaseBizAppLike();
        appLike.onCreate(this);
        appLike.initServer("http://192.168.1.217", "http://192.168.1.217",
                "http://192.168.1.217");
        initNetwork();
        SugarContext.init(this);
        MsgCenterAppLike msgCenterAppLike = new MsgCenterAppLike();
        msgCenterAppLike.onCreate(this);
    }

    private void initNetwork() {
        System.out.println("init-----------");
        HttpRequestConfig config = new HttpRequestConfig.Builder(this)
                .setDebug(true)
                .setAppChannel("guanfang")
                .setAppVersion("1.0.2")
                .setDeviceId("123abc123")
                .setBaseUrl(BaseBizAppLike.getInstance().getApiServer())
                .setUserId(UserManager.getInstance(this).getUserId())
                .setToken(UserManager.getInstance(this).getToken())
                .build();
        HttpReqManager.init(config);
    }

}
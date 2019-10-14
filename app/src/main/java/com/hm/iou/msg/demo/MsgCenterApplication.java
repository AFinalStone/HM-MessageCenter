package com.hm.iou.msg.demo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.hm.iou.base.BaseBizAppLike;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.MsgCenterAppLike;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.network.HttpRequestConfig;
import com.hm.iou.router.Router;
import com.hm.iou.sharedata.UserManager;
import com.orm.SugarContext;

/**
 * @author syl
 * @time 2019/4/4 2:08 PM
 */
public class MsgCenterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init(this, true);
        Router.init(this);
        BaseBizAppLike appLike = new BaseBizAppLike();
        appLike.onCreate(this);
//        appLike.initServer("http://dev.54jietiao.com", "http://dev.54jietiao.com",
//                "http://dev.54jietiao.com");
        appLike.initServer("http://192.168.1.107:3000", "http://192.168.1.107:3000",
                "http://192.168.1.107:3000");
//        appLike.initServer("http://re.54jietiao.com", "http://re.54jietiao.com",
//                "http://re.54jietiao.com");
        //数据库缓存
        SugarContext.init(this);
        //消息中心
        MsgCenterAppLike msgCenterAppLike = new MsgCenterAppLike();
        msgCenterAppLike.onCreate(this);
    }


    /**
     * 分包
     */
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

}
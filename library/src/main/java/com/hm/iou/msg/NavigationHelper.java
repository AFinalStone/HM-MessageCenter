package com.hm.iou.msg;

import android.content.Context;
import android.content.Intent;

import com.hm.iou.router.Router;

/**
 * @author syl
 * @time 2018/5/19 下午2:59
 */
public class NavigationHelper {

    /**
     * 跳转到消息详情
     *
     * @param context
     */
    public static void ToMsgDetail(Context context, String url) {
//        try {
//            Class WebViewH5Activity = Class.forName("com.hm.iou.base.webview.BaseWebviewActivity");
//            Intent intent = new Intent(context, WebViewH5Activity);
//            intent.putExtra("url", url);
//            context.startActivity(intent);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        Router.getInstance()
                .buildWithUrl("hmiou://m.54jietiao.com/webview/index")
                .withString("url", url)
                .navigation(context);
    }


}

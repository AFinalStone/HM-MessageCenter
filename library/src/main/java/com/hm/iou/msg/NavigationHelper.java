package com.hm.iou.msg;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

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
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Router.getInstance()
                .buildWithUrl("hmiou://m.54jietiao.com/webview/index")
                .withString("url", url)
                .navigation(context);
    }


}

package com.hm.iou.msg;

import android.content.Context;
import android.text.TextUtils;

import com.hm.iou.msg.dict.MsgType;
import com.hm.iou.router.Router;
import com.netease.nim.uikit.business.session.activity.P2PMessageActivity;

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
    public static void toMsgDetail(Context context, String url, String autoId, int msgType) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.startsWith("hmiou")) {
            Router.getInstance().buildWithUrl(url).navigation(context);
            return;
        }
        if (MsgType.TopNews.getValue() == msgType && !TextUtils.isEmpty(autoId)) {
            Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/news/detail")
                    .withString("news_id", autoId + "")
                    .withString("url", url)
                    .withString("showtitle", "false")
                    .navigation(context);
            return;
        }
        Router.getInstance()
                .buildWithUrl("hmiou://m.54jietiao.com/webview/index")
                .withString("url", url)
                .navigation(context);
    }

    /**
     * 跳转通讯录
     *
     * @param context
     */
    public static void toFriendList(Context context) {

    }

    /**
     * 跳转到合同详情
     *
     * @param context
     */
    public static void toContractMsgDetailPage(Context context) {

    }

    /**
     * 跳转到待还提醒
     *
     * @param context
     */
    public static void toRemaindItemDetailPage(Context context) {

    }

    /**
     * 会话详情
     *
     * @param context
     */
    public static void toSessionDetail(Context context, String sessionId) {
        P2PMessageActivity.start(context, sessionId, null, null);
    }


}

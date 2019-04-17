package com.hm.iou.msg;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.hm.iou.msg.business.apply.view.ApplyNewFriendListActivity;
import com.hm.iou.msg.business.friend.view.AddFriendIndexActivity;
import com.hm.iou.msg.business.friend.view.FriendDetailActivity;
import com.hm.iou.msg.business.friendlist.view.FriendListActivity;
import com.hm.iou.base.utils.RouterUtil;
import com.hm.iou.msg.business.friend.view.ReportUserActivity;
import com.hm.iou.msg.business.friend.view.SendVerifyRequestActivity;
import com.hm.iou.msg.dict.MsgType;
import com.hm.iou.router.Router;
import com.netease.nim.uikit.api.NimUIKit;
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
        context.startActivity(new Intent(context, FriendListActivity.class));
    }

    /**
     * 新的朋友列表
     *
     * @param context
     */
    public static void toApplyNewFriendList(Context context) {
        context.startActivity(new Intent(context, ApplyNewFriendListActivity.class));
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
    public static void toSessionDetail(Context context, String fromAccount) {
        NimUIKit.startP2PSession(context, fromAccount);
    }

    public static void toMyCardPage(Context context) {
        RouterUtil.clickMenuLink(context, "hmiou://m.54jietiao.com/qrcode/index?show_type=show_my_card");
    }

    public static void toScanQrCodePage(Context context) {
        RouterUtil.clickMenuLink(context, "hmiou://m.54jietiao.com/qrcode/index?show_type=show_scan_code");
    }

    /**
     * 好友举报页面
     *
     * @param context
     * @param userId  好友的userId
     */
    public static void toFriendReportPage(Context context, String userId) {
        Intent intent = new Intent(context, ReportUserActivity.class);
        intent.putExtra(ReportUserActivity.EXTRA_KEY_FRIEND_ID, userId);
        context.startActivity(intent);
    }

    /**
     * 跳转到好友申请页面
     *
     * @param context
     * @param friendId
     */
    public static void toSendVerifyRequestPage(Context context, String friendId) {
        Intent intent = new Intent(context, SendVerifyRequestActivity.class);
        intent.putExtra(SendVerifyRequestActivity.EXTRA_KEY_USER_ID, friendId);
        context.startActivity(intent);
    }

    /**
     * 跳转到好友详情页面
     *
     * @param context
     * @param friendId    好友的用户id
     * @param applyStatus 从好友申请列表进入时，需要传入此参数。1-已过期，2-等待确认。{@link FriendDetailActivity.APPLY_OVERDUE}
     * @param applyMsg
     */
    public static void toFriendDetailPage(Context context, String friendId, String applyStatus, String applyMsg) {
        Intent intent = new Intent(context, FriendDetailActivity.class);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_USER_ID, friendId);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_APPLY_STATUS, applyStatus);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_COMMENT_INFO, applyMsg);
        context.startActivity(intent);
    }

    /**
     * 添加好友
     *
     * @param context
     */
    public static void toAddNewFriend(Context context) {
        Intent intent = new Intent(context, AddFriendIndexActivity.class);
        context.startActivity(intent);
    }

}
package com.hm.iou.msg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.hm.iou.base.utils.RouterUtil;
import com.hm.iou.msg.business.alipay.list.view.IAliPayMsgItem;
import com.hm.iou.msg.business.friend.view.AddFriendIndexActivity;
import com.hm.iou.msg.business.friend.view.FriendDetailActivity;
import com.hm.iou.msg.business.friend.view.ReportUserActivity;
import com.hm.iou.msg.business.friend.view.SendVerifyRequestActivity;
import com.hm.iou.msg.business.friendlist.view.FriendListActivity;
import com.hm.iou.msg.business.message.view.SessionDetailPreviewActivity;
import com.hm.iou.msg.dict.HMMsgType;
import com.hm.iou.router.Router;
import com.netease.nim.uikit.api.NimUIKit;

/**
 * @author syl
 * @time 2018/5/19 下午2:59
 */
public class NavigationHelper {

    /**
     * 跳转到管家消息详情
     *
     * @param context
     */
    public static void toHMMsgDetail(Context context, String url, String autoId, int msgType) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.startsWith("hmiou")) {
            Router.getInstance().buildWithUrl(url).navigation(context);
            return;
        }
        if (HMMsgType.News.getValue() == msgType && !TextUtils.isEmpty(autoId)) {
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
     * 跳转到合同详情
     *
     * @param context
     */
    public static void toMsgDetailPage(Context context, String jumpUrl) {
        RouterUtil.clickMenuLink(context, jumpUrl);
    }

    /**
     * 跳转到支付宝消息详情
     *
     * @param context
     */
    public static void toAliPayMsgDetailPage(Context context, IAliPayMsgItem item) {
        Router.getInstance()
                .buildWithUrl(item.getIJumpUrl())
//                .withInt("email_id", 17)
//                .withInt("type", 401)
                .navigation(context);
    }

    /**
     * 会话详情
     *
     * @param context
     */
    public static void toSessionDetail(Context context, String friendId, String friendImId) {
        Intent intent = new Intent(context, SessionDetailPreviewActivity.class);
        intent.putExtra(SessionDetailPreviewActivity.StaticParams.getEXTRA_KEY_FRIEND_ID(), friendId);
        intent.putExtra(SessionDetailPreviewActivity.StaticParams.getEXTRA_KEY_FRIEND_IM_ID(), friendImId);
        context.startActivity(intent);
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
     * @param userId
     * @param reqCode
     */
    public static void toSendVerifyRequestPage(Activity context, String userId, int reqCode) {
        Intent intent = new Intent(context, SendVerifyRequestActivity.class);
        intent.putExtra(SendVerifyRequestActivity.EXTRA_KEY_FRIEND_ID, userId);
        intent.putExtra(SendVerifyRequestActivity.EXTRA_KEY_ID_TYPE, SendVerifyRequestActivity.EXTRA_ID_TYPE_USER_ID);
        context.startActivityForResult(intent, reqCode);
    }

    /**
     * 跳转到好友申请页面
     *
     * @param context
     * @param imId
     */
    public static void toSendVerifyRequestPage(Context context, String imId) {
        Intent intent = new Intent(context, SendVerifyRequestActivity.class);
        intent.putExtra(SendVerifyRequestActivity.EXTRA_KEY_FRIEND_ID, imId);
        intent.putExtra(SendVerifyRequestActivity.EXTRA_KEY_ID_TYPE, SendVerifyRequestActivity.EXTRA_ID_TYPE_IM_ID);
        context.startActivity(intent);
    }

    /**
     * 跳转到好友详情页面
     *
     * @param context
     * @param friendId    好友的用户id
     * @param applyStatus 从好友申请列表进入时，需要传入此参数。1-已过期，2-等待确认。{@link }
     * @param applyMsg
     */
    public static void toFriendDetailPage(Context context, String friendId, String applyStatus, String applyMsg) {
        Intent intent = new Intent(context, FriendDetailActivity.class);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_USER_ID, friendId);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_APPLY_STATUS, applyStatus);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_COMMENT_INFO, applyMsg);
        context.startActivity(intent);
    }

    public static void toMyDetailPageFromSession(Context context, String friendId) {
        Intent intent = new Intent(context, FriendDetailActivity.class);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_USER_ID, friendId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 从会话里点击头像进入详情页
     *
     * @param context
     * @param imAccId
     */
    public static void toFriendDetailPageFromSession(Context context, String imAccId) {
        Intent intent = new Intent(context, FriendDetailActivity.class);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_USER_ID, imAccId);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_ID_TYPE, FriendDetailActivity.ID_TYPE_IM);
        //需要设置 flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    /**
     * 邀请好友添加自己
     *
     * @param context
     */
    public static void toAddMySelf(Context context) {
        Router.getInstance()
                .buildWithUrl("hmiou://m.54jietiao.com/qrcode/personal_card")
                .navigation(context);
    }

}
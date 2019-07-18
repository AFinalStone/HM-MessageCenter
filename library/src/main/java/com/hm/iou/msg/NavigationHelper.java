package com.hm.iou.msg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.hm.iou.base.utils.RouterUtil;
import com.hm.iou.msg.bean.FriendInfo;
import com.hm.iou.msg.business.alipay.list.view.IAliPayMsgItem;
import com.hm.iou.msg.business.friend.view.AccountClosedActivity;
import com.hm.iou.msg.business.friend.view.AddFriendIndexActivity;
import com.hm.iou.msg.business.friend.view.BlackNameActivity;
import com.hm.iou.msg.business.friend.view.FriendDetailActivity;
import com.hm.iou.msg.business.friend.view.ReportUserActivity;
import com.hm.iou.msg.business.friend.view.SendVerifyRequestActivity;
import com.hm.iou.msg.business.friend.view.WaitFriendProcessActivity;
import com.hm.iou.msg.business.friendlist.view.FriendListActivity;
import com.hm.iou.msg.dict.HMMsgType;
import com.hm.iou.msg.dict.IdType;
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
     * @param userId
     * @param isAddFriend true-表示添加好友，false-表示确认好友申请
     * @param friendInfo  好友信息，从添加好友界面进来，需传此参数
     * @param reqCode
     */
    public static void toSendVerifyRequestPage(Activity context, String userId, boolean isAddFriend, FriendInfo friendInfo, int reqCode) {
        Intent intent = new Intent(context, SendVerifyRequestActivity.class);
        intent.putExtra(SendVerifyRequestActivity.EXTRA_KEY_FRIEND_ID, userId);
        intent.putExtra(SendVerifyRequestActivity.EXTRA_KEY_ID_TYPE, IdType.COMM.type);
        intent.putExtra(SendVerifyRequestActivity.EXTRA_KEY_IS_ADD_FRIEND, isAddFriend);
        intent.putExtra(SendVerifyRequestActivity.EXTRA_KEY_FRIEND_INFO, friendInfo);
        context.startActivityForResult(intent, reqCode);
    }

    /**
     * 跳转到好友申请页面
     *
     * @param context
     * @param imId    云信id
     */
    public static void toSendVerifyRequestPage(Context context, String imId) {
        Intent intent = new Intent(context, SendVerifyRequestActivity.class);
        intent.putExtra(SendVerifyRequestActivity.EXTRA_KEY_FRIEND_ID, imId);
        intent.putExtra(SendVerifyRequestActivity.EXTRA_KEY_ID_TYPE, IdType.IM_ACC_ID.type);
        intent.putExtra(SendVerifyRequestActivity.EXTRA_KEY_IS_ADD_FRIEND, true);
        context.startActivity(intent);
    }

    /**
     * 跳转到好友详情页面
     *
     * @param context
     * @param friendId 好友的用户id
     */
    public static void toFriendDetailPage(Context context, String friendId) {
        Intent intent = new Intent(context, FriendDetailActivity.class);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_USER_ID, friendId);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_ID_TYPE, IdType.COMM.type);
        context.startActivity(intent);
    }

    /**
     * 到好友添加确认页面
     *
     * @param context
     * @param friendId
     * @param applyStatus 从好友申请列表进入时，需要传入此参数。1-已过期，2-等待确认。{@link }
     * @param applyId     好友申请记录的申请id
     * @param remark      备注名
     */
    public static void toFriendConfirmPage(Context context, String friendId, String applyStatus, String applyId, String remark) {
        Intent intent = new Intent(context, FriendDetailActivity.class);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_USER_ID, friendId);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_ID_TYPE, IdType.COMM.type);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_APPLY_STATUS, applyStatus);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_APPLY_ID, applyId);
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_REMARK_NAME, remark);
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
        intent.putExtra(FriendDetailActivity.EXTRA_KEY_ID_TYPE, IdType.IM_ACC_ID.type);
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

    /**
     * 返回到消息中心
     *
     * @param context
     */
    public static void toMessageCenter(Context context) {
        RouterUtil.clickMenuLink(context, "hmiou://m.54jietiao.com/main/index?tab_type=tag_message");
    }

    /**
     * 进入黑名单界面
     *
     * @param context
     * @param friendId       好友id
     * @param desc           拉黑原因
     * @param blockedByOther 是否被别人拉黑
     * @param friendInfo     好友信息
     */
    public static void toBlackNamePage(Context context, String friendId, String desc, boolean blockedByOther, FriendInfo friendInfo) {
        Intent intent = new Intent(context, BlackNameActivity.class);
        intent.putExtra(BlackNameActivity.EXTRA_KEY_FRIEND_ID, friendId);
        intent.putExtra(BlackNameActivity.EXTRA_KEY_DESC, desc);
        intent.putExtra(BlackNameActivity.EXTRA_KEY_BLOCKED_BY_OTHER, blockedByOther);
        intent.putExtra(BlackNameActivity.EXTRA_KEY_FRIEND_INFO, friendInfo);
        context.startActivity(intent);
    }

    /**
     * 进入等待好友处理页面
     *
     * @param context
     * @param friendId
     * @param sex      性别
     * @param avatar   头像
     * @param desc     申请描述信息
     * @param over48h  是否超过48小时
     */
    public static void toWaitProcessPage(Context context, String friendId, int sex, String avatar, String desc, boolean over48h) {
        Intent intent = new Intent(context, WaitFriendProcessActivity.class);
        intent.putExtra(WaitFriendProcessActivity.EXTRA_KEY_FRIEND_ID, friendId);
        intent.putExtra(WaitFriendProcessActivity.EXTRA_KEY_SEX, sex);
        intent.putExtra(WaitFriendProcessActivity.EXTRA_KEY_AVATAR, avatar);
        intent.putExtra(WaitFriendProcessActivity.EXTRA_KEY_DESC, desc);
        intent.putExtra(WaitFriendProcessActivity.EXTRA_KEY_OVER_48H, over48h);
        context.startActivity(intent);
    }

    /**
     * 进入好友账号已注销或已拉黑界面
     *
     * @param context
     * @param sex      性别
     * @param avatar   头像
     * @param desc     描述信息
     * @param isClosed true-表示账户注销，false-表示账户被平台拉黑
     */
    public static void toAccountClosedPage(Context context, int sex, String avatar, String desc, boolean isClosed) {
        Intent intent = new Intent(context, AccountClosedActivity.class);
        intent.putExtra(AccountClosedActivity.EXTRA_KEY_SEX, sex);
        intent.putExtra(AccountClosedActivity.EXTRA_KEY_AVATAR, avatar);
        intent.putExtra(AccountClosedActivity.EXTRA_KEY_DESC, desc);
        intent.putExtra(AccountClosedActivity.EXTRA_KEY_TITLE, isClosed ? "【账号已注销】" : "【账户被拉黑】");
        context.startActivity(intent);
    }

}
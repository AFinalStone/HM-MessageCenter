package com.hm.iou.msg.util;

import android.content.Context;

import com.hm.iou.database.FriendDbUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.msg.MsgCenterConstants;
import com.hm.iou.msg.bean.UnReadMsgNumBean;
import com.hm.iou.tools.ACache;
import com.hm.iou.tools.StringUtil;

import static com.hm.iou.msg.MsgCenterConstants.KEY_LAST_FRIEND_APPLY_RECORD_TIME;
import static com.hm.iou.msg.MsgCenterConstants.KEY_LAST_FRIEND_UPDATE_TIME;

/**
 * @author syl
 * @time 2018/5/30 下午6:57
 */
public class CacheDataUtil {

    /**
     * 设置未读消息数量
     *
     * @return
     */
    public static void setNoReadMsgNum(Context context, UnReadMsgNumBean unReadMsgNumBean) {
        ACache.get(context.getApplicationContext()).put(MsgCenterConstants.KEY_UN_READ_MSG_NUM, unReadMsgNumBean);
    }

    /**
     * 获取未读消息数量
     *
     * @return
     */
    public static UnReadMsgNumBean getNoReadMsgNum(Context context) {
        ACache cache = ACache.get(context.getApplicationContext());
        return (UnReadMsgNumBean) cache.getAsObject(MsgCenterConstants.KEY_UN_READ_MSG_NUM);
    }

    /**
     * 通讯录
     *
     * @param context
     * @return
     */
    public static String getLastFriendPullDate(Context context) {
        ACache cache = ACache.get(context.getApplicationContext());
        return cache.getAsString(KEY_LAST_FRIEND_UPDATE_TIME);
    }

    public static void saveLastFriendPullDate(Context context, String lastPullDate) {
        ACache cache = ACache.get(context.getApplicationContext());
        cache.put(KEY_LAST_FRIEND_UPDATE_TIME, StringUtil.getUnnullString(lastPullDate));
    }


    /**
     * 新的好友申请列表
     *
     * @param context
     * @return
     */
    public static String getLastApplyRecordPullDate(Context context) {
        ACache cache = ACache.get(context.getApplicationContext());
        return cache.getAsString(KEY_LAST_FRIEND_APPLY_RECORD_TIME);
    }


    public static void saveLastApplyRecordPullDate(Context context, String lastPullDate) {
        ACache cache = ACache.get(context.getApplicationContext());
        cache.put(KEY_LAST_FRIEND_APPLY_RECORD_TIME, StringUtil.getUnnullString(lastPullDate));
    }

    /**
     * 支付宝回单拉取时间
     *
     * @param context
     * @return
     */
    public static String getLastAliPayListMsgPullTime(Context context) {
        ACache cache = ACache.get(context.getApplicationContext());
        return cache.getAsString(MsgCenterConstants.KEY_LAST_PULL_ALIPAY_LIST_MSG_RECORD_TIME);
    }

    public static void saveLastAliPayListMsgPullTime(Context context, String lastPullDate) {
        ACache cache = ACache.get(context.getApplicationContext());
        cache.put(MsgCenterConstants.KEY_LAST_PULL_ALIPAY_LIST_MSG_RECORD_TIME, StringUtil.getUnnullString(lastPullDate));
    }

    /**
     * 条管家消息列表拉取时间
     *
     * @param context
     * @return
     */
    public static String getLastHMListMsgPullTime(Context context) {
        ACache cache = ACache.get(context.getApplicationContext());
        return cache.getAsString(MsgCenterConstants.KEY_LAST_PULL_HM_LIST_MSG_RECORD_TIME);
    }

    public static void saveLastHMListMsgPullTime(Context context, String lastPullDate) {
        ACache cache = ACache.get(context.getApplicationContext());
        cache.put(MsgCenterConstants.KEY_LAST_PULL_HM_LIST_MSG_RECORD_TIME, StringUtil.getUnnullString(lastPullDate));
    }

    /**
     * 疑似合同
     *
     * @param context
     * @return
     */
    public static String getLastSimilarityContractListMsgPullTime(Context context) {
        ACache cache = ACache.get(context.getApplicationContext());
        return cache.getAsString(MsgCenterConstants.KEY_LAST_PULL_SIMILARITY_CONTRACT_LIST_MSG_RECORD_TIME);
    }

    public static void saveLastSimilarityContractListMsgPullTime(Context context, String lastPullDate) {
        ACache cache = ACache.get(context.getApplicationContext());
        cache.put(MsgCenterConstants.KEY_LAST_PULL_SIMILARITY_CONTRACT_LIST_MSG_RECORD_TIME, StringUtil.getUnnullString(lastPullDate));
    }

    /**
     * 清空消息中心所有缓存
     */
    public static synchronized void clearAllCache(Context context) {
        //合同消息，疑似合同，待还提醒，管家消息
        MsgCenterDbHelper.deleteMsgCenterAllListData();
        //未读消息数量缓存
        ACache cache = ACache.get(context.getApplicationContext());
        cache.remove(MsgCenterConstants.KEY_UN_READ_MSG_NUM);
        cache.remove(MsgCenterConstants.KEY_LAST_FRIEND_APPLY_RECORD_TIME);
        cache.remove(MsgCenterConstants.KEY_LAST_FRIEND_UPDATE_TIME);
        //合同消息缓存
        //删除相关好友数据
        FriendDbUtil.deleteAllFriendData();
    }

}
package com.hm.iou.msg.business.message;

import android.content.Context;

import com.hm.iou.msg.bean.MsgDetailBean;
import com.hm.iou.tools.ACache;

import java.util.ArrayList;
import java.util.List;

/**
 * @author syl
 * @time 2018/5/30 下午6:57
 */
public class DataUtil {

    private static final String MSGCENTER_KEY_LIST = "msgcenter_key_list";

    /**
     * 缓存消息中心消息列表
     *
     * @param context
     * @param list    消息列表
     */
    public static void cacheMsgList(Context context, List<MsgDetailBean> list) {
        clearMsgListCache(context);
        if (list == null || list.isEmpty()) {
            return;
        }
        ACache cache = ACache.get(context.getApplicationContext());
        cache.put(MSGCENTER_KEY_LIST, (ArrayList) list);
    }

    /**
     * 读取消息中心缓存列表数据
     *
     * @param context
     * @return
     */
    public static List<MsgDetailBean> readMsgListFromCache(Context context) {
        ACache cache = ACache.get(context.getApplicationContext());
        List<MsgDetailBean> list = (ArrayList<MsgDetailBean>) cache.getAsObject(MSGCENTER_KEY_LIST);
        return list;
    }

    /**
     * 清除缓存消息中心的缓存列表
     *
     * @param context
     */
    public static void clearMsgListCache(Context context) {
        ACache cache = ACache.get(context.getApplicationContext());
        cache.remove(MSGCENTER_KEY_LIST);
    }

}
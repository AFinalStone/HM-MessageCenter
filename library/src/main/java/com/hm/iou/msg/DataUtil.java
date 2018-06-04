package com.hm.iou.msg;

import android.content.Context;
import android.text.TextUtils;

import com.hm.iou.logger.Logger;
import com.hm.iou.msg.bean.MsgDetailBean;
import com.hm.iou.tools.ACache;
import com.hm.iou.tools.TimeUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author syl
 * @time 2018/5/30 下午6:57
 */
public class DataUtil {

    private static final String MSGCENTER_KEY_LIST = "msgcenter_key_list";

    public static int getNoReadMsgNum(Context context) {
        int num = 0;
        List<MsgDetailBean> list = readMsgListFromCache(context);
        for (MsgDetailBean bean : list) {
            if (!bean.isRead()) {
                num++;
            }
        }
        return num;
    }

    /**
     * 刷新消息中心消息列表缓存中
     *
     * @param context
     * @param list    消息列表
     */
    public static void setMsgListToCache(Context context, List<MsgDetailBean> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        clearMsgListCache(context);
        ACache cache = ACache.get(context.getApplicationContext());
        cache.put(MSGCENTER_KEY_LIST, (ArrayList) list);
    }

    /**
     * 添加list到cache中
     *
     * @param context
     * @param list
     */
    public static void addMsgListToCache(Context context, List<MsgDetailBean> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        List<MsgDetailBean> listCache = readMsgListFromCache(context);
        listCache.addAll(list);
        ACache cache = ACache.get(context.getApplicationContext());
        cache.put(MSGCENTER_KEY_LIST, (ArrayList) list);
    }

    /**
     * 读取消息中心缓存列表数据,超过15天的自动丢弃掉
     *
     * @param context
     * @return
     */
    public static List<MsgDetailBean> readMsgListFromCache(Context context) {
        ACache cache = ACache.get(context.getApplicationContext());
        List<MsgDetailBean> list = (ArrayList<MsgDetailBean>) cache.getAsObject(MSGCENTER_KEY_LIST);
        if (list == null) {
            list = new ArrayList<MsgDetailBean>();
        } else {
            List<MsgDetailBean> listFilter = new ArrayList<>();
            for (MsgDetailBean msg : listFilter) {
                if (!isMsgHaveOutTime(msg)) {
                    listFilter.add(msg);
                }
            }
        }
        listSort(list);
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

    /**
     * 判断数据是否超过15天的数据
     *
     * @return
     */
    private static boolean isMsgHaveOutTime(MsgDetailBean msgDetailBean) {
        if (msgDetailBean == null) {
            return true;
        }
        TimeUtil timeUtil = TimeUtil.getInstance(TimeUtil.SimpleDateFormatEnum.DateFormatForApi);
        String strPushDate = msgDetailBean.getPushDate();
        if (TextUtils.isEmpty(strPushDate)) {
            return true;
        }
        Date pushDate = null;
        try {
            pushDate = timeUtil.getTimeInDate(strPushDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (pushDate == null) {
            return true;
        }
        Date currentDate = timeUtil.getCurrentTimeInDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, -15);
        Date outDateFlag = calendar.getTime();
        Logger.d("过期的Date时间" + timeUtil.getTimeInString(currentDate));
        if (outDateFlag.after(pushDate)) {
            return true;
        } else {
            return false;
        }
    }

    private static void listSort(List<MsgDetailBean> list) {
        final TimeUtil timeUtil = TimeUtil.getInstance(TimeUtil.SimpleDateFormatEnum.DateFormatForApi);
        Collections.sort(list, new Comparator<MsgDetailBean>() {
            @Override
            public int compare(MsgDetailBean msg01, MsgDetailBean msg02) {
                try {
                    long time01 = timeUtil.getTimeInLong(msg01.getPushDate());
                    long time02 = timeUtil.getTimeInLong(msg02.getPushDate());
                    if (time01 > time02) {
                        return -1;
                    } else if (time01 < time02) {
                        return 1;
                    } else {
                        return 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

}
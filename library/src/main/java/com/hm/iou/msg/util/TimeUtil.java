package com.hm.iou.msg.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author syl
 * @time 2019/4/9 5:17 PM
 */

public class TimeUtil {

    private static ThreadLocal<SimpleDateFormat> HF_FORMAT_TL = new ThreadLocal<>();
    private static ThreadLocal<SimpleDateFormat> YMF_FORMAT_TL = new ThreadLocal<>();
    private static ThreadLocal<SimpleDateFormat> YMDF_FORMAT_TL = new ThreadLocal<>();

    public static SimpleDateFormat getHfFormat() {
        SimpleDateFormat sdf = HF_FORMAT_TL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat("HH:mm");
            HF_FORMAT_TL.set(sdf);
        }
        return sdf;
    }

    public static SimpleDateFormat getYmfFormat() {
        SimpleDateFormat sdf = YMF_FORMAT_TL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            YMF_FORMAT_TL.set(sdf);
        }
        return sdf;
    }

    public static SimpleDateFormat getYmdfFormat() {
        SimpleDateFormat sdf = YMDF_FORMAT_TL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            YMDF_FORMAT_TL.set(sdf);
        }
        return sdf;
    }

    /**
     * 格式化会话列表的时间
     *
     * @param time
     * @return
     */
    public static String formatChatListTime(long time) {
        Calendar current = Calendar.getInstance();

        Calendar today = Calendar.getInstance();
        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();
        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        current.setTimeInMillis(time);

        try {
            if (current.after(today)) {
                return getHfFormat().format(current.getTime());
            } else if (current.before(today) && current.after(yesterday)) {
                return "昨天 " + getHfFormat().format(current.getTime());
            } else {
                return getYmfFormat().format(current.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    /**
     * 格式化消息的创建时间
     *
     * @param createTime
     * @return
     */
    public static String formatMsgItemCreateTime(String createTime) {
        try {
            long time = getYmdfFormat().parse(createTime).getTime();
            return formatChatListTime(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createTime;
    }

    /**
     * 格式化待还提醒消息的还款日期
     *
     * @param returnTime
     * @return
     */
    public static String formatRemindBackReturnTime(String returnTime) {
        try {
            Date date = getYmdfFormat().parse(returnTime);
            return new SimpleDateFormat("yyyy年MM月dd日").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnTime;
    }


    /**
     * 格式化疑似合同归还时间
     *
     * @param backTime
     * @return
     */
    public static String formatSimilarityContractBackTime(String backTime) {
        try {
            Date date = getYmdfFormat().parse(backTime);
            //前天
            return new SimpleDateFormat("yyyy.MM.dd").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
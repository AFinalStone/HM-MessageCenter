package com.hm.iou.msg.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author syl
 * @time 2019/4/9 5:17 PM
 */

public class TimeUtil {

    private static SimpleDateFormat HF_FORMAT = new SimpleDateFormat("HH:mm");
    private static SimpleDateFormat YMF_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static SimpleDateFormat YMDF_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 格式化会话列表的时间
     *
     * @param time
     * @return
     */
    public static String formatChatListTime(long time) {
        try {
            long now = System.currentTimeMillis();

            if (now - time < 86400000) {
                Calendar calc = Calendar.getInstance();
                calc.setTimeInMillis(time);
                return "今天" + HF_FORMAT.format(time);
            }

            if (now - time < (86400000 * 2)) {
                //昨天
                return "昨天" + HF_FORMAT.format(time);
            }

            //前天
            return YMF_FORMAT.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 格式化待还提醒消息的创建时间
     *
     * @param createTime
     * @return
     */
    public static String formatRemindBackCreateTime(String createTime) {
        try {
            Date date = YMDF_FORMAT.parse(createTime);
            //前天
            return YMF_FORMAT.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 格式化待还提醒消息的还款日期
     *
     * @param returnTime
     * @return
     */
    public static String formatRemindBackReturnTime(String returnTime) {
        try {
            Date date = YMDF_FORMAT.parse(returnTime);
            //前天
            return new SimpleDateFormat("yyyy年MM月dd日").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 格式化管家消息的时间
     *
     * @param startTime
     * @return
     */
    public static String formatHmMsgStartTime(String startTime) {
        try {
            Date date = YMDF_FORMAT.parse(startTime);
            return YMF_FORMAT.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 格式化合同消息的时间
     *
     * @param createTime
     * @return
     */
    public static String formatContractMsgStartTime(String createTime) {
        try {
            long now = System.currentTimeMillis();
            long time = YMDF_FORMAT.parse(createTime).getTime();

            if (now - time < 86400000) {
                Calendar calc = Calendar.getInstance();
                calc.setTimeInMillis(time);
                return "今天" + HF_FORMAT.format(time);
            }

            if (now - time < (86400000 * 2)) {
                //昨天
                return "昨天" + HF_FORMAT.format(time);
            }

            //前天
            return YMF_FORMAT.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}

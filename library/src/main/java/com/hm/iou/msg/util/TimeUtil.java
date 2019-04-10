package com.hm.iou.msg.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author syl
 * @time 2019/4/9 5:17 PM
 */

public class TimeUtil {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat YMF_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat MF_FORMAT = new SimpleDateFormat("MM-dd");

    public static String formatChatListTime(long time) {
        try {
            String t = DATE_FORMAT.format(time);
            long now = System.currentTimeMillis();

            Calendar nowCalc = Calendar.getInstance();
            nowCalc.setTimeInMillis(now);
//            long ms = 1000 * (nowCalc.get(Calendar.HOUR_OF_DAY) * 3600 + nowCalc.get(Calendar.MINUTE) * 60 + nowCalc.get(Calendar.SECOND));

            if (now - time < 86400000) {
                long interval = now - time;//当前距离文章发布时间的时间间隔

                long min = interval / 60000;
                if (min <= 0) {
                    return "刚刚";
                }
                long hour = min / 60;
                if (hour <= 0) {
                    return String.format("%s分钟前", min);
                }
                return String.format("%s小时%s分钟前", hour, min % 60);
            }

            if (now - time < (86400000 * 2)) {
                //昨天
                return String.format("昨天 %s", t.substring(11, 16));
            }

            if (now - time < (86400000 * 3)) {
                //前天
                return String.format("前天 %s", t.substring(11, 16));
            }

            Calendar commentCalc = Calendar.getInstance();
            commentCalc.setTimeInMillis(time);

            //非本年
            if (commentCalc.get(Calendar.YEAR) != nowCalc.get(Calendar.YEAR)) {
                return YMF_FORMAT.format(commentCalc.getTime());
            } else {
                return MF_FORMAT.format(commentCalc.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}

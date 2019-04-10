package com.hm.iou.msg.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author syl
 * @time 2019/4/9 5:17 PM
 */

public class TimeUtil {

    private static SimpleDateFormat HF_FORMAT = new SimpleDateFormat("HH:mm");
    private static SimpleDateFormat YMF_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatChatListTime(long time) {
        try {
            long now = System.currentTimeMillis();

            if (now - time < 86400000) {
                Calendar calc = Calendar.getInstance();
                calc.setTimeInMillis(time);
                int hour = calc.get(Calendar.HOUR_OF_DAY);
                String strDay = "上午";
                if (hour > 13) {
                    strDay = "下午";
                }
                return strDay + HF_FORMAT.format(now);
            }

            if (now - time < (86400000 * 2)) {
                //昨天
                return "昨天";
            }

            //前天
            return YMF_FORMAT.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}

package com.hm.iou.msg.bean;

import android.text.TextUtils;

import com.hm.iou.msg.business.feedback.view.IFeedbackListItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hjy on 2018/5/29.
 */

public class FeedbackListItemBean implements IFeedbackListItem {

    private String id;
    private String content;
    private String recordTime;
    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    //-----------------------------------

    private String formatTimeStr;

    @Override
    public String getFeedbackId() {
        return id;
    }

    @Override
    public String getTitle() {
        return content;
    }

    @Override
    public String getTime() {
        if (!TextUtils.isEmpty(formatTimeStr)) {
            return formatTimeStr;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(recordTime);
            sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
            formatTimeStr = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatTimeStr;
    }

    @Override
    public String getStatusStr() {
        if (status == 3) {
            return "已处理";
        }
        return "未处理";
    }

    @Override
    public int getStatusColor() {
        if (status == 3) {
            return 0xffa3a3a3;
        }
        return 0xff222222;
    }
}

package com.hm.iou.msg.bean;

import com.hm.iou.msg.business.friend.view.ReportUserActivity;

import lombok.Data;

/**
 * @author syl
 * @time 2019/4/4 5:24 PM
 */
@Data
public class ReportItemBean implements ReportUserActivity.IReasonItem {

    private int feedbackId;
    private String content;

    @Override
    public String getTitle() {
        return content == null ? "" : content;
    }

    @Override
    public int getReasonId() {
        return feedbackId;
    }
}

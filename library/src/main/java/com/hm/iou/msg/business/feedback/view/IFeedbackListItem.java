package com.hm.iou.msg.business.feedback.view;

/**
 * Created by hjy on 2018/5/29.
 */

public interface IFeedbackListItem {

    String getFeedbackId();

    String getTitle();

    String getTime();

    String getStatusStr();

    int getStatusColor();

}
package com.hm.iou.msg.bean;

import lombok.Data;

@Data
public class FriendRelationResBean {

    private int relationCode;
    private boolean overdue;
    private String imAccid;
    private String desc;
    private String applierRemark;

}

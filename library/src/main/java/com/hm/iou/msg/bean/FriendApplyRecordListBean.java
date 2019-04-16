package com.hm.iou.msg.bean;

import com.hm.iou.database.table.FriendApplyRecord;

import java.util.List;

import lombok.Data;

/**
 * Created by hjy on 2019/4/15.
 */
@Data
public class FriendApplyRecordListBean {

    private List<FriendApplyRecord> applyRecordRespList;
    private String lastReqDate;

}

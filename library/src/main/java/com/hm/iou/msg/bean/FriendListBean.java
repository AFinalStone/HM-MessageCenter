package com.hm.iou.msg.bean;

import com.hm.iou.database.table.FriendData;

import java.util.List;

import lombok.Data;

/**
 * Created by hjy on 2019/4/15.
 */

@Data
public class FriendListBean {

    private List<String> delList;
    private List<FriendData> mailListRespList;
    private String lastReqDate;

}

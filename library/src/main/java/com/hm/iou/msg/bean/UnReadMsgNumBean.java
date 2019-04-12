package com.hm.iou.msg.bean;

import java.io.Serializable;

import lombok.Data;

/**
 * @author syl
 * @time 2018/5/30 上午11:51
 */
@Data
public class UnReadMsgNumBean implements Serializable {

    int msgContractUnread;
    int msgNoRepayUnread;
    int msgSimilarContractUnread;
    int msgButlerUnread;
    int newFriendUnread;
}

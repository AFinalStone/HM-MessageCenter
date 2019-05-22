package com.hm.iou.msg.bean;

import java.io.Serializable;

import lombok.Data;

/**
 * @author syl
 * @time 2018/5/30 上午11:51
 */
@Data
public class UnReadMsgNumBean implements Serializable {

    private int butlerMessageNumber;//管家消息
    private int contractNumber;//合同消息
    private int similarContractNumber;//疑似合同
    private int waitRepayNumber;//待还
    private int friendMessageNumber;//新的朋友
    private int alipayReceiptNumber;//˙支付宝回单
}

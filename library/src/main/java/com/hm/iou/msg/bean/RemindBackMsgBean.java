package com.hm.iou.msg.bean;

import com.hm.iou.msg.business.remind.view.IRemindBackMsgItem;

import lombok.Data;

/**
 * Created by syl on 2018/5/30.
 */
@Data
public class RemindBackMsgBean implements IRemindBackMsgItem {

    private String title;
    private String content;
    private String type;
    private String repayDateTime;
    private String repayAmount;
    private String createTime;
    private String jumpUrl;

    @Override
    public String getITitle() {
        return title;
    }

    @Override
    public String getITime() {
        return createTime;
    }

    @Override
    public String getIBackMoneyTime() {
        return "还款日期：" + repayDateTime;
    }

    @Override
    public String getIBackMoney() {
        return String.format("还款金额：%S元", repayAmount);
    }

    @Override
    public String getIContractId() {
        return jumpUrl;
    }

    @Override
    public String getIContractType() {
        return type;
    }
}

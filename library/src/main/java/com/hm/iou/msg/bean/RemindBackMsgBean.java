package com.hm.iou.msg.bean;

import com.hm.iou.msg.business.remindback.view.IRemindBackMsgItem;
import com.hm.iou.sharedata.model.IOUKindEnum;

import lombok.Data;

/**
 * Created by syl on 2018/5/30.
 */
@Data
public class RemindBackMsgBean implements IRemindBackMsgItem {


    /**
     * content : string
     * createTime : 2019-04-15T12:17:23.987Z
     * iouKind : 0
     * jumpUrl : string
     * repayAmount : 0
     * repayDateTime : 2019-04-15T12:17:23.988Z
     * title : string
     */

    private String content;
    private String createTime;
    private int iouKind;
    private String jumpUrl;
    private int repayAmount;
    private String repayDateTime;
    private String title;

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
        //TODO
        return String.valueOf(iouKind);
    }

}

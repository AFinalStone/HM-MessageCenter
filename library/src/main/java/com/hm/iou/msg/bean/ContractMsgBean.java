package com.hm.iou.msg.bean;

import com.hm.iou.msg.business.contractmsg.view.IContractMsgItem;

import lombok.Data;

/**
 * Created by syl on 2018/5/30.
 */
@Data
public class ContractMsgBean implements IContractMsgItem {


    private String title;
    private String time;
    private String content;
    private String contractId;
    private String contractType;

    @Override
    public String getITitle() {
        return title;
    }

    @Override
    public String getITime() {
        return time;
    }

    @Override
    public String getIContent() {
        return content;
    }

    @Override
    public String getIContractId() {
        return contractId;
    }

    @Override
    public String getIContractType() {
        return contractType;
    }
}

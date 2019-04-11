package com.hm.iou.msg.bean;

import com.hm.iou.msg.business.similarity.view.ISimilarityContractMsgItem;
import com.hm.iou.tools.StringUtil;

/**
 * Created by syl on 2019/4/11.
 */

public class SimilarityContractMsgBean implements ISimilarityContractMsgItem {

    private String title;               //标题
    private String amount;               //金额
    private String type;           //合同类型
    private String loanerName;          //出借人
    private String borrowerName;        //借款人
    private String repayDateTime;        //还款日期
    private String returnWay;        //归还方式
    private String jumpUrl;      //归还时间

    @Override
    public String getITitle() {
        return StringUtil.getUnnullString(title);
    }

    @Override
    public String getILenderName() {
        return StringUtil.getUnnullString(loanerName);
    }

    @Override
    public String getIBorrowerName() {
        return StringUtil.getUnnullString(borrowerName);
    }

    @Override
    public String getIBackTime() {
        return "归还时间：" + repayDateTime;
    }

    @Override
    public String getIBackType() {
        return returnWay;
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

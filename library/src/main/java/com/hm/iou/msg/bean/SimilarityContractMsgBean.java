package com.hm.iou.msg.bean;

import com.hm.iou.msg.business.similarity.view.ISimilarityContractMsgItem;
import com.hm.iou.tools.StringUtil;

/**
 * Created by syl on 2019/4/11.
 */

public class SimilarityContractMsgBean implements ISimilarityContractMsgItem {


    /**
     * amount : string
     * borrowerName : string
     * createTime : 2019-04-15T12:17:23.976Z
     * iouKind : 0
     * iouStatus : 0
     * jumpUrl : string
     * loanerName : string
     * returnWayDesc : string
     * title : string
     */

    private String amount;
    private String borrowerName;
    private String createTime;
    private int iouKind;
    private int iouStatus;
    private String jumpUrl;
    private String loanerName;
    private String returnWayDesc;
    private String title;

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
        return "归还时间：" + createTime;
    }

    @Override
    public String getIBackType() {
        return returnWayDesc;
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

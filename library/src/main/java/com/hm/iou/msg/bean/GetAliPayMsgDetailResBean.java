package com.hm.iou.msg.bean;

import com.hm.iou.sharedata.model.IOUKindEnum;

import lombok.Data;

/**
 * @author syl
 * @time 2019/5/21 11:13 AM
 */
@Data
public class GetAliPayMsgDetailResBean {


    /**
     * appliyReceiptStatus : 0
     * createTime : 2019-05-12 12:20:34
     * deleted : true
     * justiceId : string
     * operatorDate : 2019-05-13 08:33:21
     * operatorName : 李隆基
     * operatorShowId : 1234353453
     * pdfUrl : https://www.baidu.com
     * senderMail : 602392342@qq.com
     */

    private int appliyReceiptStatus;// 1、成功 2、没有发现附件 3、附件不合规 4、非关联回单 ,
    private String name;
    private int iouKind;//合同类型
    private String createTime;
    private boolean deleted;
    private String justiceId;
    private String exEvidenceId;
    private String operatorDate;
    private String operatorName;
    private String operatorShowId;
    private String pdfUrl;
    private String senderMail;

}

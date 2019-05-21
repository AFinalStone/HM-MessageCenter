package com.hm.iou.msg.bean;

import lombok.Data;

/**
 * @author syl
 * @time 2019/5/21 11:13 AM
 */
@Data
public class GetAliPayMsgDetailResBean {

    /**
     * status : 1
     * contractId : 5756745645
     * senderEmail : 602392342@qq.com
     * pdfUrl : https://www.baidu.com
     * creatorName : 2019-05-12 12:20:34
     * deleteStats : 1
     * operatorShowId : 1234353453
     * operatorName : 李隆基
     * operatorDeleteTime : 2019-05-13 08:33:21
     */

    private int status;
    private String contractId;
    private String senderEmail;
    private String pdfUrl;
    private String creatorName;
    private String createTime;
    private int deleteStats;
    private String operatorShowId;
    private String operatorName;
    private String operatorDeleteTime;

}

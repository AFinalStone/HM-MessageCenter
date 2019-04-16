package com.hm.iou.msg.bean;

import lombok.Data;

/**
 * Created by syl on 2018/5/30.
 */
@Data
public class ContractMsgBean {


    /**
     * content : string
     * createTime : 2019-04-15T12:17:23.967Z
     * jumpUrl : string
     * sourceBizType : 0
     * title : string
     */
    private String createTime;
    private String title;
    private String content;
    private String jumpUrl;
    private int sourceBizType;

}

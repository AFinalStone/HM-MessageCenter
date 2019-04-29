package com.hm.iou.msg.bean;

import lombok.Data;

/**
 * @author syl
 * @time 2019/4/4 5:24 PM
 */
@Data
public class GetOrRefreshIMTokenBean {


    /**
     * imAccId : string
     * imToken : string
     */

    private String imAccId;
    private String imToken;
}


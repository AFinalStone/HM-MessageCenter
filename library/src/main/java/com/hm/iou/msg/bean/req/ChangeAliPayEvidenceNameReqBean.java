package com.hm.iou.msg.bean.req;

import lombok.Data;

/**
 * @author syl
 * @time 2019/5/21 10:47 AM
 */
@Data
public class ChangeAliPayEvidenceNameReqBean {


    /**
     * exEvidenceId : 0
     * name : string
     */

    private String exEvidenceId;
    private String name;
}

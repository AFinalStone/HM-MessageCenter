package com.hm.iou.msg.bean.req;

import lombok.Data;

/**
 * @author syl
 * @time 2019/5/21 10:47 AM
 */
@Data
public class GetAliPayDetailShareUrlReqBean {


    /**
     * id : string
     * shareType : 0
     */

    private String id;//分享那个东西的Id
    private int shareType;//分享类型：0=扩展合同、1=借条,2=凭证


}

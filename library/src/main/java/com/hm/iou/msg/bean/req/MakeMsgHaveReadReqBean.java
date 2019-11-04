package com.hm.iou.msg.bean.req;

/**
 * @author syl
 * @time 2019/5/21 10:47 AM
 */
public class MakeMsgHaveReadReqBean {

    /**
     * msgId : 0
     * type : 0
     */

    private String msgId;
    private String type;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

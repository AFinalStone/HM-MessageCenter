package com.hm.iou.msg.bean;

/**
 * Created by hjy on 2018/5/30.
 */

public class AddFeedbackReqBean {

    private String content;
    private String customerId;
    private int kind;
    private String pics;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }
}

package com.hm.iou.msg.dict;

/**
 * Created by hjy on 2018/5/29.
 */

public enum MsgType {

    Sport(1, "活动"),
    Advertisement(2, "广告"),
    TopNews(3, "头条"),
    CommuniqueIntro(4, "官方公告");

    private int value;
    private String desc;

    MsgType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getDescByValue(int value) {
        MsgType[] arr = MsgType.values();
        for (MsgType kind : arr) {
            if (kind.value == value) {
                return kind.desc;
            }
        }
        return null;
    }

}

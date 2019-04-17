package com.hm.iou.msg.dict;

/**
 * @author syl
 * @time 2019/4/17 11:45 AM
 */
public enum MsgType {

    Sport(40, "活动"),
    Advertisement(10, "广告"),
    News(20, "头条"),
    FeedBack(30, "您有新的意见反馈结果"),
    CommuniqueIntro(100, "官方公告");

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

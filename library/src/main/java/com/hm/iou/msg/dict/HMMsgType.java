package com.hm.iou.msg.dict;

/**
 * @author syl
 * @time 2019/4/17 11:45 AM
 */
public enum HMMsgType {

    Sport(10, "活动"),
    News(20, "头条"),
    FeedbackByCustomer(30, "意见反馈【客户】"),
    FeedbackByInnerStaff(31, "意见反馈【客服】"),
    Advertisement(40, "广告"),
    CommuniqueIntro(100, "官方公告");

    private int value;
    private String desc;

    HMMsgType(int value, String desc) {
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
        HMMsgType[] arr = HMMsgType.values();
        for (HMMsgType kind : arr) {
            if (kind.value == value) {
                return kind.desc;
            }
        }
        return null;
    }

}

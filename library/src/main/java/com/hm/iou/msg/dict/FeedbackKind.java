package com.hm.iou.msg.dict;

/**
 * Created by hjy on 2018/5/29.
 */

public enum FeedbackKind {

    FlashWrong(1002, "闪退、卡顿或界面错位"),
    DataWrong(1003, "账户数据未显示"),
    NoCode(1001, "收不到短信验证码"),
    IouFail(1004, "生成借条失败"),
    Else(1005, "其他问题");

    private int value;
    private String desc;

    FeedbackKind(int value, String desc) {
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
        FeedbackKind[] arr = FeedbackKind.values();
        for (FeedbackKind kind : arr) {
            if (kind.value == value) {
                return kind.desc;
            }
        }
        return null;
    }

}

package com.hm.iou.msg.dict;

/**
 * Created by hjy on 2018/5/29.
 */

public enum ApplyNewFriendStatus {

    NEED_TO_AGREE(0, "待同意"),
    HAVE_AGREE(1, "已同意"),
    HAVE_OVER(3, "已过期");

    private int value;
    private String desc;

    ApplyNewFriendStatus(int value, String desc) {
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
        ApplyNewFriendStatus[] arr = ApplyNewFriendStatus.values();
        for (ApplyNewFriendStatus kind : arr) {
            if (kind.value == value) {
                return kind.desc;
            }
        }
        return null;
    }

}

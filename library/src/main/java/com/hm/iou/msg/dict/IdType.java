package com.hm.iou.msg.dict;

public enum IdType {

    COMM(1, "嘿马用户userId"),
    IM_ACC_ID(2, "云信账号id");

    public int type;
    public String desc;

    IdType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

}

package com.hm.iou.msg.dict;

/**
 * Created by hjy on 2018/5/29.
 */

enum class CheckIMFriendStatus(val type: Int, val desc: String) {
    NORMAL(0, "成功可发消息"),
    BLACK_NAME(1, "对方已拉黑自己"),
    DELETE(2, "对方已删除自己"),
    LOGOUT(3, "对方账户已注销"),
    SYS_BLACK_NAME(4, "对方账户是系统黑名单");

}

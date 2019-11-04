package com.hm.iou.msg.bean.req;

/**
 * Created by hjy on 2019/4/19.
 */
public class FriendDetailReqBean {

    private String friendId;
    private int idType;

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }
}
package com.hm.iou.msg.bean.req;

/**
 * Created by hjy on 2019/4/12.
 */
public class AddFriendReqBean {

    private String friendId;
    private String applyMsg;
    private int idType;//1用户id，2im_id

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getApplyMsg() {
        return applyMsg;
    }

    public void setApplyMsg(String applyMsg) {
        this.applyMsg = applyMsg;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }
}

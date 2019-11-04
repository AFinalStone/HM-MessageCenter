package com.hm.iou.msg.bean.req;

/**
 * Created by hjy on 2019/4/12.
 */
public class UpdateRemarkNameReqBean {

    private String friendId;
    private String stageName;

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }
}

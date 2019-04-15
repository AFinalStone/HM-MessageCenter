package com.hm.iou.msg.bean;

import com.hm.iou.msg.business.apply.view.IApplyNewFriend;

import lombok.Data;

/**
 * Created by syl on 2018/5/30.
 * 新的好友申请
 */
@Data
public class ApplyApplyNewFriendBean implements IApplyNewFriend {

    private String applyId;
    private String avatarUrl;
    private String nickName;
    private String applyMsg;
    private String friendId;
    private int status;                 //状态 0 待同意 1已同意 3已过期

    @Override
    public String getIHeaderImg() {
        return avatarUrl;
    }

    @Override
    public String getINick() {
        return nickName;
    }

    @Override
    public String getIContent() {
        return applyMsg;
    }

    @Override
    public int getIStatus() {
        return status;
    }

    @Override
    public String getFriendId() {
        return friendId;
    }

    @Override
    public String getApplyId() {
        return applyId;
    }

}

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
    private String applyMemo;
    private int status;

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
        return applyMemo;
    }

    @Override
    public int getIStatus() {
        return status;
    }

    @Override
    public String getIApplyId() {
        return applyId;
    }
}

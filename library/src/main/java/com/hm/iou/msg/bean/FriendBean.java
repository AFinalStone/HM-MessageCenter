package com.hm.iou.msg.bean;

import com.hm.iou.msg.business.friendlist.view.IFriend;

import lombok.Data;

/**
 * Created by syl on 2018/5/30.
 * 通讯录
 */
@Data
public class FriendBean implements IFriend {

    private String avatarUrl;   //头像
    private String nickName;    //昵称
    private String friendId;    //好友id
    private String stageName;   //备注名

    @Override
    public String getIHeaderImg() {
        return avatarUrl;
    }

    @Override
    public String getINick() {
        return nickName;
    }

    @Override
    public String getIAccount() {
        return friendId;
    }
}

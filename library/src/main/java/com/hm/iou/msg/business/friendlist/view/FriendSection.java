package com.hm.iou.msg.business.friendlist.view;

import com.chad.library.adapter.base.entity.SectionEntity;

import java.util.List;

public class FriendSection extends SectionEntity<IFriend> {

    private List<IFriend> friendList;

    public FriendSection(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public FriendSection(IFriend iFriend) {
        super(iFriend);
    }

    public List<IFriend> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<IFriend> friendList) {
        this.friendList = friendList;
    }

    public int getChildCount() {
        return friendList != null ? friendList.size() : 0;
    }

    public int getTotalCount() {
        return getChildCount() + 1;
    }

}

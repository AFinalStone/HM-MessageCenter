package com.hm.iou.msg.event;

/**
 * Created by hjy on 2019/4/16.
 */

public class DeleteFriendEvent {

    public String friendId;

    public DeleteFriendEvent(String friendId) {
        this.friendId = friendId;
    }
}

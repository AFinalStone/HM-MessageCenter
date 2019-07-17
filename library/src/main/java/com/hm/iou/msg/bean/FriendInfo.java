package com.hm.iou.msg.bean;

import lombok.Data;

/**
 * Created by hjy on 2019/4/11.
 */
@Data
public class FriendInfo {

    private String avatarUrl;           //头像uri
    private boolean blackStatus;        //是否是我的黑名单用户 0=不是黑名单 1=黑名单
    private String friendId;            //用户id
    private boolean friended;           //是否是好友 0=不是好友 1=好友
    private String location;            //常驻城市
    private String nickName;            //昵称
    private boolean own;                //是否是自己 0=不是自己 1=自己
    private String showId;
    private int customerType;           //用户类型 0 普通用户 1 实名用户
    private String stageName;           //备注
    private boolean sysBlackStatus;     //判断是否是对方黑名单或平台黑名单 0=不是黑名单 1=黑名单
    private String friendImAccId;       //好友im账户
    private int sex;                    //0=女，1=男，3=未知

}
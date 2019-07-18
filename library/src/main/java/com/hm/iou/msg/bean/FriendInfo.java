package com.hm.iou.msg.bean;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

/**
 * Created by hjy on 2019/4/11.
 */
@Data
public class FriendInfo implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatarUrl);
        dest.writeByte(this.blackStatus ? (byte) 1 : (byte) 0);
        dest.writeString(this.friendId);
        dest.writeByte(this.friended ? (byte) 1 : (byte) 0);
        dest.writeString(this.location);
        dest.writeString(this.nickName);
        dest.writeByte(this.own ? (byte) 1 : (byte) 0);
        dest.writeString(this.showId);
        dest.writeInt(this.customerType);
        dest.writeString(this.stageName);
        dest.writeByte(this.sysBlackStatus ? (byte) 1 : (byte) 0);
        dest.writeString(this.friendImAccId);
        dest.writeInt(this.sex);
    }

    protected FriendInfo(Parcel in) {
        this.avatarUrl = in.readString();
        this.blackStatus = in.readByte() != 0;
        this.friendId = in.readString();
        this.friended = in.readByte() != 0;
        this.location = in.readString();
        this.nickName = in.readString();
        this.own = in.readByte() != 0;
        this.showId = in.readString();
        this.customerType = in.readInt();
        this.stageName = in.readString();
        this.sysBlackStatus = in.readByte() != 0;
        this.friendImAccId = in.readString();
        this.sex = in.readInt();
    }

    public static final Parcelable.Creator<FriendInfo> CREATOR = new Parcelable.Creator<FriendInfo>() {
        @Override
        public FriendInfo createFromParcel(Parcel source) {
            return new FriendInfo(source);
        }

        @Override
        public FriendInfo[] newArray(int size) {
            return new FriendInfo[size];
        }
    };
}
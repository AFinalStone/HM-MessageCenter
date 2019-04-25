package com.hm.iou.msg.bean;

import android.text.TextUtils;

import com.hm.iou.sharedata.model.SexEnum;

import lombok.Data;

/**
 * @author syl
 * @time 2018/5/30 上午11:51
 */
@Data
public class ChatMsgBean {

    String contactId;
    String contractHeaderImage;
    String contractShowName;
    String chatContent;
    String time;
    int redMsgNum;
    int sex;//0未知，1男性，2女性
    int status;// 0正在发送，1发送成功，2发送失败

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChatMsgBean) {
            return !TextUtils.isEmpty(contactId) && contactId.equals(((ChatMsgBean) obj).getContactId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (contactId != null ? contactId.hashCode() : 0);
        return result;
    }
}

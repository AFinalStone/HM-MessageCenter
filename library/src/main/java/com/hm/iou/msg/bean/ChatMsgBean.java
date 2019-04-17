package com.hm.iou.msg.bean;

import android.text.TextUtils;

import lombok.Data;

/**
 * @author syl
 * @time 2018/5/30 上午11:51
 */
@Data
public class ChatMsgBean {

    String fromAccount;
    String fromHeaderImage;
    String fromNick;
    String contactId;
    String chatContent;
    String time;
    int redMsgNum;

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

package com.hm.iou.msg.business.message.view;

import android.text.TextUtils;

import lombok.Data;

/**
 * @author syl
 * @time 2018/5/30 上午11:51
 */
@Data
public class ChatMsgModel {

    String fromAccount;
    String fromHeaderImage;
    String fromNick;
    String contactId;
    String chatContent;
    String time;
    int redMsgNum;


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChatMsgModel) {
            return !TextUtils.isEmpty(fromAccount) && fromAccount.equals(((ChatMsgModel) obj).getFromAccount());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (fromAccount != null ? fromAccount.hashCode() : 0);
        return result;
    }
}

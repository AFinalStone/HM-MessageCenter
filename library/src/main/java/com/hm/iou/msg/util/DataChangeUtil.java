package com.hm.iou.msg.util;

import com.hm.iou.logger.Logger;
import com.hm.iou.msg.business.message.view.ChatMsgModel;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by syl on 2019/4/9.
 */

public class DataChangeUtil {

    public static List<ChatMsgModel> changeRecentContactToIChatMsgItem(List<RecentContact> list) {
        List<ChatMsgModel> resultList = new ArrayList<>();

        if (list != null) {
            for (RecentContact contact : list) {
                String fromNick = contact.getFromNick();
                String fromAccount = contact.getFromAccount();
                String contactId = contact.getContactId();
                String content = contact.getContent();
                String strTime = TimeUtil.formatChatListTime(contact.getTime());
                int redMsgNum = contact.getUnreadCount();
                NimUserInfo users = NIMClient.getService(UserService.class).getUserInfo(fromAccount);
                ChatMsgModel chatMsgModel = new ChatMsgModel();
                Logger.d("fromName=" + fromNick + "contactId="
                        + contactId + "content=" + content + "strTime" + strTime + "redMsgNum" + redMsgNum);
                chatMsgModel.setFromNick(fromNick);
                chatMsgModel.setFromHeaderImage(users.getAvatar());
                chatMsgModel.setFromAccount(fromAccount);
                chatMsgModel.setContactId(contactId);
                chatMsgModel.setChatContent(content);
                chatMsgModel.setRedMsgNum(redMsgNum);
                chatMsgModel.setTime(strTime);
                resultList.add(chatMsgModel);
            }
        }

        return resultList;
    }

}

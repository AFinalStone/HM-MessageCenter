package com.hm.iou.msg.util;

import com.hm.iou.logger.Logger;
import com.hm.iou.msg.bean.ChatMsgBean;
import com.hm.iou.msg.bean.ContractMsgBean;
import com.hm.iou.msg.business.contractmsg.view.IContractMsgItem;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by syl on 2019/4/9.
 */

public class DataChangeUtil {

    /**
     * @param list
     * @return
     */
    public static List<ChatMsgBean> changeRecentContactToIChatMsgItem(List<RecentContact> list) {
        List<ChatMsgBean> resultList = new ArrayList<>();

        if (list != null) {
            for (RecentContact contact : list) {
                String fromNick = contact.getFromNick();
                String fromAccount = contact.getFromAccount();
                String contactId = contact.getContactId();
                String content = contact.getContent();
                String strTime = TimeUtil.formatChatListTime(contact.getTime());
                int redMsgNum = contact.getUnreadCount();
                NimUserInfo users = NIMClient.getService(UserService.class).getUserInfo(fromAccount);
                ChatMsgBean chatMsgBean = new ChatMsgBean();
                Logger.d("fromName=" + fromNick + "contactId="
                        + contactId + "content=" + content + "strTime" + strTime + "redMsgNum" + redMsgNum);
                chatMsgBean.setFromNick(fromNick);
                chatMsgBean.setFromHeaderImage(users.getAvatar());
                chatMsgBean.setFromAccount(fromAccount);
                chatMsgBean.setContactId(contactId);
                chatMsgBean.setChatContent(content);
                chatMsgBean.setRedMsgNum(redMsgNum);
                chatMsgBean.setTime(strTime);
                resultList.add(chatMsgBean);
            }
        }

        return resultList;
    }

    /**
     * @param list
     * @return
     */
    public static List<IContractMsgItem> changeContractMsgBeanToIContractMsgItem(List<ContractMsgBean> list) {
        List<IContractMsgItem> resultList = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                final ContractMsgBean msgBean = list.get(i);
                final String time = msgBean.getCreateTime();
                final String title = msgBean.getTitle();
                final String content = msgBean.getContent();
                final String justUrl = msgBean.getJumpUrl();
                final int contractType = msgBean.getSourceBizType();
                IContractMsgItem iMsgItem = new IContractMsgItem() {

                    private boolean mIfShowTime = true;

                    @Override
                    public String getITime() {
                        return time;
                    }

                    @Override
                    public boolean ifIShowTime() {
                        return mIfShowTime;
                    }

                    @Override
                    public boolean setIfIShowTime(boolean isShowTime) {
                        return mIfShowTime = isShowTime;
                    }

                    @Override
                    public String getITitle() {
                        return title;
                    }

                    @Override
                    public String getIContent() {
                        return content;
                    }

                    @Override
                    public String getIJustUrl() {
                        return justUrl;
                    }

                    @Override
                    public String getIContractType() {
                        return null;
                    }
                };
                try {
                    if (i > 0) {
                        ContractMsgBean previewMsgItem = list.get(i - 1);
                        String strPreviewTime = previewMsgItem.getCreateTime();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        long previewTime = df.parse(strPreviewTime).getTime();
                        long currentTime = df.parse(time).getTime();
                        if (currentTime - previewTime > 300000) {
                            iMsgItem.setIfIShowTime(true);
                        } else {
                            iMsgItem.setIfIShowTime(false);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                resultList.add(iMsgItem);
            }
        }
        return resultList;
    }
}

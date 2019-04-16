package com.hm.iou.msg.util;

import com.hm.iou.database.table.msg.ContractMsgDbData;
import com.hm.iou.database.table.msg.RemindBackMsgDbData;
import com.hm.iou.database.table.msg.SimilarityContractMsgDbData;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.bean.ChatMsgBean;
import com.hm.iou.msg.business.contractmsg.view.IContractMsgItem;
import com.hm.iou.msg.business.remindback.view.IRemindBackMsgItem;
import com.hm.iou.msg.business.similarity.view.ISimilarityContractMsgItem;
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
     * 会话列表
     *
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
     * 合同消息
     *
     * @param list
     * @return
     */
    public static List<IContractMsgItem> changeContractMsgDbDataToIContractMsgItem(List<ContractMsgDbData> list) {
        List<IContractMsgItem> resultList = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                final ContractMsgDbData msgBean = list.get(i);
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
                    public int getIContractType() {
                        return contractType;
                    }
                };
                try {
                    if (i > 0) {
                        ContractMsgDbData previewMsgItem = list.get(i - 1);
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

    /**
     * 提醒归还消息
     *
     * @param list
     * @return
     */
    public static List<IRemindBackMsgItem> changeRemindBackMsgDbDataToIRemindBackMsgItem(List<RemindBackMsgDbData> list) {
        List<IRemindBackMsgItem> resultList = new ArrayList<>();
        if (list != null) {
            for (final RemindBackMsgDbData dbData : list) {
                IRemindBackMsgItem msgItem = new IRemindBackMsgItem() {
                    @Override
                    public String getITitle() {
                        return dbData.getTitle();
                    }

                    @Override
                    public String getITime() {
                        return "还款日期：" + dbData.getCreateTime();
                    }

                    @Override
                    public String getIBackMoneyTime() {
                        return dbData.getRepayDateTime();
                    }

                    @Override
                    public String getIBackMoney() {
                        return "还款日期：" + dbData.getRepayAmount() + "元";
                    }

                    @Override
                    public int getIContractType() {
                        return dbData.getIouKind();
                    }

                    @Override
                    public String getIJustUrl() {
                        return dbData.getJumpUrl();
                    }
                };
                resultList.add(msgItem);
            }
        }

        return resultList;
    }

    /**
     * 提醒疑似合同
     *
     * @param list
     * @return
     */
    public static List<ISimilarityContractMsgItem> changeSimilarityContractMsgDbDataToISimilarityContractMsgItem(List<SimilarityContractMsgDbData> list) {
        List<ISimilarityContractMsgItem> resultList = new ArrayList<>();
        if (list != null) {
            for (final SimilarityContractMsgDbData dbData : list) {
                ISimilarityContractMsgItem msgItem = new ISimilarityContractMsgItem() {

                    @Override
                    public String getITitle() {
                        return dbData.getTitle();
                    }

                    @Override
                    public String getILenderName() {
                        return dbData.getLoanerName();
                    }

                    @Override
                    public String getIBorrowerName() {
                        return dbData.getBorrowerName();
                    }

                    @Override
                    public String getIBackTime() {
                        return "归还时间：" + dbData.getCreateTime();
                    }

                    @Override
                    public String getIBackType() {
                        return "归还方式：" + dbData.getReturnWayDesc();
                    }

                    @Override
                    public String getIJustUrl() {
                        return dbData.getJumpUrl();
                    }

                    @Override
                    public int getIContractType() {
                        return dbData.getIouKind();
                    }
                };
                resultList.add(msgItem);
            }
        }

        return resultList;
    }

}

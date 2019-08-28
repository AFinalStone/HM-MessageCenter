package com.hm.iou.msg.util;

import android.text.TextUtils;

import com.hm.iou.database.table.msg.AliPayMsgDbData;
import com.hm.iou.database.table.msg.ContractMsgDbData;
import com.hm.iou.database.table.msg.HmMsgDbData;
import com.hm.iou.database.table.msg.RemindBackMsgDbData;
import com.hm.iou.database.table.msg.SimilarityContractMsgDbData;
import com.hm.iou.msg.R;
import com.hm.iou.msg.bean.ChatMsgBean;
import com.hm.iou.msg.business.alipay.list.view.IAliPayMsgItem;
import com.hm.iou.msg.business.contractmsg.view.IContractMsgItem;
import com.hm.iou.msg.business.hmmsg.view.IHmMsgItem;
import com.hm.iou.msg.business.remindback.view.IRemindBackMsgItem;
import com.hm.iou.msg.business.similarity.view.ISimilarityContractMsgItem;
import com.hm.iou.msg.dict.HMMsgType;
import com.hm.iou.sharedata.model.IOUKindEnum;
import com.hm.iou.tools.StringUtil;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
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
                String contactId = contact.getContactId();
                String content = contact.getContent();
                String strTime = TimeUtil.formatChatListTime(contact.getTime());
                int redMsgNum = contact.getUnreadCount();
                String displayName = UserInfoHelper.getUserDisplayName(contactId);
                NimUserInfo nimUserInfo = NIMClient.getService(UserService.class).getUserInfo(contactId);
                String headerImgUrl = "";
                GenderEnum genderEnum = GenderEnum.UNKNOWN;
                if (nimUserInfo != null) {
                    headerImgUrl = nimUserInfo.getAvatar();
                    genderEnum = nimUserInfo.getGenderEnum();
                }
                int msgStatus = 1;
                MsgStatusEnum msgStatusEnum = contact.getMsgStatus();
                if (msgStatusEnum != null) {
                    msgStatus = msgStatusEnum.getValue();
                }
                ChatMsgBean chatMsgBean = new ChatMsgBean();
                chatMsgBean.setContactId(contactId);
                if (genderEnum == null) {
                    chatMsgBean.setSex(0);
                } else {
                    chatMsgBean.setSex(genderEnum.getValue());
                }
                chatMsgBean.setContractHeaderImage(headerImgUrl);
                chatMsgBean.setContractShowName(displayName);
                chatMsgBean.setChatContent(content);
                chatMsgBean.setRedMsgNum(redMsgNum);
                chatMsgBean.setTime(strTime);
                chatMsgBean.setStatus(msgStatus);
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
                final ContractMsgDbData dbData = list.get(i);
                final String time = TimeUtil.formatMsgItemCreateTime(dbData.getCreateTime());
                final String title = dbData.getTitle();
                final String content = dbData.getContent();
                final String justUrl = dbData.getJumpUrl();
                final boolean isHaveRead = dbData.isHaveRead();
                final String msgId = dbData.getMsgId();
                final String msgType = dbData.getType();
                final int contractType = dbData.getSourceBizType();
                IContractMsgItem iMsgItem = new IContractMsgItem() {

                    private boolean mIfShowTime = true;
                    private boolean mIsHaveRead = false;

                    @Override
                    public String getITime() {
                        return time;
                    }

                    @Override
                    public boolean ifIShowTime() {
                        return mIfShowTime;
                    }

                    @Override
                    public void setIfIShowTime(boolean isShowTime) {
                        mIfShowTime = isShowTime;
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

                    @Override
                    public String getIMsgId() {
                        return msgId;
                    }

                    @Override
                    public String getIMsgType() {
                        return msgType;
                    }

                    @Override
                    public boolean isHaveRead() {
                        return mIsHaveRead;
                    }

                    @Override
                    public void setHaveRead(boolean isHaveRead) {
                        mIsHaveRead = isHaveRead;
                    }
                };
                try {
                    if (i > 0) {
                        ContractMsgDbData previewDbData = list.get(i - 1);
                        String strPreviewTime = previewDbData.getCreateTime();
                        String strCurrentTime = dbData.getCreateTime();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        long previewTime = df.parse(strPreviewTime).getTime();
                        long currentTime = df.parse(strCurrentTime).getTime();
                        if (currentTime - previewTime > 300000) {
                            iMsgItem.setIfIShowTime(true);
                        } else {
                            iMsgItem.setIfIShowTime(false);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                iMsgItem.setHaveRead(isHaveRead);
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
            for (int i = 0; i < list.size(); i++) {
                final RemindBackMsgDbData dbData = list.get(i);
                final String time = TimeUtil.formatMsgItemCreateTime(dbData.getCreateTime());
                IRemindBackMsgItem iMsgItem = new IRemindBackMsgItem() {

                    private boolean mIsHaveRead = false;
                    private boolean mIfShowTime = true;

                    @Override
                    public String getITitle() {
                        return dbData.getTitle();
                    }

                    @Override
                    public String getITime() {
                        return time;
                    }

                    @Override
                    public boolean ifIShowTime() {
                        return mIfShowTime;
                    }

                    @Override
                    public void setIfIShowTime(boolean isShowTime) {
                        mIfShowTime = isShowTime;
                    }

                    @Override
                    public String getIBackTime() {
                        String backTime = "还款时间：";
                        if (IOUKindEnum.FzContract.getValue() == dbData.getIouKind()) {
                            backTime = "付款时间：";
                        }
                        if (0 == dbData.getThingsType()) {
                            backTime = "归还时间：";
                        }
                        return backTime + TimeUtil.formatRemindBackReturnTime(dbData.getRepayDateTime());
                    }

                    @Override
                    public String getIBackThingName() {
                        String backThingName = "还款金额：";
                        if (IOUKindEnum.FzContract.getValue() == dbData.getIouKind()) {
                            backThingName = "付款金额：";
                        }
                        if (0 == dbData.getThingsType()) {
                            backThingName = "待还物品：";
                        }
                        return backThingName + dbData.getRepayThing();
                    }

                    @Override
                    public int getIContractType() {
                        return dbData.getIouKind();
                    }

                    @Override
                    public String getIJustUrl() {
                        return dbData.getJumpUrl();
                    }

                    @Override
                    public String getIMsgId() {
                        return dbData.getMsgId();
                    }

                    @Override
                    public String getIMsgType() {
                        return dbData.getType();
                    }

                    @Override
                    public boolean isHaveRead() {
                        return mIsHaveRead;
                    }

                    @Override
                    public void setHaveRead(boolean isHaveRead) {
                        mIsHaveRead = isHaveRead;
                    }
                };
                try {
                    if (i > 0) {
                        RemindBackMsgDbData previewDbData = list.get(i - 1);
                        String strPreviewTime = previewDbData.getCreateTime();
                        String strCurrentTime = dbData.getCreateTime();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        long previewTime = df.parse(strPreviewTime).getTime();
                        long currentTime = df.parse(strCurrentTime).getTime();
                        if (currentTime - previewTime > 300000) {
                            iMsgItem.setIfIShowTime(true);
                        } else {
                            iMsgItem.setIfIShowTime(false);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                iMsgItem.setHaveRead(dbData.isHaveRead());
                resultList.add(iMsgItem);
            }
        }

        return resultList;
    }


    /**
     * 疑似合同
     *
     * @param list
     * @return
     */
    public static List<ISimilarityContractMsgItem> changeSimilarityContractMsgDbDataToISimilarityContractMsgItem(List<SimilarityContractMsgDbData> list) {
        List<ISimilarityContractMsgItem> resultList = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                final SimilarityContractMsgDbData dbData = list.get(i);
                final String time = TimeUtil.formatMsgItemCreateTime(dbData.getCreateTime());
                ISimilarityContractMsgItem iMsgItem = new ISimilarityContractMsgItem() {

                    private boolean mIfShowTime = true;
                    private boolean mIsHaveRead = false;

                    @Override
                    public String getITitle() {
                        return dbData.getTitle();
                    }

                    @Override
                    public int getILogoResId() {
                        if (dbData.getIouKind() == IOUKindEnum.Qiantiao.getValue()) {
                            return R.mipmap.jietiao_ic_cover_elec_qiantiao_money;
                        }
                        return R.mipmap.jietiao_ic_cover_elec_borrow_money;
                    }

                    @Override
                    public String getILender() {
                        if (dbData.getIouKind() == IOUKindEnum.Qiantiao.getValue()) {
                            return "债权人";
                        }
                        return "出借方";
                    }

                    @Override
                    public String getILenderName() {
                        return dbData.getLoanerName();
                    }

                    @Override
                    public String getIBorrower() {
                        if (dbData.getIouKind() == IOUKindEnum.Qiantiao.getValue()) {
                            return "债务人";
                        }
                        return "借到方";
                    }

                    @Override
                    public String getIBorrowerName() {
                        return dbData.getBorrowerName();
                    }

                    @Override
                    public String getITime() {
                        return time;
                    }

                    @Override
                    public String getIBackTime() {
                        return "归还时间：" + TimeUtil.formatSimilarityContractBackTime(dbData.getReturnDate());
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
                    public String getIMsgId() {
                        return dbData.getMsgId();
                    }

                    @Override
                    public String getIMsgType() {
                        return dbData.getType();
                    }

                    @Override
                    public boolean isHaveRead() {
                        return mIsHaveRead;
                    }

                    @Override
                    public void setHaveRead(boolean isHaveRead) {
                        mIsHaveRead = isHaveRead;
                    }

                    @Override
                    public boolean ifIShowTime() {
                        return mIfShowTime;
                    }

                    @Override
                    public void setIfIShowTime(boolean isShowTime) {
                        mIfShowTime = isShowTime;
                    }

                };
                try {
                    if (i > 0) {
                        SimilarityContractMsgDbData previewDbData = list.get(i - 1);
                        String strPreviewTime = previewDbData.getCreateTime();
                        String strCurrentTime = dbData.getCreateTime();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        long previewTime = df.parse(strPreviewTime).getTime();
                        long currentTime = df.parse(strCurrentTime).getTime();
                        if (currentTime - previewTime > 300000) {
                            iMsgItem.setIfIShowTime(true);
                        } else {
                            iMsgItem.setIfIShowTime(false);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                iMsgItem.setHaveRead(dbData.isHaveRead());
                resultList.add(iMsgItem);
            }
        }

        return resultList;
    }

    /**
     * 管家消息
     *
     * @param list
     * @return
     */
    public static List<IHmMsgItem> changeHmMsgDbDataToIHmMsgItem(List<HmMsgDbData> list) {
        List<IHmMsgItem> resultList = new ArrayList<>();
        if (list != null) {
            for (final HmMsgDbData dbData : list) {
                IHmMsgItem msgItem = new IHmMsgItem() {

                    private boolean mIsHaveRead = false;

                    @Override
                    public int getMsgIcon() {
                        if (HMMsgType.Sport.getValue() == dbData.getSourceBizType()) {
                            return R.mipmap.msgcenter_ic_activity;
                        }
                        if (HMMsgType.Advertisement.getValue() == dbData.getSourceBizType()) {
                            return R.mipmap.msgcenter_ic_ad;
                        }
                        if (HMMsgType.News.getValue() == dbData.getSourceBizType()) {
                            return R.mipmap.msgcenter_ic_toutiao;
                        }
                        return R.mipmap.msgcenter_ic_official_notice;
                    }

                    @Override
                    public String getMsgTitle() {
                        if (TextUtils.isEmpty(dbData.getTitle())) {
                            return HMMsgType.getDescByValue(dbData.getSourceBizType()) + "：";
                        }
                        return HMMsgType.getDescByValue(dbData.getSourceBizType()) + "：" + dbData.getTitle();
                    }

                    @Override
                    public String getMsgTime() {
                        return TimeUtil.formatMsgItemCreateTime(dbData.getStartTime());
                    }

                    @Override
                    public String getNotice() {
                        return StringUtil.getUnnullString(dbData.getNotice());
                    }

                    @Override
                    public String getMsgImage() {
                        return dbData.getImgUrl();
                    }

                    @Override
                    public String getMsgDetailLinkUrl() {
                        return dbData.getJumpUrl();
                    }

                    @Override
                    public String getMsgAutoId() {
                        String msgAutoId = dbData.getContentCollectId();
                        if (TextUtils.isEmpty(msgAutoId) || "0".equals(msgAutoId)) {
                            msgAutoId = "";
                        }
                        return msgAutoId;
                    }

                    @Override
                    public int getHMMsgType() {
                        return dbData.getSourceBizType();
                    }

                    @Override
                    public String getIMsgId() {
                        return dbData.getMsgId();
                    }

                    @Override
                    public String getIMsgType() {
                        return dbData.getType();
                    }

                    @Override
                    public boolean isHaveRead() {
                        return mIsHaveRead;
                    }

                    @Override
                    public void setHaveRead(boolean isHaveRead) {
                        mIsHaveRead = isHaveRead;
                    }

                    @Override
                    public int getItemType() {
                        if (HMMsgType.CommuniqueIntro.getValue() == dbData.getSourceBizType()) {
                            return TYPE_COMMUNIQUE;
                        }
                        return TYPE_ADVERTISEMENT_NEWS_SPORT;
                    }
                };
                msgItem.setHaveRead(dbData.isHaveRead());
                resultList.add(msgItem);
            }
        }

        return resultList;
    }

    /**
     * 支付宝回单
     *
     * @return
     */
    public static List<IAliPayMsgItem> changeAliPayDbDataToIAliPayItem(List<AliPayMsgDbData> list) {
        List<IAliPayMsgItem> resultList = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                AliPayMsgDbData dbData = list.get(i);
                final String title = dbData.getTitle();
                final String content = dbData.getContent();
                final String jumpUrl = dbData.getJumpUrl();
                final boolean isHaveRead = dbData.isHaveRead();
                final String msgId = dbData.getMsgId();
                final String msgType = dbData.getType();
                final String time = TimeUtil.formatMsgItemCreateTime(dbData.getCreateTime());
                IAliPayMsgItem iMsgItem = new IAliPayMsgItem() {

                    private boolean mIfShowTime = true;
                    private boolean mIsHaveRead = false;

                    @Override
                    public boolean isHaveRead() {
                        return mIsHaveRead;
                    }

                    @Override
                    public void setHaveRead(boolean isHaveRead) {
                        mIsHaveRead = isHaveRead;
                    }

                    @Override
                    public String getITime() {
                        return time;
                    }

                    @Override
                    public boolean ifIShowTime() {
                        return mIfShowTime;
                    }

                    @Override
                    public void setIfIShowTime(boolean isShowTime) {
                        mIfShowTime = isShowTime;
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
                    public String getIJumpUrl() {
                        return jumpUrl;
                    }

                    @Override
                    public String getIMsgId() {
                        return msgId;
                    }

                    @Override
                    public String getIMsgType() {
                        return msgType;
                    }
                };
                try {
                    if (i > 0) {
                        AliPayMsgDbData previewDbData = list.get(i - 1);
                        String strPreviewTime = previewDbData.getCreateTime();
                        String strCurrentTime = dbData.getCreateTime();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        long previewTime = df.parse(strPreviewTime).getTime();
                        long currentTime = df.parse(strCurrentTime).getTime();
                        if (currentTime - previewTime > 300000) {
                            iMsgItem.setIfIShowTime(true);
                        } else {
                            iMsgItem.setIfIShowTime(false);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                iMsgItem.setHaveRead(isHaveRead);
                resultList.add(iMsgItem);
            }
        }
        return resultList;
    }
}

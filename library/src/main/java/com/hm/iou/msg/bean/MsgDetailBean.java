package com.hm.iou.msg.bean;

import com.hm.iou.msg.R;
import com.hm.iou.msg.business.message.view.IMsgItem;
import com.hm.iou.msg.dict.MsgType;
import com.hm.iou.tools.TimeUtil;

import java.io.Serializable;

/**
 * Created by syl on 2018/5/30.
 */
public class MsgDetailBean implements IMsgItem, Serializable {

    /**
     * type : 3
     * autoId : 280
     * imageUrl : http://iou-steward.oss-cn-hangzhou.aliyuncs.com/contentCollect/20180530183241
     * title : 因为拍了一张照片，这名女子被警察逮捕了，引起全国关
     * infoLinkUrl : https://www.toutiao.com/a6561254954655285774/
     */

    private int type;
    private int autoId;
    private String pushDate;
    private String imageUrl;
    private String title;
    private String infoLinkUrl;

    //官方公告
    private boolean isRead = false;//是否阅览过
    private String communiqueIntro;//官方公告简介

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAutoId() {
        return autoId;
    }

    public void setAutoId(int autoId) {
        this.autoId = autoId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPushDate() {
        return pushDate;
    }

    public void setPushDate(String pushDate) {
        this.pushDate = pushDate;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfoLinkUrl() {
        return infoLinkUrl;
    }

    public void setInfoLinkUrl(String infoLinkUrl) {
        this.infoLinkUrl = infoLinkUrl;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setCommuniqueIntro(String communiqueIntro) {
        this.communiqueIntro = communiqueIntro;
    }

    @Override
    public boolean getMsgReadState() {
        return isRead;
    }

    @Override
    public int getMsgIcon() {
        if (MsgType.Sport.getValue() == type) {
            return R.mipmap.msg_icon_ad_or_sport;
        }
        if (MsgType.Advertisement.getValue() == type) {
            return R.mipmap.msg_icon_ad_or_sport;
        }
        if (MsgType.TopNews.getValue() == type) {
            return R.mipmap.msg_icon_news_or_top;
        }
        return R.mipmap.msg_icon_communique;
    }


    @Override
    public String getMsgTitle() {
        return MsgType.getDescByValue(type) + "：" + title;
    }

    @Override
    public String getCommuniqueIntro() {
        return communiqueIntro;
    }

    @Override
    public String getMsgTime() {
        return pushDate.replaceAll("-", ".");
    }

    @Override
    public String getMsgImage() {
        return imageUrl;
    }

    @Override
    public String getMsgDetailLinkUrl() {
        return infoLinkUrl;
    }

    @Override
    public int getItemType() {
        if (MsgType.CommuniqueIntro.getValue() == type) {
            return TYPE_COMMUNIQUE;
        }
        return TYPE_AD_OR_SPORT;
    }
}

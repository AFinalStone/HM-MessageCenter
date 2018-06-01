package com.hm.iou.msg.bean;

import com.hm.iou.msg.R;
import com.hm.iou.msg.business.message.view.IMsgItem;
import com.hm.iou.msg.dict.MsgType;

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
    private String imageUrl;
    private String title;
    private String infoLinkUrl;

    //官方公告
    private boolean isRead = false;//是否阅览过
    private String communiqueIntro;//官方公告简介

    public MsgDetailBean() {
    }

    public MsgDetailBean(int type, int autoId, String imageUrl, String title, String infoLinkUrl, boolean isRead, String communiqueIntro) {
        this.type = type;
        this.autoId = autoId;
        this.imageUrl = imageUrl;
        this.title = title;
        this.infoLinkUrl = infoLinkUrl;
        this.isRead = isRead;
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
            return R.mipmap.msg_icon_ad_or_sport;
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
        return "04-09 21:05";
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

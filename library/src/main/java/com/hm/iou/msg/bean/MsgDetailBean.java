package com.hm.iou.msg.bean;

import com.hm.iou.msg.R;
import com.hm.iou.msg.business.message.view.IMsgItem;

/**
 * Created by syl on 2018/5/30.
 */
public class MsgDetailBean implements IMsgItem {

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


    @Override
    public int getMsgIcon() {
        return R.mipmap.msg_logo;
    }

    @Override
    public String getMsgTitle() {
        return "标题：";
    }

    @Override
    public String getMsgContent() {
        return "3月4日22点-3月5日06点，进行系统升级，可能会 出现“服务器繁忙”等异常提示，特此公告。";
    }

    @Override
    public String getMsgSubContent() {
        return " 客服微信号：jietiaoguanjia2018 ";
    }

    @Override
    public String getMsgImage() {
        return "https://upload-images.jianshu.io/upload_images/972352-aa122442f6568954.gif?imageMogr2/auto-orient/strip%7CimageView2/2/w/341";
    }

    @Override
    public String getMsgDetailLinkUrl() {
        return "https://www.jianshu.com/p/b343fcff51b0";
    }

    @Override
    public int getItemType() {
        return TYPE_COMMUNIQUE;
    }
}

package com.hm.iou.msg.bean;

import com.hm.iou.msg.R;
import com.hm.iou.msg.business.message.view.IMsgItem;

/**
 * Created by syl on 2018/5/30.
 */
public class MsgDetailBean implements IMsgItem {


    @Override
    public int getIcon() {
        return R.mipmap.msg_logo;
    }

    @Override
    public String getTitle() {
        return "标题：";
    }

    @Override
    public String getContent() {
        return "3月4日22点-3月5日06点，进行系统升级，可能会 出现“服务器繁忙”等异常提示，特此公告。";
    }

    @Override
    public String getSubContent() {
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

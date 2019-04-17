package com.hm.iou.msg.business.hmmsg.view;

import android.support.annotation.DrawableRes;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author syl
 * @time 2018/5/30 上午11:51
 */
public interface IHmMsgItem extends MultiItemEntity {

    int TYPE_ADVERTISEMENT = 0;      //广告
    int TYPE_NEWS = 1;               //资讯
    int TYPE_COMMUNIQUE = 2;         //官方公告
    int TYPE_FEEDBACK = 3;           //意见反馈

    /**
     * 获取消息的icon
     *
     * @return
     */
    @DrawableRes
    int getMsgIcon();

    /**
     * 消息标题
     *
     * @return
     */
    String getMsgTitle();

    /**
     * 获取时间
     *
     * @return
     */
    String getMsgTime();

    /**
     * 获取官方公告简介
     *
     * @return
     */
    String getNotice();

    /**
     * 消息图片
     *
     * @return
     */
    String getMsgImage();

    /**
     * 具体链接地址
     *
     * @return
     */
    String getMsgDetailLinkUrl();

    /**
     * 获取消息的唯一id
     *
     * @return
     */
    String getMsgAutoId();

    /**
     * 获取消息类型
     *
     * @return
     */
    int getMsgType();
}

package com.hm.iou.msg.business.contract.view;

import android.support.annotation.DrawableRes;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author syl
 * @time 2018/5/30 上午11:51
 */
public interface IContractMsgItem extends MultiItemEntity {

    int TYPE_AD_OR_SPORT = 0;       //广告或者活动
    int TYPE_COMMUNIQUE = 1;       //官方公告

    /**
     * 是否已读
     *
     * @return
     */
    boolean getMsgReadState();

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

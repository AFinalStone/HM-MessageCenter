package com.hm.iou.msg.business.hmmsg.view;

import android.support.annotation.DrawableRes;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

/**
 * @author syl
 * @time 2018/5/30 上午11:51
 */
public interface IHmMsgItem extends MultiItemEntity {

    int TYPE_ADVERTISEMENT_NEWS_SPORT = 1;      //广告, 头条，活动
    int TYPE_COMMUNIQUE = 2;                    //官方公告
    int TYPE_FEEDBACK_CUSTOMER = 3;             //用户能看到的客服反馈消息
    int TYPE_FEEDBACK_STAFF = 4;                //嘿马员工能看到的客服反馈消息
    int TYPE_LAWYER = 5;                        //律师服务相关信息

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
     * 获取条管家消息类型
     *
     * @return
     */
    int getHMMsgType();

    /**
     * 消息id
     *
     * @return
     */
    String getIMsgId();

    /**
     * 消息类型
     *
     * @return
     */
    String getIMsgType();

    /**
     * 是否已经阅读过
     *
     * @return
     */
    boolean isHaveRead();

    /**
     * 设置是否已经阅读过
     *
     * @param isHaveRead
     */
    void setHaveRead(boolean isHaveRead);

    String getContent();

    List<String> getImgs();
}

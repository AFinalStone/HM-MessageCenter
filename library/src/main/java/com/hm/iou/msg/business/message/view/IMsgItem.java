package com.hm.iou.msg.business.message.view;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author syl
 * @time 2018/5/30 上午11:51
 */
public interface IMsgItem extends MultiItemEntity {

    int TYPE_AD_OR_SPORT = 0;       //广告或者活动
    int TYPE_COMMUNIQUE = 1;       //官方公告

    int getIcon();

    String getTitle();

    String getContent();

    String getSubContent();

    String getMsgImage();

    String getMsgDetailLinkUrl();
}

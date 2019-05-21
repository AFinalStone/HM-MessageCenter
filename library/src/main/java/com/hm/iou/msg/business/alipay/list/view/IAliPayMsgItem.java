package com.hm.iou.msg.business.alipay.list.view;

/**
 * Created by syl on 2019/4/11.
 */

public interface IAliPayMsgItem {
    /**
     * 是否已经阅读过
     *
     * @return
     */
    boolean isHaveRead();

    /**
     * 获取时间
     *
     * @return
     */
    String getITime();

    /**
     * 是否显示时间
     *
     * @return
     */
    boolean ifIShowTime();

    /**
     * 设置是否显示时间
     *
     * @return
     */
    void setIfIShowTime(boolean isShowTime);

    /**
     * 标题
     *
     * @return
     */
    String getITitle();


    /**
     * 内容
     *
     * @return
     */
    String getIContent();

    /**
     * 跳转链接
     *
     * @return
     */
    String getIJumpUrl();
}

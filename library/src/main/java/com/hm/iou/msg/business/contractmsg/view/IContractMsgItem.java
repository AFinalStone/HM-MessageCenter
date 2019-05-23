package com.hm.iou.msg.business.contractmsg.view;

/**
 * @author syl
 * @time 2018/5/30 上午11:51
 */
public interface IContractMsgItem {

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
     * 消息标题
     *
     * @return
     */
    String getITitle();

    /**
     * 消息图片
     *
     * @return
     */
    String getIContent();

    /**
     * 获取跳转的url
     *
     * @return
     */
    String getIJustUrl();

    /**
     * 获取合同类型
     *
     * @return
     */
    int getIContractType();

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
}


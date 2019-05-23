package com.hm.iou.msg.business.remindback.view;

/**
 * Created by syl on 2019/4/11.
 */

public interface IRemindBackMsgItem {

    /**
     * 标题
     *
     * @return
     */
    String getITitle();

    /**
     * 时间
     *
     * @return
     */
    String getITime();

    /**
     * 还款日期
     *
     * @return
     */
    String getIBackTime();

    /**
     * 归还物件/金额
     *
     * @return
     */
    String getIBackThingName();

    /**
     * 合同类型
     *
     * @return
     */
    int getIContractType();

    /**
     * 跳转的url
     *
     * @return
     */
    String getIJustUrl();

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

package com.hm.iou.msg.business.similarity.view;

/**
 * Created by syl on 2019/4/11.
 */

public interface ISimilarityContractMsgItem {

    /**
     * 金额
     *
     * @return
     */
    String getITitle();

    /**
     * 出借人姓名
     *
     * @return
     */
    String getILenderName();

    /**
     * 借款人姓名
     *
     * @return
     */
    String getIBorrowerName();

    /**
     * 时间
     *
     * @return
     */
    String getITime();

    /**
     * 归还时间
     *
     * @return
     */
    String getIBackTime();

    /**
     * 归还方式
     *
     * @return
     */
    String getIBackType();

    /**
     * 跳转的链接
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

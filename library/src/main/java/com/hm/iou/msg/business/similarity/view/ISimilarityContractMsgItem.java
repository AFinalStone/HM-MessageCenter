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
     * 合同logo资源id
     *
     * @return
     */
    int getILogoResId();

    /**
     * 出借人或者债权人
     *
     * @return
     */
    String getILender();

    /**
     * 出借人姓名
     *
     * @return
     */
    String getILenderName();

    /**
     * 借款人或者债务人
     *
     * @return
     */
    String getIBorrower();

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

}

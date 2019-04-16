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
    public String getITitle();

    /**
     * 出借人姓名
     *
     * @return
     */
    public String getILenderName();

    /**
     * 借款人姓名
     *
     * @return
     */
    public String getIBorrowerName();

    /**
     * 归还时间
     *
     * @return
     */
    public String getIBackTime();

    /**
     * 归还方式
     *
     * @return
     */
    public String getIBackType();

    /**
     * 跳转的链接
     *
     * @return
     */
    public String getIJustUrl();

    /**
     * 合同类型
     *
     * @return
     */
    public int getIContractType();
}

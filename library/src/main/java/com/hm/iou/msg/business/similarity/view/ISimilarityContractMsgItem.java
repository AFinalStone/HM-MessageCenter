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
     * 是否显示"待确认"、"已失效"标记
     *
     * @return
     */
    boolean showStatusTag();

    /**
     * 显示"待确认"、"已失效"标记的背景图片
     *
     * @return
     */
    int getStatusTagBg();
}

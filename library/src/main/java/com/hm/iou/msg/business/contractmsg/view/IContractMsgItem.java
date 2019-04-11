package com.hm.iou.msg.business.contractmsg.view;

/**
 * @author syl
 * @time 2018/5/30 上午11:51
 */
public interface IContractMsgItem {

    /**
     * 消息标题
     *
     * @return
     */
    String getITitle();

    /**
     * 获取时间
     *
     * @return
     */
    String getITime();

    /**
     * 消息图片
     *
     * @return
     */
    String getIContent();

    /**
     * 获取合同id
     *
     * @return
     */
    String getIContractId();

    /**
     * 获取合同类型
     *
     * @return
     */
    String getIContractType();
}


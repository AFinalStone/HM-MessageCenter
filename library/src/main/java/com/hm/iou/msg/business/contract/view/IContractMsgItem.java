package com.hm.iou.msg.business.contract.view;

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
    String getTitle();

    /**
     * 获取时间
     *
     * @return
     */
    String getTime();

    /**
     * 消息图片
     *
     * @return
     */
    String getContent();

    /**
     * 获取合同id
     *
     * @return
     */
    String getContractId();

    /**
     * 获取合同类型
     *
     * @return
     */
    String getContractType();
}


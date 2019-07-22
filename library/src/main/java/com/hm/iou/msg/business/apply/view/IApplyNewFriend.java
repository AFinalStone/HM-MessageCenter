package com.hm.iou.msg.business.apply.view;

/**
 * @author syl
 * @time 2018/5/30 上午11:51
 */
public interface IApplyNewFriend {

    /**
     * 头像
     *
     * @return
     */
    String getIHeaderImg();

    /**
     * 昵称
     *
     * @return
     */
    String getINick();

    /**
     * 内容
     *
     * @return
     */
    String getIContent();

    /**
     * 状态
     *
     * @return
     */
    int getIStatus();

    String getFriendId();

    String getApplyId();

    /**
     * 获取性别类型
     *
     * @return
     */
    int getSexType();

    String getStageName();
}


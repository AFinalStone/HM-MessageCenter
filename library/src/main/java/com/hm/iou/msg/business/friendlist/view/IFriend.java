package com.hm.iou.msg.business.friendlist.view;

/**
 * @author syl
 * @time 2018/5/30 上午11:51
 */
public interface IFriend {

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
     * 账号
     */
    String getIAccount();

    /**
     * 获取拼音
     *
     * @return
     */
    String getPinyin();
}


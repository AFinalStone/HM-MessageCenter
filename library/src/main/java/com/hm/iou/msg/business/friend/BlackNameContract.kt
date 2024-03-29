package com.hm.iou.msg.business.friend

import com.hm.iou.base.mvp.BaseContract

interface BlackNameContract {

    interface View: BaseContract.BaseView {

        /**
         * 显示黑名单描述说明
         */
        fun showBlackNameDesc(desc: String?)

        fun toAddFriend()
    }


    interface Presenter: BaseContract.BasePresenter {

        /**
         * 解除黑名单限制
         *
         * @param friendId 好友userId
         */
        fun removeBlackName(friendId: String?)

        /**
         * 解除黑名单并添加好友
         *
         * @param friendId 好友userId
         */
        fun removeBlackNameAndAddFriend(friendId: String?)

    }


}
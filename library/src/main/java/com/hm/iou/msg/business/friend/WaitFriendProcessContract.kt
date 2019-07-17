package com.hm.iou.msg.business.friend

import com.hm.iou.base.mvp.BaseContract

interface WaitFriendProcessContract {

    interface View: BaseContract.BaseView {

        /**
         * 显示头像
         */
        fun showAvatar(avatarUrl: String?)

        /**
         * 显示性别
         */
        fun showSexImage(sexResId: Int)

        /**
         * 显示等待好友处理的描述信息
         */
        fun showWaitProcessDesc(desc: String?)

        /**
         * 底部Button显示为"退回消息中心"样式
         */
        fun showReturnBtnStyle()

        /**
         * 底部Button显示为"重新添加好友"样式
         */
        fun showRetryBtnStyle()

        fun updateOver48hFlag(over48h: Boolean)
    }


    interface Presenter: BaseContract.BasePresenter {

        /**
         * 重新申请添加好友
         */
        fun refreshFriendApply(friendId: String?)

    }


}
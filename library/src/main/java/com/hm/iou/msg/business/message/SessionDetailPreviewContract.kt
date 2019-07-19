package com.hm.iou.msg.business.message

import com.hm.iou.base.mvp.BaseContract

/**
 * Created by syl on 2019/7/18.
 */
public class SessionDetailPreviewContract {

    interface Presenter : BaseContract.BasePresenter {
        fun checkFriendStatus(friendId: String?)
    }

    interface View : BaseContract.BaseView {

        /**
         * 显示被好友拉黑了
         */
        fun showHadInAnOtherBlackName(reason: String?)

        /**
         * 显示被好友删除了
         */
        fun showHadDeleteByAnOther(reason: String?)

        /**
         * 账号已经被注销了
         */
        fun showAccountHadLogout(headerUrl: String?, sexIconResId: Int?, name: String?, idAndNickName: String?, content: String?)

        /**
         * 账号被官方拉黑了
         */
        fun showAccountHadInSysBlackName(headerUrl: String?, sexIconResId: Int?, name: String?, idAndNickName: String?, content: String?)

        /**
         * 进入会话详情页面
         */
        fun toSessionDetail()
    }
}
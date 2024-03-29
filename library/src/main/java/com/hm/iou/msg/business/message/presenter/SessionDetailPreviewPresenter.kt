package com.hm.iou.msg.business.message.presenter

import android.app.Activity
import com.hm.iou.base.mvp.MvpActivityPresenter
import com.hm.iou.base.utils.CommSubscriber
import com.hm.iou.base.utils.RxUtil
import com.hm.iou.msg.R
import com.hm.iou.msg.api.MsgApi
import com.hm.iou.msg.bean.CheckForIMChatResBean
import com.hm.iou.msg.business.message.SessionDetailPreviewContract
import com.hm.iou.msg.dict.CheckIMFriendStatus
import com.hm.iou.sharedata.model.BaseResponse
import com.hm.iou.sharedata.model.SexEnum
import com.trello.rxlifecycle2.android.ActivityEvent

/**
 * Created by syl on 2019/7/18.
 */
class SessionDetailPreviewPresenter(context: Activity, view: SessionDetailPreviewContract.View) : MvpActivityPresenter<SessionDetailPreviewContract.View>(context, view), SessionDetailPreviewContract.Presenter {
    override fun checkFriendStatus(friendId: String?) {
        mView.showLoadingView()
        MsgApi.checkForIMChat(friendId)
                .compose(provider.bindUntilEvent<BaseResponse<CheckForIMChatResBean>>(ActivityEvent.DESTROY))
                .map(RxUtil.handleResponse<CheckForIMChatResBean>())
                .subscribeWith(object : CommSubscriber<CheckForIMChatResBean>(mView) {

                    override fun handleResult(p0: CheckForIMChatResBean?) {
                        mView.dismissLoadingView()
                        if (p0 == null) {
                            mView.closeCurrPage()
                            return@handleResult
                        }
                        p0.let {
                            when (it.result) {
                                CheckIMFriendStatus.NORMAL.type -> mView.toSessionDetail()
                                CheckIMFriendStatus.BLACK_NAME.type -> mView.showHadInAnOtherBlackName(it.content)
                                CheckIMFriendStatus.DELETE.type -> mView.showHadDeleteByAnOther(it.content)
                                CheckIMFriendStatus.LOGOUT.type -> {
                                    var sexIconResId: Int? = null
                                    when (it.sex) {
                                        SexEnum.FEMALE.value -> sexIconResId = R.mipmap.uikit_ic_gender_woman
                                        SexEnum.MALE.value -> sexIconResId = R.mipmap.uikit_ic_gender_man
                                    }
                                    var name: String? = it.stageName
                                    var nickName: String? = it.nickName
                                    if (name.isNullOrEmpty()) {
                                        name = nickName
                                    }
                                    var idAndName = "ID：" + it.showId + "（$nickName）"

                                    mView.showAccountHadLogout(it.avatarUrl, sexIconResId, name, idAndName, it.content)
                                }
                                CheckIMFriendStatus.SYS_BLACK_NAME.type -> {
                                    var sexIconResId: Int? = null
                                    when (it.sex) {
                                        SexEnum.FEMALE.value -> sexIconResId = R.mipmap.uikit_ic_gender_woman
                                        SexEnum.MALE.value -> sexIconResId = R.mipmap.uikit_ic_gender_man
                                    }
                                    var name: String? = it.stageName
                                    var nickName: String? = it.nickName
                                    if (name.isNullOrEmpty()) {
                                        name = nickName
                                    }
                                    var idAndName = "ID：" + it.showId + "（$nickName）"

                                    mView.showAccountHadInSysBlackName(it.avatarUrl, sexIconResId, name, idAndName, it.content)
                                }
                            }
                        }
                    }

                    override fun handleException(p0: Throwable?, p1: String?, p2: String?) {
                        mView.dismissLoadingView()
                        mView.closeCurrPage()
                    }

                })

    }

}
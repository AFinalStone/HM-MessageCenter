package com.hm.iou.msg.business.friend.presenter

import android.content.Context
import com.hm.iou.base.mvp.MvpActivityPresenter
import com.hm.iou.base.utils.CommSubscriber
import com.hm.iou.base.utils.RxUtil
import com.hm.iou.msg.api.MsgApi
import com.hm.iou.msg.business.friend.BlackNameContract
import com.trello.rxlifecycle2.android.ActivityEvent

/**
 * 黑名单Presenter
 */
class BlackNamePresenter : MvpActivityPresenter<BlackNameContract.View>, BlackNameContract.Presenter {

    constructor(context: Context, view: BlackNameContract.View) : super(context, view)

    override fun removeBlackName(friendId: String?) {
        mView.showLoadingView()
        MsgApi.removeBlackName(friendId)
                .compose(provider.bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.handleResponse())
                .subscribeWith(object : CommSubscriber<Any>(mView) {
                    override fun handleResult(p0: Any?) {
                        mView.dismissLoadingView()
                        mView.toastMessage("限制已解除")
                        mView.closeCurrPage()
                    }

                    override fun handleException(p0: Throwable?, p1: String?, p2: String?) {
                        mView.dismissLoadingView()
                    }
                })
    }

    override fun removeBlackNameAndAddFriend(friendId: String?) {
        mView.showLoadingView()
        MsgApi.removeBlackName(friendId)
                .compose(provider.bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.handleResponse())
                .subscribeWith(object : CommSubscriber<Any>(mView) {
                    override fun handleResult(p0: Any?) {
                        mView.dismissLoadingView()
                        mView.toAddFriend()
                    }

                    override fun handleException(p0: Throwable?, p1: String?, p2: String?) {
                        mView.dismissLoadingView()
                    }
                })
    }
}
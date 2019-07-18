package com.hm.iou.msg.business.friend.presenter

import android.content.Context
import com.hm.iou.base.mvp.MvpActivityPresenter
import com.hm.iou.base.utils.CommSubscriber
import com.hm.iou.base.utils.RxUtil
import com.hm.iou.msg.api.MsgApi
import com.hm.iou.msg.business.friend.WaitFriendProcessContract
import com.trello.rxlifecycle2.android.ActivityEvent

class WaitFriendProcessPresenter : MvpActivityPresenter<WaitFriendProcessContract.View>, WaitFriendProcessContract.Presenter {

    constructor(context: Context, view: WaitFriendProcessContract.View) : super(context, view)

    override fun refreshFriendApply(friendId: String?) {
        if (friendId.isNullOrEmpty())
            return
        mView.showLoadingView()
        MsgApi.refreshFriendApply(friendId)
                .compose(provider.bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.handleResponse())
                .subscribeWith(object : CommSubscriber<String>(mView) {
                    override fun handleResult(desc: String?) {
                        mView.dismissLoadingView()
                        mView.showWaitProcessDesc(desc)
                        mView.showReturnBtnStyle()
                        mView.updateOver48hFlag(false)
                    }

                    override fun handleException(p0: Throwable?, p1: String?, p2: String?) {
                        mView.dismissLoadingView()
                    }
                })
    }
}
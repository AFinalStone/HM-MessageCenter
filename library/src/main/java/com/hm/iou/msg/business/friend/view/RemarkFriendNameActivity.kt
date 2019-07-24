package com.hm.iou.msg.business.friend.view

import android.os.Bundle
import com.hm.iou.base.BaseActivity
import com.hm.iou.base.mvp.BaseContract
import com.hm.iou.base.mvp.MvpActivityPresenter
import com.hm.iou.base.utils.CommSubscriber
import com.hm.iou.base.utils.RxUtil
import com.hm.iou.msg.R
import com.hm.iou.msg.api.MsgApi
import com.hm.iou.msg.event.UpdateFriendEvent
import com.hm.iou.tools.kt.click
import com.hm.iou.tools.kt.extraDelegate
import kotlinx.android.synthetic.main.msgcenter_activity_remark_friend_name.*
import org.greenrobot.eventbus.EventBus

/**
 * 设置好友备注名
 */
class RemarkFriendNameActivity : BaseActivity<MvpActivityPresenter<BaseContract.BaseView>>() {

    companion object {
        const val EXTRA_FRIEND_ID: String = "friend_id"
        const val EXTRA_IS_FRIEND: String = "is_friend"
        const val EXTRA_DEF_REMARK_NAME: String = "def_remark"
    }

    private var mFriendId: String? by extraDelegate(EXTRA_FRIEND_ID, null)
    private var mIsFriend: Boolean? by extraDelegate(EXTRA_IS_FRIEND, false)
    private var mDefRemarkName: String? by extraDelegate(EXTRA_DEF_REMARK_NAME, null)

    override fun initPresenter(): MvpActivityPresenter<BaseContract.BaseView>? = null

    override fun getLayoutId(): Int = R.layout.msgcenter_activity_remark_friend_name

    override fun initEventAndData(bundle: Bundle?) {
        if (bundle != null) {
            mFriendId = bundle.getString(EXTRA_FRIEND_ID)
            mIsFriend = bundle.getBoolean(EXTRA_IS_FRIEND, false)
            mDefRemarkName = bundle.getString(EXTRA_DEF_REMARK_NAME)
        }
        et_verify_content.setText(mDefRemarkName ?: "")
        et_verify_content.setSelection(et_verify_content.length())

        btn_verify_send.click {
            val name = et_verify_content.text.toString().trim()
            if (name.isNullOrEmpty()) {
                toastMessage("请输入备注名")
                return@click
            }
            if (mIsFriend == true) {
                updateFriendRemarkName(name)
            } else {
                updateApplyRemarkName(name)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let {
            outState.putString(EXTRA_FRIEND_ID, mFriendId)
            outState.putBoolean(EXTRA_IS_FRIEND, mIsFriend ?: false)
            outState.putString(EXTRA_DEF_REMARK_NAME, mDefRemarkName)
        }
    }

    /**
     * 更新好友的备注名
     */
    private fun updateFriendRemarkName(name: String) {
        showLoadingView()
        MsgApi.updateRemarkName(mFriendId, name)
                .map(RxUtil.handleResponse())
                .subscribeWith(object : CommSubscriber<Any>(this) {
                    override fun handleResult(p0: Any?) {
                        dismissLoadingView()
                        val updateFriendEvent = UpdateFriendEvent()
                        updateFriendEvent.reamrkName = name
                        EventBus.getDefault().post(updateFriendEvent)
                        closeCurrPage()
                    }

                    override fun handleException(p0: Throwable?, p1: String?, p2: String?) {
                        dismissLoadingView()
                    }
                })
    }

    /**
     * 更新好友申请的备注名
     */
    private fun updateApplyRemarkName(name: String) {
        showLoadingView()
        MsgApi.updateApplyRemarkName(mFriendId, name)
                .map(RxUtil.handleResponse())
                .subscribeWith(object : CommSubscriber<Any>(this) {
                    override fun handleResult(p0: Any?) {
                        dismissLoadingView()
                        val updateFriendEvent = UpdateFriendEvent()
                        updateFriendEvent.reamrkName = name
                        EventBus.getDefault().post(updateFriendEvent)
                        closeCurrPage()
                    }

                    override fun handleException(p0: Throwable?, p1: String?, p2: String?) {
                        dismissLoadingView()
                    }
                })
    }

}
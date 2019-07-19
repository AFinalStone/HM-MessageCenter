package com.hm.iou.msg.business.message.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.hm.iou.base.BaseActivity
import com.hm.iou.msg.R
import com.hm.iou.msg.business.message.SessionDetailPreviewContract
import com.hm.iou.msg.business.message.presenter.SessionDetailPreviewPresenter
import com.hm.iou.tools.kt.extraDelegate
import com.hm.iou.tools.kt.getValue
import com.hm.iou.tools.kt.putValue
import com.netease.nim.uikit.api.NimUIKit
import kotlinx.android.synthetic.main.msgcenter_activity_session_detail_preview.*

/**
 * Created by syl on 2019/7/18.
 */
class SessionDetailPreviewActivity : BaseActivity<SessionDetailPreviewPresenter>(), SessionDetailPreviewContract.View {

    companion object StaticParams {
        val EXTRA_KEY_FRIEND_ID = "friend_id"
        val EXTRA_KEY_FRIEND_IM_ID = "friend_im_id"
    }

    var mFriendId: String? by extraDelegate(EXTRA_KEY_FRIEND_ID, null);
    var mFriendImId: String? by extraDelegate(EXTRA_KEY_FRIEND_IM_ID, null);


    override fun initEventAndData(p0: Bundle?) {
        p0?.let {
            mFriendId = p0.getValue(EXTRA_KEY_FRIEND_ID)
            mFriendImId = p0.getValue(EXTRA_KEY_FRIEND_IM_ID)
        }
        mFriendId?.let { mPresenter.checkFriendStatus(it) }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putValue(EXTRA_KEY_FRIEND_ID, mFriendId)
        outState?.putValue(EXTRA_KEY_FRIEND_IM_ID, mFriendImId)
    }

    override fun initPresenter(): SessionDetailPreviewPresenter = SessionDetailPreviewPresenter(mContext, this)

    override fun getLayoutId(): Int = R.layout.msgcenter_activity_session_detail_preview

    override fun showHadDeleteByAnOther(reason: String?) {
        val view: View = viewStub_black_or_delete.inflate()
        reason?.let {
            view.findViewById<TextView>(R.id.tv_content).text = reason
        }
    }

    override fun showHadInAnOtherBlackName(reason: String?) {
        val view: View = viewStub_black_or_delete.inflate()
        reason?.let {
            view.findViewById<TextView>(R.id.tv_content).text = reason
        }
    }

    override fun showAccountHadLogout() {
        val view: View = viewStub_account_logout_or_black.inflate()
    }

    override fun showAccountHadInSysBlackName() {
        val view: View = viewStub_account_logout_or_black.inflate()
    }

    override fun toSessionDetail() {
        NimUIKit.startP2PSession(mContext, mFriendImId)
    }
}
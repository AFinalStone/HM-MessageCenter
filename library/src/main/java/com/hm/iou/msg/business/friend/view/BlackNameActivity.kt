package com.hm.iou.msg.business.friend.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.hm.iou.base.BaseActivity
import com.hm.iou.msg.NavigationHelper
import com.hm.iou.msg.R
import com.hm.iou.msg.bean.FriendInfo
import com.hm.iou.msg.business.friend.BlackNameContract
import com.hm.iou.msg.business.friend.presenter.BlackNamePresenter
import com.hm.iou.tools.kt.click
import com.hm.iou.tools.kt.extraDelegate
import com.hm.iou.tools.kt.unPackData
import kotlinx.android.synthetic.main.msgcenter_activity_friend_blackname.*

/**
 * 黑名单
 */
class BlackNameActivity : BaseActivity<BlackNamePresenter>(), BlackNameContract.View {

    companion object {

        const val EXTRA_KEY_FRIEND_ID = "friendId"
        const val EXTRA_KEY_DESC = "desc"
        const val EXTRA_KEY_BLOCKED_BY_OTHER = "blocked_by_other"
        const val EXTRA_KEY_FRIEND_INFO = "friend_info"       //好友详情信息

        private const val REQ_ADD_FRIEND = 100
    }

    private var mFriendId: String? by extraDelegate(EXTRA_KEY_FRIEND_ID, null)
    private var mDesc: String? by extraDelegate(EXTRA_KEY_DESC, null)
    private var mBlockedByOther: Boolean? by extraDelegate(EXTRA_KEY_BLOCKED_BY_OTHER, false)
    private var mFriendInfo: FriendInfo? by extraDelegate(EXTRA_KEY_FRIEND_INFO, null)

    override fun initPresenter(): BlackNamePresenter = BlackNamePresenter(this, this)

    override fun getLayoutId(): Int = R.layout.msgcenter_activity_friend_blackname

    override fun initEventAndData(bundle: Bundle?) {
        bundle?.let {
            mFriendId = unPackData(bundle, EXTRA_KEY_FRIEND_ID)
            mDesc = unPackData(bundle, EXTRA_KEY_DESC)
            mBlockedByOther = unPackData(bundle, EXTRA_KEY_BLOCKED_BY_OTHER)
            mFriendInfo = unPackData(bundle, EXTRA_KEY_FRIEND_INFO)
        }

        showBlackNameDesc(mDesc)

        if (mBlockedByOther == true) {
            tv_black_title.text = "含泪把你拉黑"
            btn_back_relieve.visibility = View.INVISIBLE
            btn_back_add.visibility = View.INVISIBLE
        }

        btn_back_relieve.click {
            mPresenter.removeBlackName(mFriendId)
        }
        btn_back_add.click {
            NavigationHelper.toSendVerifyRequestPage(this, mFriendId, true, mFriendInfo, REQ_ADD_FRIEND)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let {
            outState.putString(EXTRA_KEY_FRIEND_ID, mFriendId)
            outState.putString(EXTRA_KEY_DESC, mDesc)
            outState.putBoolean(EXTRA_KEY_BLOCKED_BY_OTHER, mBlockedByOther ?: false)
            outState.putParcelable(EXTRA_KEY_FRIEND_INFO, mFriendInfo)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_ADD_FRIEND) {
            if (resultCode == Activity.RESULT_OK) {
                finish()
            }
        }
    }

    override fun showBlackNameDesc(desc: String?) {
        tv_black_desc.text = desc
    }

}
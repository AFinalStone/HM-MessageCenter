package com.hm.iou.msg.business.friend.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.hm.iou.base.BaseActivity
import com.hm.iou.msg.NavigationHelper
import com.hm.iou.msg.R
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

        private const val REQ_ADD_FRIEND = 100
    }

    private var mFriendId: String? by extraDelegate(EXTRA_KEY_FRIEND_ID, null)
    private var mSex: Int? by extraDelegate(WaitFriendProcessActivity.EXTRA_KEY_SEX, 3)
    private var mAvatar: String? by extraDelegate(WaitFriendProcessActivity.EXTRA_KEY_AVATAR, null)
    private var mDesc: String? by extraDelegate(WaitFriendProcessActivity.EXTRA_KEY_DESC, null)

    override fun initPresenter(): BlackNamePresenter = BlackNamePresenter(this, this)

    override fun getLayoutId(): Int = R.layout.msgcenter_activity_friend_blackname

    override fun initEventAndData(bundle: Bundle?) {
        bundle?.let {
            mFriendId = unPackData(bundle, EXTRA_KEY_FRIEND_ID)
            mDesc = unPackData(bundle, EXTRA_KEY_DESC)
        }

        showBlackNameDesc(mDesc)

        btn_back_relieve.click {
            mPresenter.removeBlackName(mFriendId)
        }
        btn_back_add.click {
            NavigationHelper.toSendVerifyRequestPage(this, mFriendId, true, null, REQ_ADD_FRIEND)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let {
            outState.putString(EXTRA_KEY_FRIEND_ID, mFriendId)
            outState.putString(EXTRA_KEY_DESC, mDesc)
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
package com.hm.iou.msg.business.friend.view

import android.os.Bundle
import android.view.View
import com.hm.iou.base.BaseActivity
import com.hm.iou.base.mvp.BaseContract
import com.hm.iou.base.mvp.MvpActivityPresenter
import com.hm.iou.msg.R
import com.hm.iou.tools.ImageLoader
import com.hm.iou.tools.kt.click
import com.hm.iou.tools.kt.extraDelegate
import com.hm.iou.tools.kt.unPackData
import kotlinx.android.synthetic.main.msgcenter_activity_friend_account_closed.*
import kotlinx.android.synthetic.main.msgcenter_activity_friend_wait_to_process.iv_friend_avatar
import kotlinx.android.synthetic.main.msgcenter_activity_friend_wait_to_process.iv_friend_sex
import kotlinx.android.synthetic.main.msgcenter_activity_friend_wait_to_process.tv_friend_desc

/**
 * 账户已注销 或 账户被拉黑
 */
class AccountClosedActivity : BaseActivity<MvpActivityPresenter<BaseContract.BaseView>>() {

    companion object {
        const val EXTRA_KEY_FRIEND_ID = "friendId"
        const val EXTRA_KEY_SEX = "sex"
        const val EXTRA_KEY_AVATAR = "avatar"
        const val EXTRA_KEY_DESC = "desc"
        const val EXTRA_KEY_TITLE = "title"
    }

    private var mFriendId: String? by extraDelegate(EXTRA_KEY_FRIEND_ID, null)
    private var mSex: Int? by extraDelegate(EXTRA_KEY_SEX, 3)
    private var mAvatar: String? by extraDelegate(EXTRA_KEY_AVATAR, null)
    private var mDesc: String? by extraDelegate(EXTRA_KEY_DESC, null)
    private var mTitle: String? by extraDelegate(EXTRA_KEY_TITLE, null)

    override fun initPresenter(): MvpActivityPresenter<BaseContract.BaseView>? = null

    override fun getLayoutId(): Int = R.layout.msgcenter_activity_friend_account_closed

    override fun initEventAndData(bundle: Bundle?) {
        bundle?.let {
            mFriendId = unPackData(bundle, EXTRA_KEY_FRIEND_ID)
            mSex = unPackData(bundle, EXTRA_KEY_SEX)
            mAvatar = unPackData(bundle, EXTRA_KEY_AVATAR)
            mDesc = unPackData(bundle, EXTRA_KEY_DESC)
            mTitle = unPackData(bundle, EXTRA_KEY_TITLE)
        }

        tv_friend_label.text = title
        showAvatar(mAvatar)
        showSexImage(if (mSex == 0) R.mipmap.uikit_ic_gender_woman else if (mSex == 1) R.mipmap.uikit_ic_gender_man else 0)
        showAccountClosedDesc(mDesc)

        btn_friend_known.click {
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let {
            outState.putString(EXTRA_KEY_FRIEND_ID, mFriendId)
            outState.putInt(EXTRA_KEY_SEX, mSex ?: 3)
            outState.putString(EXTRA_KEY_AVATAR, mAvatar)
            outState.putString(EXTRA_KEY_DESC, mDesc)
            outState.putString(EXTRA_KEY_TITLE, mTitle)
        }
    }

    private fun showAvatar(avatarUrl: String?) {
        if (avatarUrl.isNullOrEmpty()) {
            iv_friend_avatar.setImageResource(R.mipmap.uikit_icon_header_unknow)
        } else {
            ImageLoader.getInstance(this)
                    .displayImage(avatarUrl, iv_friend_avatar, R.mipmap.uikit_icon_header_unknow, R.mipmap.uikit_icon_header_unknow)
        }
    }

    private fun showSexImage(sexResId: Int) {
        if (sexResId <= 0) {
            iv_friend_sex.visibility = View.GONE
        } else {
            iv_friend_sex.visibility = View.VISIBLE
            iv_friend_sex.setImageResource(sexResId)
        }
    }

    private fun showAccountClosedDesc(desc: String?) {
        tv_friend_desc.text = desc
    }


}
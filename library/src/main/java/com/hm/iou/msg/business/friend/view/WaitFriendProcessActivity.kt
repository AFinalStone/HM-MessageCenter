package com.hm.iou.msg.business.friend.view

import android.os.Bundle
import android.view.View
import com.hm.iou.base.BaseActivity
import com.hm.iou.msg.NavigationHelper
import com.hm.iou.msg.R
import com.hm.iou.msg.business.friend.WaitFriendProcessContract
import com.hm.iou.msg.business.friend.presenter.WaitFriendProcessPresenter
import com.hm.iou.tools.ImageLoader
import com.hm.iou.tools.kt.click
import com.hm.iou.tools.kt.extraDelegate
import com.hm.iou.tools.kt.unPackData
import kotlinx.android.synthetic.main.msgcenter_activity_friend_wait_to_process.*

/**
 * 等待好友处理
 */
class WaitFriendProcessActivity : BaseActivity<WaitFriendProcessPresenter>(), WaitFriendProcessContract.View {

    companion object {
        const val EXTRA_KEY_FRIEND_ID = "friendId"
        const val EXTRA_KEY_SEX = "sex"
        const val EXTRA_KEY_AVATAR = "avatar"
        const val EXTRA_KEY_DESC = "desc"
        const val EXTRA_KEY_OVER_48H = "over48h"
    }

    private var mFriendId: String? by extraDelegate(EXTRA_KEY_FRIEND_ID, null)
    private var mSex: Int? by extraDelegate(EXTRA_KEY_SEX, 3)
    private var mAvatar: String? by extraDelegate(EXTRA_KEY_AVATAR, null)
    private var mDesc: String? by extraDelegate(EXTRA_KEY_DESC, null)
    private var mOver48H: Boolean? by extraDelegate(EXTRA_KEY_OVER_48H, false)

    override fun initPresenter(): WaitFriendProcessPresenter = WaitFriendProcessPresenter(this, this)

    override fun getLayoutId(): Int = R.layout.msgcenter_activity_friend_wait_to_process

    override fun initEventAndData(bundle: Bundle?) {
        bundle?.let {
            mFriendId = unPackData(bundle, EXTRA_KEY_FRIEND_ID)
            mSex = unPackData(bundle, EXTRA_KEY_SEX)
            mAvatar = unPackData(bundle, EXTRA_KEY_AVATAR)
            mDesc = unPackData(bundle, EXTRA_KEY_DESC)
            mOver48H = unPackData(bundle, EXTRA_KEY_OVER_48H)
        }

        showAvatar(mAvatar)
        showSexImage(if (mSex == 0) R.mipmap.uikit_ic_gender_woman else if (mSex == 1) R.mipmap.uikit_ic_gender_man else 0)
        showWaitProcessDesc(mDesc)
        when(mOver48H) {
            true -> showRetryBtnStyle()
            else -> showReturnBtnStyle()
        }

        //点击返回"消息中心"
        btn_friend_return.click {
            when(mOver48H) {
                true -> mPresenter.refreshFriendApply(mFriendId)
                else ->  {
                    NavigationHelper.toMessageCenter(this)
                    finish()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let {
            outState.putString(EXTRA_KEY_FRIEND_ID, mFriendId)
            outState.putInt(EXTRA_KEY_SEX, mSex ?: 3)
            outState.putString(EXTRA_KEY_AVATAR, mAvatar)
            outState.putString(EXTRA_KEY_DESC, mDesc)
            outState.putBoolean(EXTRA_KEY_OVER_48H, mOver48H ?: false)
        }
    }

    override fun showAvatar(avatarUrl: String?) {
        if (avatarUrl.isNullOrEmpty()) {
            iv_friend_avatar.setImageResource(R.mipmap.uikit_icon_header_unknow)
        } else {
            ImageLoader.getInstance(this)
                    .displayImage(avatarUrl, iv_friend_avatar, R.mipmap.uikit_icon_header_unknow, R.mipmap.uikit_icon_header_unknow)
        }
    }

    override fun showSexImage(sexResId: Int) {
        if (sexResId <= 0) {
            iv_friend_sex.visibility = View.GONE
        } else {
            iv_friend_sex.visibility = View.VISIBLE
            iv_friend_sex.setImageResource(sexResId)
        }
    }

    override fun showWaitProcessDesc(desc: String?) {
        tv_friend_desc.text = desc
    }

    override fun showReturnBtnStyle() {
        btn_friend_return.text = "退回“消息中心”"
        btn_friend_return.setBackgroundResource(R.drawable.uikit_selector_btn_bordered)
        btn_friend_return.setTextColor(resources.getColor(R.color.uikit_selector_btn_main))
    }

    override fun showRetryBtnStyle() {
        btn_friend_return.text = "重新申请添加好友"
        btn_friend_return.setBackgroundResource(R.drawable.uikit_selector_btn_main)
        btn_friend_return.setTextColor(resources.getColor(R.color.uikit_selector_btn_main))
    }

    override fun updateOver48hFlag(over48h: Boolean) {
        mOver48H = over48h
    }

}
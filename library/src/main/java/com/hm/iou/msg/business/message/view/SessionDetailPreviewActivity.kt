package com.hm.iou.msg.business.message.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.hm.iou.base.BaseActivity
import com.hm.iou.msg.R
import com.hm.iou.msg.business.message.SessionDetailPreviewContract
import com.hm.iou.msg.business.message.presenter.SessionDetailPreviewPresenter
import com.hm.iou.tools.ImageLoader
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
        mPresenter.checkFriendStatus(mFriendId)
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
        view.findViewById<TextView>(R.id.tv_title).text = "友谊的小船翻了"
        view.findViewById<Button>(R.id.btn_see_session_history).setOnClickListener({
            toSessionDetail()
        })
        reason?.let {
            view.findViewById<TextView>(R.id.tv_content).text = reason
        }
    }

    override fun showHadInAnOtherBlackName(reason: String?) {
        val view: View = viewStub_black_or_delete.inflate()
        view.findViewById<TextView>(R.id.tv_title).text = "含泪把你拉黑"
        view.findViewById<Button>(R.id.btn_see_session_history).setOnClickListener({
            toSessionDetail()
        })
        reason?.let {
            view.findViewById<TextView>(R.id.tv_content).text = reason
        }
    }

    override fun showAccountHadLogout(headerUrl: String?, sexIconResId: Int?, name: String?, idAndNickName: String?, content: String?) {
        val view: View = viewStub_account_logout_or_black.inflate()
        ImageLoader.getInstance(mContext).displayImage(headerUrl, view.findViewById(R.id.iv_header), R.drawable.uikit_bg_pic_loading_place, R.mipmap.uikit_icon_header_unknow)
        view.findViewById<TextView>(R.id.tv_status).text = "【账户已注销】"
        view.findViewById<Button>(R.id.btn_ok).setOnClickListener({
            onBackPressed()
        })
        sexIconResId?.let {
            view.findViewById<ImageView>(R.id.iv_sex).setImageResource(sexIconResId)
        }
        name?.let {
            view.findViewById<TextView>(R.id.tv_name).text = name
        }
        idAndNickName?.let {
            view.findViewById<TextView>(R.id.tv_id_and_nick_name).text = idAndNickName
        }
        content?.let {
            view.findViewById<TextView>(R.id.tv_content).text = content
        }
    }

    override fun showAccountHadInSysBlackName(headerUrl: String?, sexIconResId: Int?, name: String?, idAndNickName: String?, content: String?) {
        val view: View = viewStub_account_logout_or_black.inflate()
        ImageLoader.getInstance(mContext).displayImage(headerUrl, view.findViewById(R.id.iv_header), R.drawable.uikit_bg_pic_loading_place, R.mipmap.uikit_icon_header_unknow)
        view.findViewById<TextView>(R.id.tv_status).text = "【账户被拉黑】"
        view.findViewById<Button>(R.id.btn_ok).setOnClickListener({
            onBackPressed()
        })
        sexIconResId?.let {
            view.findViewById<ImageView>(R.id.iv_sex).setImageResource(sexIconResId)
        }
        name?.let {
            view.findViewById<TextView>(R.id.tv_name).text = name
        }
        idAndNickName?.let {
            view.findViewById<TextView>(R.id.tv_id_and_nick_name).text = idAndNickName
        }
        content?.let {
            view.findViewById<TextView>(R.id.tv_content).text = content
        }
    }

    override fun toSessionDetail() {
        NimUIKit.startP2PSession(mContext, mFriendImId)
        finish()
    }
}
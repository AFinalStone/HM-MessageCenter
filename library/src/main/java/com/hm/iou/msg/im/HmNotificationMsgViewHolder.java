package com.hm.iou.msg.im;

import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.R;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.business.friend.view.SendVerifyRequestActivity;
import com.hm.iou.tools.ToastUtil;
import com.netease.nim.uikit.business.session.emoji.MoonUtil;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;

/**
 * @author syl
 * @time 2019/4/15 10:21 AM
 */
public class HmNotificationMsgViewHolder extends MsgViewHolderBase {

    private TextView mTvMsg;
    private String mIsNoFriend;
    private String mIsInBlack;
    private int mAddFriendColor;
    private boolean mIsNoFriendFlag;

    public HmNotificationMsgViewHolder(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msgcenter_item_im_chat_view_holder_notication;
    }

    @Override
    protected void inflateContentView() {
        mTvMsg = view.findViewById(com.netease.nim.uikit.R.id.tv_msg);
        mIsNoFriend = view.getContext().getString(R.string.no_friend_send_tip);
        mIsInBlack = view.getContext().getString(R.string.black_list_send_tip);
        mAddFriendColor = view.getContext().getResources().getColor(R.color.uikit_function_remind);
        mTvMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsNoFriendFlag) {
                    NavigationHelper.toSendVerifyRequestPage(view.getContext(), message.getSessionId());
                }
            }
        });
    }

    @Override
    protected void bindContentView() {
        String text = message.getContent();
        if (TextUtils.isEmpty(text)) {
            text = "未知通知提醒";
        }
        if (mIsNoFriend.equals(text)) {
            mIsNoFriendFlag = true;
            SpannableString spannableString = new SpannableString(text);
            ForegroundColorSpan span = new ForegroundColorSpan(mAddFriendColor);
            //将这个Span应用于指定范围的字体
            spannableString.setSpan(span, text.length() - 4, text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            mTvMsg.setText(spannableString);
        } else {
            mIsNoFriendFlag = false;
            MoonUtil.identifyFaceExpressionAndATags(context, mTvMsg, text, ImageSpan.ALIGN_BOTTOM);
            mTvMsg.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
    }


}

package com.hm.iou.msg.business.message.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;
import com.hm.iou.msg.bean.ChatMsgBean;
import com.hm.iou.tools.ImageLoader;
import com.hm.iou.uikit.HMDotTextView;

/**
 * Created by syl on 18/4/28.<br>
 */

public class ChatMsgListAdapter extends BaseQuickAdapter<ChatMsgBean, BaseViewHolder> {

    public ChatMsgListAdapter(Context context) {
        super(R.layout.msgcenter_item_msg_list_item);
        mContext = context;
    }


    @Override
    protected void convert(BaseViewHolder helper, ChatMsgBean item) {
        //头像
        ImageView ivHeader = helper.getView(R.id.iv_from_header);
        int headerDefaultResId = R.mipmap.uikit_icon_header_unknow;
        if (1 == item.getSex()) {
            headerDefaultResId = R.mipmap.person_ic_header_man;
        } else if (2 == item.getSex()) {
            headerDefaultResId = R.mipmap.person_ic_header_wuman;
        }

        if (0 == item.getStatus()) {//正在发送
            helper.setImageResource(R.id.iv_alert, R.mipmap.msgcenter_ic_chat_sending_flag);
            helper.setGone(R.id.iv_alert, true);
        } else if (2 == item.getStatus()) {//发送哦失败
            helper.setImageResource(R.id.iv_alert, R.drawable.nim_ic_failed);
            helper.setGone(R.id.iv_alert, true);
        } else {
            helper.setGone(R.id.iv_alert, false);
        }

        ImageLoader.getInstance(mContext).displayImage(item.getContractHeaderImage(), ivHeader, headerDefaultResId, headerDefaultResId);
        //名字
        helper.setText(R.id.tv_from_nick, item.getContractShowName());
        //内容
        helper.setText(R.id.tv_chat_content, item.getChatContent());
        //时间
        helper.setText(R.id.tv_time, item.getTime());
        //未读消息数量
        int redMsgNum = item.getRedMsgNum();
        HMDotTextView dotTextView = helper.getView(R.id.dot_chat_red_msg_num);
        if (redMsgNum > 99) {
            dotTextView.setVisibility(View.VISIBLE);
            dotTextView.showMoreText();
        } else if (redMsgNum >= 1) {
            dotTextView.setVisibility(View.VISIBLE);
            dotTextView.setText(String.valueOf(redMsgNum));
        } else {
            dotTextView.setVisibility(View.INVISIBLE);
        }
        //添加点击事件
        helper.addOnClickListener(R.id.btn_hide);
        helper.addOnClickListener(R.id.rl_content);
    }
}

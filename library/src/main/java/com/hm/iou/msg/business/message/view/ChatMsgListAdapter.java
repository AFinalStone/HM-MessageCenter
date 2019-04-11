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
        ImageLoader.getInstance(mContext).displayImage(item.getFromHeaderImage(), ivHeader);
        //名字
        helper.setText(R.id.tv_from_nick, item.getFromNick());
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
        } else if (redMsgNum > 1) {
            dotTextView.setVisibility(View.VISIBLE);
            dotTextView.setText(String.valueOf(redMsgNum));
        } else {
            dotTextView.setVisibility(View.INVISIBLE);
        }
    }
}

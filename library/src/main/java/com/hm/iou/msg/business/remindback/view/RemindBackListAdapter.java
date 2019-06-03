package com.hm.iou.msg.business.remindback.view;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;


/**
 * Created by syl on 18/4/28.<br>
 */

public class RemindBackListAdapter extends BaseQuickAdapter<IRemindBackMsgItem, BaseViewHolder> {

    public RemindBackListAdapter(Context context) {
        super(R.layout.msgcenter_item_remind_back_msg_list_item);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, IRemindBackMsgItem item) {
        //时间
        helper.setText(R.id.tv_time, item.getITime());
        helper.setGone(R.id.tv_time, item.ifIShowTime());
        //标题
        helper.setText(R.id.tv_title, item.getITitle());
        //还款/付款时间
        helper.setText(R.id.tv_back_money_time, item.getIBackTime());
        //还款/付款金额
        helper.setText(R.id.tv_back_money, item.getIBackThingName());
        //添加点击事件
        helper.addOnClickListener(R.id.rl_content);

        if (item.isHaveRead()) {
            helper.setAlpha(R.id.iv_logo, 0.618f);
            helper.setTextColor(R.id.tv_title, mContext.getResources().getColor(R.color.uikit_text_auxiliary));
            helper.setTextColor(R.id.tv_back_money_time, mContext.getResources().getColor(R.color.uikit_text_hint));
            helper.setTextColor(R.id.tv_back_money, mContext.getResources().getColor(R.color.uikit_text_hint));
        } else {
            helper.setAlpha(R.id.iv_logo, 1.0f);
            helper.setTextColor(R.id.tv_title, mContext.getResources().getColor(R.color.uikit_text_main_content));
            helper.setTextColor(R.id.tv_back_money_time, mContext.getResources().getColor(R.color.uikit_text_auxiliary));
            helper.setTextColor(R.id.tv_back_money, mContext.getResources().getColor(R.color.uikit_text_auxiliary));
        }
    }

}

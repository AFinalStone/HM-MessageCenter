package com.hm.iou.msg.business.feedback.view;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;

/**
 * Created by hjy on 2018/5/29.
 */

public class FeedbackListAdapter extends BaseQuickAdapter<IFeedbackListItem, BaseViewHolder>{

    public FeedbackListAdapter(Context context) {
        super(R.layout.msg_item_history_feedback);
    }

    @Override
    protected void convert(BaseViewHolder helper, IFeedbackListItem item) {
        helper.setText(R.id.tv_feedback_title, item.getTitle());
        helper.setText(R.id.tv_feedback_time, item.getTime());
        helper.setText(R.id.tv_feedback_status, item.getStatusStr());
        helper.setTextColor(R.id.tv_feedback_status, item.getStatusColor());
    }
}

package com.hm.iou.msg.business.alipay.list.view;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;


/**
 * @author syl
 * @time 2019/5/20 4:22 PM
 */
public class AliPayListAdapter extends BaseQuickAdapter<IAliPayMsgItem, BaseViewHolder> {

    public AliPayListAdapter(Context context) {
        super(R.layout.msgcenter_item_alipay_msg_list_item);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, IAliPayMsgItem item) {
        //时间
        helper.setText(R.id.tv_time, item.getITime());
        helper.setGone(R.id.tv_time, item.ifIShowTime());
        //标题
        helper.setText(R.id.tv_title, item.getITitle());
        //内容
        helper.setText(R.id.tv_content, item.getIContent());
        //点击事件
        if (item.isHaveRead()) {
            helper.setAlpha(R.id.rl_content, 0.5f);
        } else {
            helper.setAlpha(R.id.rl_content, 1f);
        }
        helper.addOnClickListener(R.id.rl_content);
    }

}

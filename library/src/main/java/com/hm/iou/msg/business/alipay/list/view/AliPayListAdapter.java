package com.hm.iou.msg.business.alipay.list.view;

import android.content.Context;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;

import java.util.List;


/**
 * @author syl
 * @time 2019/5/20 4:22 PM
 */
public class AliPayListAdapter extends BaseQuickAdapter<IAliPayMsgItem, BaseViewHolder> {

    public AliPayListAdapter(Context context) {
        super(R.layout.msgcenter_item_alipay_msg_list_item);
        mContext = context;
    }

    public void removeDataByMsgId(String msgId) {
        if (TextUtils.isEmpty(msgId)) {
            return;
        }
        List<IAliPayMsgItem> list = getData();
        if (list != null) {
            int index = -1;
            for (int i = 0; i < list.size(); i++) {
                if (msgId.equals(list.get(i).getIMsgId())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                remove(index);
            }
        }
    }

    public void updateData(IAliPayMsgItem msgItem) {
        if (msgItem == null || TextUtils.isEmpty(msgItem.getIMsgId())) {
            return;
        }
        List<IAliPayMsgItem> list = getData();
        if (list != null) {
            int index = -1;
            for (int i = 0; i < list.size(); i++) {
                if (msgItem.getIMsgId().equals(list.get(i).getIMsgId())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                setData(index, msgItem);
            }
        }
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
        helper.addOnClickListener(R.id.rl_content);
        helper.addOnClickListener(R.id.btn_delete);

        if (item.isHaveRead()) {
            helper.setAlpha(R.id.iv_logo, 0.618f);
            helper.setTextColor(R.id.tv_title, mContext.getResources().getColor(R.color.uikit_text_auxiliary));
            helper.setTextColor(R.id.tv_content, mContext.getResources().getColor(R.color.uikit_text_hint));
        } else {
            helper.setAlpha(R.id.iv_logo, 1.0f);
            helper.setTextColor(R.id.tv_title, mContext.getResources().getColor(R.color.uikit_text_main_content));
            helper.setTextColor(R.id.tv_content, mContext.getResources().getColor(R.color.uikit_text_auxiliary));
        }
    }

}

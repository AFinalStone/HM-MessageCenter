package com.hm.iou.msg.business.similarity.view;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;


/**
 * Created by syl on 18/4/28.<br>
 */

public class SimilarityContractListAdapter extends BaseQuickAdapter<ISimilarityContractMsgItem, BaseViewHolder> {

    public SimilarityContractListAdapter(Context context) {
        super(R.layout.msgcenter_item_similarity_contract_list_item);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, ISimilarityContractMsgItem item) {
        helper.setText(R.id.tv_title, item.getITitle());
        helper.setText(R.id.tv_lender_name, item.getILenderName());
        helper.setText(R.id.tv_borrower_name, item.getIBorrowerName());
        helper.setText(R.id.tv_back_time, item.getIBackTime());
        helper.setText(R.id.tv_back_type, item.getIBackType());

        helper.setGone(R.id.iv_iou_tag, item.showStatusTag());
        if (item.showStatusTag()) {
            helper.setImageResource(R.id.iv_iou_tag, item.getStatusTagBg());
        }
        //点击事件
        helper.addOnClickListener(R.id.rl_content);
    }

}

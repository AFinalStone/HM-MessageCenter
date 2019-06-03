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
        helper.setText(R.id.tv_time, item.getITime());
        helper.setGone(R.id.tv_time, item.ifIShowTime());
        helper.setText(R.id.tv_title, item.getITitle());
        helper.setText(R.id.tv_lender_name, item.getILenderName());
        helper.setText(R.id.tv_borrower_name, item.getIBorrowerName());
        helper.setText(R.id.tv_back_time, item.getIBackTime());
        helper.setText(R.id.tv_back_type, item.getIBackType());

        //点击事件
        helper.addOnClickListener(R.id.rl_content);
        if (item.isHaveRead()) {
            helper.setTextColor(R.id.tv_title, mContext.getResources().getColor(R.color.uikit_text_auxiliary));
            int color = mContext.getResources().getColor(R.color.uikit_text_hint);
            helper.setTextColor(R.id.tv_lender, color);
            helper.setTextColor(R.id.tv_lender_name, color);
            helper.setTextColor(R.id.tv_borrower, color);
            helper.setTextColor(R.id.tv_borrower_name, color);
            helper.setTextColor(R.id.tv_back_time, color);
            helper.setTextColor(R.id.tv_back_type, color);
        } else {
            helper.setTextColor(R.id.tv_title, mContext.getResources().getColor(R.color.uikit_text_main_content));
            int color = mContext.getResources().getColor(R.color.uikit_text_auxiliary);
            helper.setTextColor(R.id.tv_lender, color);
            helper.setTextColor(R.id.tv_lender_name, color);
            helper.setTextColor(R.id.tv_borrower, color);
            helper.setTextColor(R.id.tv_borrower_name, color);
            helper.setTextColor(R.id.tv_back_time, color);
            helper.setTextColor(R.id.tv_back_type, color);
        }
    }

}

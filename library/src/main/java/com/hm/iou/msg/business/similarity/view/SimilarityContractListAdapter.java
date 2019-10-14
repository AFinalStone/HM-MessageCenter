package com.hm.iou.msg.business.similarity.view;

import android.content.Context;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;

import java.util.List;


/**
 * Created by syl on 18/4/28.<br>
 */

public class SimilarityContractListAdapter extends BaseQuickAdapter<ISimilarityContractMsgItem, BaseViewHolder> {

    public SimilarityContractListAdapter(Context context) {
        super(R.layout.msgcenter_item_similarity_contract_list_item);
        mContext = context;
    }

    public void removeDataByMsgId(String msgId) {
        if (TextUtils.isEmpty(msgId)) {
            return;
        }
        List<ISimilarityContractMsgItem> list = getData();
        if (list != null) {
            int index = -1;
            for (int i = 0; i < list.size(); i++) {
                if (msgId.equals(list.get(i).getIMsgId())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                int next = index + 1;
                if (next < list.size()) {
                    ISimilarityContractMsgItem currMsg = list.get(index);
                    ISimilarityContractMsgItem nextMsg = list.get(next);
                    if (currMsg.ifIShowTime()) {
                        nextMsg.setIfIShowTime(true);
                    }
                }
                remove(index);
            }
        }
    }

    public void updateData(ISimilarityContractMsgItem msgItem) {
        if (msgItem == null || TextUtils.isEmpty(msgItem.getIMsgId())) {
            return;
        }
        List<ISimilarityContractMsgItem> list = getData();
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
    protected void convert(BaseViewHolder helper, ISimilarityContractMsgItem item) {
        helper.setImageResource(R.id.iv_iou, item.getILogoResId());
        helper.setText(R.id.tv_time, item.getITime() + "  (↓)");
        helper.setGone(R.id.tv_time, item.ifIShowTime());
        helper.setText(R.id.tv_title, item.getITitle());
        helper.setText(R.id.tv_lender, item.getILender());
        helper.setText(R.id.tv_lender_name, item.getILenderName());
        helper.setText(R.id.tv_borrower, item.getIBorrower());
        helper.setText(R.id.tv_borrower_name, item.getIBorrowerName());
        helper.setText(R.id.tv_back_time, item.getIBackTime());
        helper.setText(R.id.tv_back_type, item.getIBackType());

        //点击事件
        //添加点击事件
        helper.addOnClickListener(R.id.rl_content);
        helper.addOnClickListener(R.id.btn_delete);

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

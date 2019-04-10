package com.hm.iou.msg.business.contract.view;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;


/**
 * Created by syl on 18/4/28.<br>
 */

public class ContractMsgListAdapter extends BaseQuickAdapter<IContractMsgItem, BaseViewHolder> {

    public ContractMsgListAdapter(Context context) {
        super(R.layout.msgcenter_item_contract_msg_list_item);
        mContext = context;
    }


    @Override
    protected void convert(BaseViewHolder helper, IContractMsgItem item) {
        //时间
        helper.setText(R.id.tv_time, item.getTime());
        //标题
        helper.setText(R.id.tv_title, item.getTitle());
        //内容
        helper.setText(R.id.tv_content, item.getContent());
    }
}

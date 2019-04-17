package com.hm.iou.msg.business.apply.view;

import android.content.Context;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;
import com.hm.iou.msg.dict.ApplyNewFriendStatus;
import com.hm.iou.tools.ImageLoader;


/**
 * Created by syl on 18/4/28.<br>
 */

public class ApplyNewFriendListAdapter extends BaseQuickAdapter<IApplyNewFriend, BaseViewHolder> {

    public ApplyNewFriendListAdapter(Context context) {
        super(R.layout.msgcenter_item_apply_new_friend_list);
        mContext = context;
    }

    public void removeData(String applyId) {
        if (mData != null) {
            int index = -1;
            for (int i = 0; i < mData.size(); i++) {
                if (applyId.equals(mData.get(i).getApplyId())) {
                    index = i;
                }
            }
            if (index != -1) {
                remove(index);
            }
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, IApplyNewFriend item) {
        //头像
        ImageView ivHeader = helper.getView(R.id.iv_from_header);
        String headerUrl = item.getIHeaderImg();
        ImageLoader.getInstance(mContext).displayImage(headerUrl, ivHeader);
        //昵称
        helper.setText(R.id.tv_from_nick, item.getINick());
        //备注
        helper.setText(R.id.tv_chat_content, item.getIContent());
        //状态
        int status = item.getIStatus();
        if (ApplyNewFriendStatus.HAVE_AGREE.getValue() == status
                || ApplyNewFriendStatus.HAVE_OVER.getValue() == status) {
            helper.setVisible(R.id.btn_see, false);
            helper.setVisible(R.id.tv_status, true);
            helper.setText(R.id.tv_status, ApplyNewFriendStatus.getDescByValue(status));
        } else {
            helper.setVisible(R.id.btn_see, true);
            helper.setVisible(R.id.tv_status, false);
            helper.addOnClickListener(R.id.btn_see);
        }
        helper.addOnClickListener(R.id.btn_delete);
        helper.addOnClickListener(R.id.rl_content);
        helper.addOnClickListener(R.id.btn_see);
    }
}

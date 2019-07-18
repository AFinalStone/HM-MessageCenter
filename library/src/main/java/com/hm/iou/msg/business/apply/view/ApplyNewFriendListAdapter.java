package com.hm.iou.msg.business.apply.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;
import com.hm.iou.msg.dict.ApplyNewFriendStatus;
import com.hm.iou.sharedata.model.SexEnum;
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
                    break;
                }
            }
            if (index != -1) {
                remove(index);
            }
        }
    }

    public void removeDataByFriendId(String friendId) {
        if (mData != null) {
            int index = -1;
            for (int i = 0; i < mData.size(); i++) {
                if (friendId.equals(mData.get(i).getFriendId())) {
                    index = i;
                    break;
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
        ImageLoader.getInstance(mContext).displayImage(headerUrl, ivHeader, R.mipmap.uikit_icon_header_unknow,
                R.mipmap.uikit_icon_header_unknow);
        //性别
        int sex = item.getSexType();
        ImageView ivSex = helper.getView(R.id.iv_sex);
        if (sex == SexEnum.FEMALE.getValue()) {
            ivSex.setVisibility(View.VISIBLE);
            ImageLoader.getInstance(mContext).displayImage(R.mipmap.uikit_ic_gender_woman, ivSex);
        } else if (sex == SexEnum.MALE.getValue()) {
            ivSex.setVisibility(View.VISIBLE);
            ImageLoader.getInstance(mContext).displayImage(R.mipmap.uikit_ic_gender_man, ivSex);
        } else {
            ivSex.setVisibility(View.INVISIBLE);
        }
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

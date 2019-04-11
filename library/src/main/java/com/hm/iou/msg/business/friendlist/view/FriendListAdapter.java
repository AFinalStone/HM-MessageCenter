package com.hm.iou.msg.business.friendlist.view;

import android.content.Context;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;
import com.hm.iou.tools.ImageLoader;


/**
 * Created by syl on 18/4/28.<br>
 */

public class FriendListAdapter extends BaseQuickAdapter<IFriend, BaseViewHolder> {

    public FriendListAdapter(Context context) {
        super(R.layout.msgcenter_item_friend_list_item);
        mContext = context;
    }


    @Override
    protected void convert(BaseViewHolder helper, IFriend item) {
        //头像
        ImageView ivHeader = helper.getView(R.id.iv_header);
        String headerUrl = item.getIHeaderImg();
        ImageLoader.getInstance(mContext).displayImage(headerUrl, ivHeader);
        //昵称
        helper.setText(R.id.tv_nick, item.getINick());
    }
}

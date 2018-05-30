package com.hm.iou.msg.business.message.view;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;
import com.hm.iou.tools.ImageLoader;

import java.util.List;

import static com.hm.iou.msg.business.message.view.IMsgItem.TYPE_AD_OR_SPORT;
import static com.hm.iou.msg.business.message.view.IMsgItem.TYPE_COMMUNIQUE;

/**
 * Created by syl on 18/4/28.<br>
 */

public class MsgListAdapter extends BaseMultiItemQuickAdapter<IMsgItem, BaseViewHolder> {

    public MsgListAdapter() {
        super(null);
        addItemType(TYPE_AD_OR_SPORT, R.layout.msg_item_msg_center_ad_or_sport);
        addItemType(TYPE_COMMUNIQUE, R.layout.msg_item_msg_center_communique);
    }

    @Override
    protected void convert(BaseViewHolder helper, IMsgItem item) {
        if (helper.getItemViewType() == TYPE_COMMUNIQUE) {
            helper.setText(R.id.tv_content, item.getContent());
            helper.setText(R.id.tv_subContent, item.getSubContent());
        }
        helper.setImageResource(R.id.iv_icon, item.getIcon());
        helper.setText(R.id.tv_title, item.getTitle());
        ImageLoader.getInstance(mContext).displayImage(item.getMsgImage(), (ImageView) helper.getView(R.id.imageView),
                R.drawable.msg_bg_feedback_item_pic, R.drawable.msg_bg_feedback_item_pic);
    }

}

package com.hm.iou.msg.business.message.view;

import android.text.TextUtils;
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

        helper.setImageResource(R.id.iv_icon, item.getMsgIcon());
        helper.setText(R.id.tv_title, item.getMsgTitle());
        ImageLoader.getInstance(mContext).displayImage(item.getMsgImage(), (ImageView) helper.getView(R.id.imageView),
                R.drawable.uikit_bg_pic_loading_place, R.drawable.uikit_bg_pic_loading_error);

        if (helper.getItemViewType() == TYPE_AD_OR_SPORT) {
            if (TextUtils.isEmpty(item.getMsgTime())) {
                helper.setGone(R.id.tv_time, true);
                helper.setText(R.id.tv_time, item.getMsgTime());
            } else {
                helper.setGone(R.id.tv_time, false);
            }
            if (item.getMsgReadState()) {
                helper.setVisible(R.id.iv_noRead, false);
            } else {
                helper.setVisible(R.id.iv_noRead, true);
            }
            return;
        }
        if (helper.getItemViewType() == TYPE_COMMUNIQUE) {
            helper.setText(R.id.tv_intro, item.getCommuniqueIntro());
        }
    }

}

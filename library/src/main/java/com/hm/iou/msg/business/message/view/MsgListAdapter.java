package com.hm.iou.msg.business.message.view;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;
import com.hm.iou.tools.ImageLoader;

import static com.hm.iou.msg.business.message.view.IMsgItem.TYPE_AD_OR_SPORT;
import static com.hm.iou.msg.business.message.view.IMsgItem.TYPE_COMMUNIQUE;

/**
 * Created by syl on 18/4/28.<br>
 */

public class MsgListAdapter extends BaseMultiItemQuickAdapter<IMsgItem, BaseViewHolder> {

    public MsgListAdapter() {
        super(null);
        addItemType(TYPE_AD_OR_SPORT, R.layout.msgcenter_item_ad_or_sport);
        addItemType(TYPE_COMMUNIQUE, R.layout.msgcenter_item_notice);
    }

    @Override
    protected void convert(BaseViewHolder helper, IMsgItem item) {
        helper.setText(R.id.tv_title, item.getMsgTitle());

        if (helper.getItemViewType() == TYPE_AD_OR_SPORT) {
            String msgImage = item.getMsgImage();
            if (TextUtils.isEmpty(msgImage)) {
                ImageLoader.getInstance(mContext).displayImage(R.mipmap.msgcenter_icon_load_image_error_default, (ImageView) helper.getView(R.id.imageView));
            } else {
                ImageLoader.getInstance(mContext).displayImage(item.getMsgImage(), (ImageView) helper.getView(R.id.imageView),
                        R.drawable.uikit_bg_pic_loading_place, R.mipmap.msgcenter_icon_load_image_error_default);
            }

            helper.setImageResource(R.id.iv_icon, item.getMsgIcon());
            helper.addOnClickListener(R.id.ll_adOrSport);

            String time = item.getMsgTime();
            helper.setText(R.id.tv_time, time);
            if (item.getMsgReadState()) {
                helper.setVisible(R.id.iv_noRead, false);
            } else {
                helper.setVisible(R.id.iv_noRead, true);
            }
            return;
        }
        if (helper.getItemViewType() == TYPE_COMMUNIQUE) {
            String time = item.getMsgTime();
            helper.setText(R.id.tv_time, time);
            helper.setText(R.id.tv_intro, item.getNotice());
        }
    }

}

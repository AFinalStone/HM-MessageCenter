package com.hm.iou.msg.business.hmmsg.view;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;
import com.hm.iou.tools.ImageLoader;


/**
 * Created by syl on 18/4/28.<br>
 */

public class HmMsgListAdapter extends BaseMultiItemQuickAdapter<IHmMsgItem, BaseViewHolder> {

    public HmMsgListAdapter(Context context) {
        super(null);
        mContext = context;
        addItemType(IHmMsgItem.TYPE_ADVERTISEMENT, R.layout.msgcenter_item_hm_msg_list_ad_or_sport);
        addItemType(IHmMsgItem.TYPE_COMMUNIQUE, R.layout.msgcenter_item_hm_msg_list_notice);
    }

    @Override
    protected void convert(BaseViewHolder helper, IHmMsgItem item) {
        helper.setText(R.id.tv_title, item.getMsgTitle());

        if (helper.getItemViewType() == IHmMsgItem.TYPE_ADVERTISEMENT) {
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
            return;
        }
        if (helper.getItemViewType() == IHmMsgItem.TYPE_COMMUNIQUE) {
            String time = item.getMsgTime();
            helper.setText(R.id.tv_time, time);
            helper.setText(R.id.tv_intro, item.getNotice());
        }
    }

}

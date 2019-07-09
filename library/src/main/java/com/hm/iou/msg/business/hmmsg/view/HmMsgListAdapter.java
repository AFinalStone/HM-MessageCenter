package com.hm.iou.msg.business.hmmsg.view;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;
import com.hm.iou.tools.ImageLoader;

import java.util.List;


/**
 * Created by syl on 18/4/28.<br>
 */

public class HmMsgListAdapter extends BaseMultiItemQuickAdapter<IHmMsgItem, BaseViewHolder> {

    public HmMsgListAdapter(Context context) {
        super(null);
        mContext = context;
        addItemType(IHmMsgItem.TYPE_ADVERTISEMENT_NEWS_SPORT, R.layout.msgcenter_item_hm_msg_list_ad_or_sport);
        addItemType(IHmMsgItem.TYPE_COMMUNIQUE, R.layout.msgcenter_item_hm_msg_list_notice);
    }

    public void removeDataByMsgId(String msgId) {
        if (TextUtils.isEmpty(msgId)) {
            return;
        }
        List<IHmMsgItem> list = getData();
        if (list != null) {
            int index = -1;
            for (int i = 0; i < list.size(); i++) {
                if (msgId.equals(list.get(i).getIMsgId())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                remove(index);
            }
        }
    }

    public void updateData(IHmMsgItem msgItem) {
        if (msgItem == null || TextUtils.isEmpty(msgItem.getIMsgId())) {
            return;
        }
        List<IHmMsgItem> list = getData();
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
    protected void convert(BaseViewHolder helper, IHmMsgItem item) {
        helper.setText(R.id.tv_title, item.getMsgTitle());
        String time = item.getMsgTime();
        helper.setText(R.id.tv_time, time);
        if (helper.getItemViewType() == IHmMsgItem.TYPE_ADVERTISEMENT_NEWS_SPORT) {
            String msgImage = item.getMsgImage();
            if (TextUtils.isEmpty(msgImage)) {
                ImageLoader.getInstance(mContext).displayImage(R.mipmap.msgcenter_icon_load_image_error_default, (ImageView) helper.getView(R.id.imageView));
            } else {
                ImageLoader.getInstance(mContext).displayImage(item.getMsgImage(), (ImageView) helper.getView(R.id.imageView),
                        R.drawable.uikit_bg_pic_loading_place, R.mipmap.msgcenter_icon_load_image_error_default);
            }
            helper.setImageResource(R.id.iv_icon, item.getMsgIcon());
            helper.addOnClickListener(R.id.ll_content);
            if (item.isHaveRead()) {
                helper.setAlpha(R.id.iv_icon, 0.618f);
                helper.setAlpha(R.id.imageView, 0.618f);
                helper.setTextColor(R.id.tv_title, mContext.getResources().getColor(R.color.uikit_text_auxiliary));
                helper.setTextColor(R.id.tv_see_detail, mContext.getResources().getColor(R.color.uikit_text_hint));
            } else {
                helper.setAlpha(R.id.iv_icon, 1.0f);
                helper.setAlpha(R.id.imageView, 1.0f);
                helper.setTextColor(R.id.tv_title, mContext.getResources().getColor(R.color.uikit_text_main_content));
                helper.setTextColor(R.id.tv_see_detail, mContext.getResources().getColor(R.color.uikit_text_auxiliary));
            }
            return;
        }
        if (helper.getItemViewType() == IHmMsgItem.TYPE_COMMUNIQUE) {
            helper.setText(R.id.tv_intro, item.getNotice());
            if (item.isHaveRead()) {
                helper.setAlpha(R.id.iv_icon, 0.618f);
                helper.setAlpha(R.id.imageView, 0.618f);
                helper.setTextColor(R.id.tv_title, mContext.getResources().getColor(R.color.uikit_text_auxiliary));
                helper.setTextColor(R.id.tv_intro, mContext.getResources().getColor(R.color.uikit_text_hint));
            } else {
                helper.setAlpha(R.id.iv_icon, 1.0f);
                helper.setAlpha(R.id.imageView, 1.0f);
                helper.setTextColor(R.id.tv_title, mContext.getResources().getColor(R.color.uikit_text_main_content));
                helper.setTextColor(R.id.tv_intro, mContext.getResources().getColor(R.color.uikit_text_auxiliary));
            }
            return;
        }
    }

}

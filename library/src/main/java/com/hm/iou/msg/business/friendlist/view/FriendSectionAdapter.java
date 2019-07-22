package com.hm.iou.msg.business.friendlist.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.msg.R;
import com.hm.iou.tools.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendSectionAdapter extends BaseSectionQuickAdapter<FriendSection, BaseViewHolder> {

    private Map<String, Integer> mHeaderIndex = new HashMap<>();

    public FriendSectionAdapter(Context context) {
        super(R.layout.msgcenter_item_friend_list_item, R.layout.msgcenter_item_friend_list_section_header, null);
    }

    @Override
    public void setNewData(@Nullable List<FriendSection> data) {
        super.setNewData(data);
        refreshHeaderIndex();
    }

    private void refreshHeaderIndex() {
        mHeaderIndex.clear();
        List<FriendSection> data = getData();
        if (data != null && !data.isEmpty()) {
            int index = 0;
            for (FriendSection section : data) {
                if (section.isHeader) {
                    mHeaderIndex.put(section.header, index);
                    index += section.getTotalCount();
                }
            }
        }
    }

    public Integer getHeaderIndex(String letter) {
        return mHeaderIndex.get(letter);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, FriendSection item) {
        helper.setText(R.id.tv_friend_section_header, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, FriendSection data) {
        //头像
        IFriend item = data.t;
        ImageView ivHeader = helper.getView(R.id.iv_header);
        String headerUrl = item.getIHeaderImg();
        ImageLoader.getInstance(mContext).displayImage(headerUrl, ivHeader, R.mipmap.uikit_icon_header_unknow,
                R.mipmap.uikit_icon_header_unknow);
        //昵称
        helper.setText(R.id.tv_nick, item.getINick());
        //如果是分组最后一条数据时，去掉分隔线
        helper.setVisible(R.id.view_friend_divider, !item.isGroupLastItem());

        helper.addOnClickListener(R.id.ll_friend_item);
    }

}

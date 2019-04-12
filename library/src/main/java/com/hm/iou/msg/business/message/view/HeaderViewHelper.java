package com.hm.iou.msg.business.message.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hm.iou.base.utils.ImageLoadUtil;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.bean.MsgListHeaderBean;
import com.hm.iou.msg.business.message.MsgCenterContract;
import com.hm.iou.router.Router;
import com.hm.iou.tools.ImageLoader;
import com.hm.iou.uikit.HMDotTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author syl
 * @time 2019/4/9 1:27 PM
 */

public class HeaderViewHelper {

    @BindView(R2.id.ll_header)
    LinearLayout mLlHeader;
    @BindView(R2.id.dot_module_new_friend_no_read_num)
    HMDotTextView mDotModuleNewFriendNoReadNum;
    private View mRootView;
    private Context mContent;
    private MsgCenterContract.Presenter mPresenter;

    public HeaderViewHelper(@NonNull View parentView, @NonNull MsgCenterContract.Presenter presenter) {
        mContent = parentView.getContext();
        mRootView = LayoutInflater.from(mContent).inflate(R.layout.msgcenter_item_msg_list_header, null, false);
        mPresenter = presenter;
        ButterKnife.bind(this, mRootView);
    }

    public View getRootView() {
        return mRootView;
    }

    private void addModule(MsgListHeaderBean moduleBean) {
        if (moduleBean == null) {
            return;
        }
        View viewModule = LayoutInflater.from(mContent).inflate(R.layout.msgcenter_item_msg_list_header_item, mLlHeader, false);
        ImageView ivModule = viewModule.findViewById(R.id.iv_module);
        TextView tvModule = viewModule.findViewById(R.id.tv_module);
        HMDotTextView dotTextView = viewModule.findViewById(R.id.dot_module_red_msg_num);
        String imageUrl = moduleBean.getImage();
        imageUrl = ImageLoadUtil.getImageRealUrl(mContent, imageUrl);
        ImageLoader.getInstance(mContent).displayImage(imageUrl, ivModule);
        tvModule.setText(moduleBean.getName());
        int redMsgNum = moduleBean.getRedMsgNum();
        if (redMsgNum > 99) {
            dotTextView.setVisibility(View.VISIBLE);
            dotTextView.showMoreText();
        } else if (redMsgNum > 1) {
            dotTextView.setVisibility(View.VISIBLE);
            dotTextView.setText(String.valueOf(redMsgNum));
        } else {
            dotTextView.setVisibility(View.INVISIBLE);
        }
        viewModule.setTag(moduleBean);
        viewModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MsgListHeaderBean childModule = (MsgListHeaderBean) v.getTag();
                if (childModule != null && !TextUtils.isEmpty(childModule.getUrl())) {
                    Router.getInstance().buildWithUrl(childModule.getUrl())
                            .navigation(mContent);
                }
            }
        });
        mLlHeader.addView(viewModule);
    }

    public void addModule(List<MsgListHeaderBean> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            addModule(list.get(i));
        }
    }

    public void updateModuleItem(MsgListHeaderBean moduleBean) {
        if (moduleBean == null) {
            return;
        }
        int count = mLlHeader.getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = mLlHeader.getChildAt(i);
            MsgListHeaderBean childModule = (MsgListHeaderBean) childView.getTag();
            if (childModule != null && childModule.getModuleId().equals(moduleBean.getModuleId())) {
                ImageView ivModule = childView.findViewById(R.id.iv_module);
                TextView tvModule = childView.findViewById(R.id.tv_module);
                HMDotTextView dotTextView = childView.findViewById(R.id.dot_module_red_msg_num);
                String imageUrl = moduleBean.getImage();
                imageUrl = ImageLoadUtil.getImageRealUrl(mContent, imageUrl);
                ImageLoader.getInstance(mContent).displayImage(imageUrl, ivModule);
                tvModule.setText(moduleBean.getName());
                int redMsgNum = moduleBean.getRedMsgNum();
                if (redMsgNum > 99) {
                    dotTextView.setVisibility(View.VISIBLE);
                    dotTextView.showMoreText();
                } else if (redMsgNum > 1) {
                    dotTextView.setVisibility(View.VISIBLE);
                    dotTextView.setText(String.valueOf(redMsgNum));
                } else {
                    dotTextView.setVisibility(View.INVISIBLE);
                }
                break;
            }
        }
    }

    public void clearHeaderModules() {
        mLlHeader.removeAllViews();
    }

    @OnClick(R2.id.ll_module_new_friend)
    public void onClick() {
        NavigationHelper.toApplyNewFriendList(mContent);
    }
}

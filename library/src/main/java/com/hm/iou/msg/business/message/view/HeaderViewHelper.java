package com.hm.iou.msg.business.message.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hm.iou.base.adver.AdBean;
import com.hm.iou.base.utils.ImageLoadUtil;
import com.hm.iou.base.utils.RouterUtil;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.bean.MsgListHeaderBean;
import com.hm.iou.msg.business.message.MsgCenterContract;
import com.hm.iou.msg.dict.ModuleType;
import com.hm.iou.router.Router;
import com.hm.iou.tools.ImageLoader;
import com.hm.iou.tools.NetStateUtil;
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

    //不显示任何提示内容
    private static final int BANNER_TYPE_SHOW_NOTHING = 0;
    //横条类型：广告显示和隐藏
    private static final int BANNER_TYPE_ADVERTISEMENT = 1;

    @BindView(R2.id.ll_header)
    LinearLayout mLlHeader;
    @BindView(R2.id.tv_header_tips_no_net)
    TextView mTvHeaderTipNoNet;//无网络
    @BindView(R2.id.ll_header_tips_advertisement)
    LinearLayout mLlHeaderTipAdvertisement;//广告
    @BindView(R2.id.iv_advertisement)
    ImageView mIvAdvertisement;//广告图片
    private View mRootView;
    private Context mContent;
    private MsgCenterContract.Presenter mPresenter;
    private NetworkBroadcastReceiver mReceiver;
    private int mTopBannerType = BANNER_TYPE_SHOW_NOTHING;

    private boolean mIsHideAdvertisement;//是否显示广告

    public HeaderViewHelper(@NonNull View parentView, @NonNull MsgCenterContract.Presenter presenter) {
        mContent = parentView.getContext();
        mRootView = LayoutInflater.from(mContent).inflate(R.layout.msgcenter_item_msg_list_header, null, false);
        mPresenter = presenter;
        ButterKnife.bind(this, mRootView);

        mReceiver = new NetworkBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContent.registerReceiver(mReceiver, filter);
        checkNetwork();
    }

    @OnClick({R2.id.iv_advertisement, R2.id.iv_close_advertisement})
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.iv_advertisement == id) {
            String jumpUrl = (String) mIvAdvertisement.getTag();
            if (!TextUtils.isEmpty(jumpUrl)) {
                RouterUtil.clickMenuLink(mContent, jumpUrl);
            }
        } else if (R.id.iv_close_advertisement == id) {
            mLlHeaderTipAdvertisement.setVisibility(View.GONE);
            mIsHideAdvertisement = true;
        }
    }

    public View getRootView() {
        return mRootView;
    }

    private void addModule(MsgListHeaderBean moduleBean) {
        if (moduleBean == null) {
            return;
        }
        View viewModule = LayoutInflater.from(mContent).inflate(R.layout.msgcenter_item_msg_list_header_item, mLlHeader, false);
        View viewTopDivider = viewModule.findViewById(R.id.view_top_divider);
        ImageView ivModule = viewModule.findViewById(R.id.iv_module);
        TextView tvModule = viewModule.findViewById(R.id.tv_module);
        HMDotTextView dotTextView = viewModule.findViewById(R.id.dot_module_red_msg_num);

        //填充数据
        if (ModuleType.NEW_APPLY_FRIEND.getTypeId().equals(moduleBean.getModuleId())) {
            viewTopDivider.setVisibility(View.VISIBLE);
        } else {
            viewTopDivider.setVisibility(View.GONE);
        }
        String imageUrl = moduleBean.getImage();
        imageUrl = ImageLoadUtil.getImageRealUrl(mContent, imageUrl);
        ImageLoader.getInstance(mContent).displayImage(imageUrl, ivModule);
        tvModule.setText(moduleBean.getName());
        int redMsgNum = moduleBean.getRedMsgNum();
        if (redMsgNum > 99) {
            dotTextView.setVisibility(View.VISIBLE);
            dotTextView.showMoreText();
        } else if (redMsgNum >= 1) {
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
                } else if (redMsgNum >= 1) {
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

    /**
     * 显示广告
     */
    public void showAdvertisement(AdBean adBean) {
        if (adBean == null) {
            mTopBannerType = BANNER_TYPE_SHOW_NOTHING;
            mLlHeaderTipAdvertisement.setVisibility(View.GONE);
            return;
        }
        if (!mIsHideAdvertisement) {
            mTopBannerType = BANNER_TYPE_ADVERTISEMENT;
            mLlHeaderTipAdvertisement.setVisibility(View.VISIBLE);
            ImageLoader.getInstance(mContent)
                    .displayImage(adBean.getUrl(), mIvAdvertisement, R.drawable.uikit_bg_pic_loading_place, R.drawable.uikit_bg_pic_loading_error);
            mIvAdvertisement.setTag(adBean.getLinkUrl());
        }
    }

    /**
     * 检测网络
     */
    private void checkNetwork() {
        boolean isConnected = NetStateUtil.isNetworkConnected(mContent);
        if (!isConnected) {
            mTvHeaderTipNoNet.setVisibility(View.VISIBLE);
            mLlHeaderTipAdvertisement.setVisibility(View.GONE);
        } else {
            mTvHeaderTipNoNet.setVisibility(View.GONE);
            if (mTopBannerType == BANNER_TYPE_ADVERTISEMENT && !mIsHideAdvertisement) {
                mLlHeaderTipAdvertisement.setVisibility(View.VISIBLE);
            }
        }
    }

    class NetworkBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            checkNetwork();
        }
    }
}

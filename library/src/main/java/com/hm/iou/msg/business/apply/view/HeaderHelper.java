package com.hm.iou.msg.business.apply.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.router.Router;
import com.hm.iou.tools.ImageLoader;
import com.hm.iou.uikit.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by syl on 2019/7/26.
 */

public class HeaderHelper {
    @BindView(R2.id.iv_header)
    CircleImageView mIvHeader;
    @BindView(R2.id.iv_sex)
    ImageView mIvSex;
    @BindView(R2.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R2.id.tv_show_id)
    TextView mTvShowId;
    @BindView(R2.id.iv_qr_code)
    ImageView mIvQrCode;

    private Context mContext;

    public HeaderHelper(Context mContext, View view) {
        this.mContext = mContext;
        ButterKnife.bind(this, view);
    }

    @OnClick({R2.id.iv_qr_code, R2.id.ll_search, R2.id.ll_mobile_contract, R2.id.ll_sweep_qr_code, R2.id.ll_add_my_self})
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.ll_search == id) {
            NavigationHelper.toAddNewFriend(mContext);
        } else if (R.id.ll_sweep_qr_code == id) {
            Router.getInstance()
                    .buildWithUrl("hmiou://m.54jietiao.com/qrcode/index")
                    .withString("show_type", "show_scan_code")
                    .navigation(mContext);
        } else if (R.id.ll_add_my_self == id) {
            NavigationHelper.toAddMySelf(mContext);
        } else if (R.id.iv_qr_code == id) {
            NavigationHelper.toMyCardPage(mContext);
        }
    }

    public void showHeaderData(String headerUrl, String nickName, String showId) {
        ImageLoader.getInstance(mContext).displayImage(headerUrl, mIvHeader, R.drawable.uikit_bg_pic_loading_place, R.mipmap.uikit_icon_header_unknow);
        mTvNickname.setText(nickName);
        mTvShowId.setText("ID：" + showId);
    }

    public void showSex(int sexImageResId) {
        mIvSex.setImageResource(sexImageResId);
    }


    public void showQRCodeImage(Bitmap bitmap) {
        mIvQrCode.setImageBitmap(bitmap);
    }
}

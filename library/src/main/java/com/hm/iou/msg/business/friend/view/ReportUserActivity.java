package com.hm.iou.msg.business.friend.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.base.BaseActivity;
import com.hm.iou.base.ImageGalleryActivity;
import com.hm.iou.base.photo.CompressPictureUtil;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.business.friend.ReportUserContract;
import com.hm.iou.msg.business.friend.presenter.ReportUserPresenter;
import com.hm.iou.router.Router;
import com.hm.iou.tools.ImageLoader;
import com.hm.iou.uikit.HMLoadingView;
import com.hm.iou.uikit.HMTopBarView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Created by hjy on 2019/4/11.
 * 好友举报页面
 */

public class ReportUserActivity extends BaseActivity<ReportUserPresenter> implements ReportUserContract.View {

    private static final int REQ_OPEN_SELECT_PIC = 10;
    private static final int REQ_IMAGE_GALLERY = 11;

    public static final String EXTRA_KEY_FRIEND_ID = "friend_id";

    @BindView(R2.id.topbar)
    HMTopBarView mTopBar;
    @BindView(R2.id.rv_reason_list)
    RecyclerView mRvReasonList;
    @BindView(R2.id.et_mContent)
    EditText etContent;
    @BindView(R2.id.tv_number)
    TextView tvNumber;
    @BindView(R2.id.iv_report_img)
    ImageView mIvReportImg;
    @BindView(R2.id.hmLoadingView)
    HMLoadingView mHmLoadingView;
    @BindView(R2.id.sv_report_content)
    View mLayoutContent;

    private ReasonAdapter mAdapter;
    private IReasonItem mCurrentSelectItem;

    private String mStrFeedbackContent = "";
    private String mFriendId;

    @Override
    protected int getLayoutId() {
        return R.layout.msgcenter_activity_report_user;
    }

    @Override
    protected void initEventAndData(Bundle savedInstanceState) {
        mFriendId = getIntent().getStringExtra(EXTRA_KEY_FRIEND_ID);
        if (savedInstanceState != null) {
            mFriendId = savedInstanceState.getString(EXTRA_KEY_FRIEND_ID);
        }

        mTopBar.setRightText("提交");
        mTopBar.setOnMenuClickListener(new HMTopBarView.OnTopBarMenuClickListener() {
            @Override
            public void onClickTextMenu() {
                if (mCurrentSelectItem == null) {
                    toastMessage("请选择举报原因");
                    return;
                }
                mPresenter.reportUser(mFriendId, mCurrentSelectItem.getReasonId(), (String) mIvReportImg.getTag(), mStrFeedbackContent);
            }

            @Override
            public void onClickImageMenu() {

            }
        });

        RxTextView.textChanges(etContent).subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                mStrFeedbackContent = String.valueOf(charSequence);
                tvNumber.setText(String.format("%d/50", mStrFeedbackContent.length()));
            }
        });

        mAdapter = new ReasonAdapter();
        mRvReasonList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter.bindToRecyclerView(mRvReasonList);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mAdapter.selectItem(position);
                mCurrentSelectItem = (IReasonItem) adapter.getItem(position);
            }
        });

        mPresenter.getReportList();
    }

    @Override
    protected ReportUserPresenter initPresenter() {
        return new ReportUserPresenter(this, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_KEY_FRIEND_ID, mFriendId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_OPEN_SELECT_PIC) {
            if (resultCode == RESULT_OK) {
                List<String> pathList = data.getStringArrayListExtra("extra_result_selection_path");
                if (pathList != null && !pathList.isEmpty()) {
                    for (String path : pathList) {
                        compressPic(path);
                    }
                }
            }
            return;
        }
        if (requestCode == REQ_IMAGE_GALLERY) {
            if (resultCode == RESULT_OK) {
                List<String> delList = data.getStringArrayListExtra(ImageGalleryActivity.EXTRA_KEY_DELETE_URLS);
                if (delList == null || delList.isEmpty())
                    return;
                mIvReportImg.setImageResource(R.mipmap.uikit_ic_circle_add);
                mIvReportImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mIvReportImg.setTag(null);
            }
            return;
        }
    }

    @OnClick({R2.id.iv_report_img})
    public void onClick(View view) {
        if (view.getId() == R.id.iv_report_img) {
            final String data = (String) view.getTag();
            if (TextUtils.isEmpty(data)) {
                Router.getInstance()
                        .buildWithUrl("hmiou://m.54jietiao.com/select_pic/index")
                        .withString("enable_select_max_num", "1")
                        .navigation(mContext, REQ_OPEN_SELECT_PIC);
            } else {
                String[] urls = new String[]{data};
                Intent intent = new Intent(ReportUserActivity.this, ImageGalleryActivity.class);
                intent.putExtra(ImageGalleryActivity.EXTRA_KEY_IMAGES, urls);
                intent.putExtra(ImageGalleryActivity.EXTRA_KEY_INDEX, 0);
                intent.putExtra(ImageGalleryActivity.EXTRA_KEY_SHOW_DELETE, 1);
                startActivityForResult(intent, REQ_IMAGE_GALLERY);
            }
        }
    }


    @Override
    public void showInitLoadingView() {
        mHmLoadingView.setVisibility(View.VISIBLE);
        mHmLoadingView.showDataLoading();
    }

    @Override
    public void hideInitLoadingView() {
        mHmLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void showInitLoadingFailed(String msg) {
        mHmLoadingView.setVisibility(View.VISIBLE);
        mHmLoadingView.showDataFail(msg, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getReportList();
            }
        });
    }

    @Override
    public void showData(List<IReasonItem> list) {
        mLayoutContent.setVisibility(View.VISIBLE);
        mAdapter.setNewData(list);
    }

    private void compressPic(final String fileUrl) {
        if (TextUtils.isEmpty(fileUrl)) {
            return;
        }
        CompressPictureUtil.compressPic(this, fileUrl, new CompressPictureUtil.OnCompressListener() {
            public void onCompressPicSuccess(File file) {
                String data = "file://" + file.getAbsolutePath();
                mIvReportImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImageLoader.getInstance(mContext).displayImage(data, mIvReportImg,
                        R.drawable.uikit_bg_pic_loading_place, R.drawable.uikit_bg_pic_loading_error);
                mIvReportImg.setTag(data);
            }
        });
    }

    public interface IReasonItem {

        String getTitle();

        int getReasonId();

    }

    public static class ReasonAdapter extends BaseQuickAdapter<IReasonItem, BaseViewHolder> {

        private int mCurrentCheckPosition = -1;

        public ReasonAdapter() {
            super(R.layout.msgcenter_item_report_user);
        }

        @Override
        protected void convert(BaseViewHolder helper, IReasonItem item) {
            helper.setText(R.id.tv_item_title, item.getTitle());
            if (mCurrentCheckPosition == helper.getLayoutPosition()) {
                helper.setImageResource(R.id.checkBox, R.mipmap.uikit_icon_check_black);
            } else {
                helper.setImageResource(R.id.checkBox, R.mipmap.uikit_icon_check_default);
            }
        }

        public void selectItem(int position) {
            mCurrentCheckPosition = position;
            notifyDataSetChanged();
        }
    }
}

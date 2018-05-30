package com.hm.iou.msg.business.feedback.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.base.BaseActivity;
import com.hm.iou.base.file.FileApi;
import com.hm.iou.base.photo.CompressPictureUtil;
import com.hm.iou.base.photo.PhotoUtil;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.business.feedback.FeedbackContract;
import com.hm.iou.msg.business.feedback.presenter.FeedbackPresenter;
import com.hm.iou.tools.ImageLoader;
import com.hm.iou.uikit.HMTopBarView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by hjy on 2018/5/29.
 */

public class FeedbackActivity  extends BaseActivity<FeedbackPresenter> implements FeedbackContract.View  {

    private static final int REQ_CODE_CAMERA = 101;
    private static final int REQ_CODE_PHOTO = 102;

    @BindView(R2.id.topbar)
    HMTopBarView mTopBarView;
    @BindView(R2.id.et_mContent)
    EditText mEtContent;
    @BindView(R2.id.tv_number)
    TextView mTvNumber;
    @BindView(R2.id.tv_feedback_top_type)
    TextView mTvTopType;
    @BindView(R2.id.recyclerView_feedback_img)
    RecyclerView mRecyclerView;
    @BindView(R2.id.tv_type)
    TextView mTvType;

    private FeedbackImageAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.msg_activity_feedback;
    }

    @Override
    protected FeedbackPresenter initPresenter() {
        return new FeedbackPresenter(this, this);
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        mTopBarView.setRightText("发送");
        mTopBarView.setOnMenuClickListener(new HMTopBarView.OnTopBarMenuClickListener() {
            @Override
            public void onClickTextMenu() {

            }

            @Override
            public void onClickImageMenu() {
            }
        });

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new FeedbackImageAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.iv_feedback_image) {
                    String url = (String) view.getTag();
                    if (TextUtils.isEmpty(url)) {
                        PhotoUtil.showSelectDialog(FeedbackActivity.this, REQ_CODE_CAMERA, REQ_CODE_PHOTO);
                    } else {

                    }
                } else {
                    String url = (String) view.getTag();
                    if (!TextUtils.isEmpty(url)) {
                        mAdapter.removeData(position);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_CAMERA) {
            if (resultCode == RESULT_OK) {
                String path = PhotoUtil.getCameraPhotoPath();
                compressPic(path);
            }
        } else if (requestCode == REQ_CODE_PHOTO) {
            if (resultCode == RESULT_OK) {
                String path = PhotoUtil.getPath(this, data.getData());
                compressPic(path);
            }
        }
    }

    private void compressPic(String fileUrl) {
        CompressPictureUtil.compressPic(this, fileUrl, new CompressPictureUtil.OnCompressListener() {
            @Override
            public void onCompressPicSuccess(File file) {
                Logger.d("图片压缩成功....");
                mAdapter.addImage("file://" + file.getAbsolutePath());
            }
        });
    }

    class FeedbackImageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        static final int MAX_PIC_COUNT = 3;

        public FeedbackImageAdapter() {
            super(R.layout.msg_item_feedback_image);
            List<String> list = new ArrayList<>();
            list.add("");   //url为空时展示"+"图片
            setNewData(list);
        }

        public void addImage(String url) {
            if (mData.size() >= MAX_PIC_COUNT) {
                remove(MAX_PIC_COUNT - 1);
                addData(url);
            } else {
                addData(mData.size() - 1, url);
            }
        }

        public void removeData(int pos) {
            Logger.d("removeData: " + pos);
            remove(pos);
            int size = mData.size();
            if (size == 0) {
                addData(0, "");
            } else {
                String lastUrl = mData.get(size - 1);
                if (!TextUtils.isEmpty(lastUrl)) {
                    addData(size, "");
                }
            }
        }

        @Override
        protected void convert(final BaseViewHolder helper, String url) {
            helper.setTag(R.id.iv_feedback_image, url);
            helper.setTag(R.id.iv_delete, url);
            if (TextUtils.isEmpty(url)) {
                helper.setVisible(R.id.iv_delete, false);
                helper.setImageResource(R.id.iv_feedback_image, R.mipmap.msg_ic_add_photo);
                helper.addOnClickListener(R.id.iv_feedback_image);
            } else {
                helper.setVisible(R.id.iv_delete, true);
                int resId = R.drawable.msg_bg_feedback_item_pic;
                ImageLoader.getInstance(FeedbackActivity.this)
                        .displayImage(url, (ImageView) helper.getView(R.id.iv_feedback_image), resId, resId);
                helper.addOnClickListener(R.id.iv_delete);
            }
        }
    }

}
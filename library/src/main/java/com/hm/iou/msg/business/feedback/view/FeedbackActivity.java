package com.hm.iou.msg.business.feedback.view;

import android.content.DialogInterface;
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
import com.hm.iou.base.photo.CompressPictureUtil;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.business.feedback.FeedbackContract;
import com.hm.iou.msg.business.feedback.presenter.FeedbackPresenter;
import com.hm.iou.msg.dict.FeedbackKind;
import com.hm.iou.router.Router;
import com.hm.iou.tools.ImageLoader;
import com.hm.iou.uikit.HMTopBarView;
import com.hm.iou.uikit.dialog.IOSActionSheetItem;
import com.hm.iou.uikit.dialog.IOSActionSheetTitleDialog;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hjy on 2018/5/29.
 */

public class FeedbackActivity extends BaseActivity<FeedbackPresenter> implements FeedbackContract.View {

    public static final String EXTRA_KEY_TYPE = "type";

    private static final int REQ_OPEN_SELECT_PIC = 100;


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

    private FeedbackKind[] mFeedbackKindArr = new FeedbackKind[]{
            FeedbackKind.FlashWrong,
            FeedbackKind.DataWrong,
            FeedbackKind.NoCode,
            FeedbackKind.IouFail,
            FeedbackKind.Else
    };

    private String mFeedbackType;

    @Override
    protected int getLayoutId() {
        return R.layout.msgcenter_activity_feedback;
    }

    @Override
    protected FeedbackPresenter initPresenter() {
        return new FeedbackPresenter(this, this);
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        mFeedbackType = getIntent().getStringExtra(EXTRA_KEY_TYPE);
        if (bundle != null) {
            mFeedbackType = bundle.getString(EXTRA_KEY_TYPE);
        }

        mTopBarView.setRightText("发送");
        mTopBarView.getStatusBarPlaceHolder().setVisibility(View.GONE);
        mTopBarView.setOnMenuClickListener(new HMTopBarView.OnTopBarMenuClickListener() {
            @Override
            public void onClickTextMenu() {
                mPresenter.sendFeedback(mEtContent.getText().toString(), mAdapter.getData());
            }

            @Override
            public void onClickImageMenu() {
            }
        });

        RxTextView.textChanges(mEtContent).subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                mTvNumber.setText(charSequence.length() + "");
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
                        List<String> list = mAdapter.getData();
                        Router.getInstance()
                                .buildWithUrl("hmiou://m.54jietiao.com/select_pic/index")
                                .withString("enable_select_max_num", String.valueOf(3 + 1 - list.size()))
                                .navigation(mContext, REQ_OPEN_SELECT_PIC);

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

        try {
            int type = Integer.valueOf(mFeedbackType);
            mPresenter.setFeedbackType(type);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            mPresenter.setFeedbackType(FeedbackKind.FlashWrong.getValue());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_KEY_TYPE, mFeedbackType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQ_OPEN_SELECT_PIC == requestCode && RESULT_OK == resultCode) {
            List<String> listPaths = data.getStringArrayListExtra("extra_result_selection_path");
            Log.d("Photo", " path: " + listPaths);
            Flowable.just(listPaths)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Function<List<String>, List<String>>() {
                        @Override
                        public List<String> apply(List<String> list) throws Exception {
                            List<String> listFiles = new ArrayList<>();
                            for (String path : list) {
                                listFiles.add("file://" + CompressPictureUtil.compressPic(mContext, path).getAbsolutePath());
                            }
                            return listFiles;
                        }
                    })
                    .subscribe(new Consumer<List<String>>() {
                        @Override
                        public void accept(List<String> list) throws Exception {
                            for (String path : list) {
                                mAdapter.addImage(path);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });

        }
    }

//    private void compressPic(String fileUrl) {
//        CompressPictureUtil.compressPic(this, fileUrl, new CompressPictureUtil.OnCompressListener() {
//            @Override
//            public void onCompressPicSuccess(File file) {
//                Logger.d("图片压缩成功....");
//                mAdapter.addImage("file://" + file.getAbsolutePath());
//            }
//        });
//    }

    @OnClick(value = {R2.id.linearLayout_type})
    void onClick(View v) {
        if (v.getId() == R.id.linearLayout_type) {
            showFeedbackTypeDialog();
        }
    }

    private void showFeedbackTypeDialog() {
        IOSActionSheetTitleDialog.Builder builder = new IOSActionSheetTitleDialog.Builder(this);
        for (FeedbackKind kind : mFeedbackKindArr) {
            builder.addSheetItem(IOSActionSheetItem.create(kind.getDesc()).setItemClickListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Logger.d("click pos = " + which);
                    int type = mFeedbackKindArr[which].getValue();
                    mPresenter.setFeedbackType(type);
                }
            }));
        }
        builder.show();
    }

    @Override
    public void showTopFeedbackType(String typeStr) {
        mTvTopType.setText(typeStr);
    }

    @Override
    public void showBottomFeedbackType(String typeStr) {
        mTvType.setText(typeStr);
    }

    class FeedbackImageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        static final int MAX_PIC_COUNT = 3;

        public FeedbackImageAdapter() {
            super(R.layout.msgcenter_item_feedback_image);
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
                helper.setImageResource(R.id.iv_feedback_image, R.mipmap.msgcenter_ic_add_photo);
                helper.addOnClickListener(R.id.iv_feedback_image);
            } else {
                helper.setVisible(R.id.iv_delete, true);
                int resId = R.drawable.msgcenter_bg_feedback_item_pic;
                ImageLoader.getInstance(FeedbackActivity.this)
                        .displayImage(url, (ImageView) helper.getView(R.id.iv_feedback_image), resId, resId);
                helper.addOnClickListener(R.id.iv_delete);
            }
        }
    }

}
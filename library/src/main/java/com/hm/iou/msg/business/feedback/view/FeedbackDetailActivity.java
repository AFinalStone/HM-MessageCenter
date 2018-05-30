package com.hm.iou.msg.business.feedback.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hm.iou.base.BaseActivity;
import com.hm.iou.msg.R;
import com.hm.iou.msg.R2;
import com.hm.iou.msg.business.feedback.FeedbackDetailContract;
import com.hm.iou.msg.business.feedback.HistoryFeedbackContract;
import com.hm.iou.msg.business.feedback.presenter.FeedbackDetailPresenter;
import com.hm.iou.msg.business.feedback.presenter.HistoryFeedbackPresenter;
import com.hm.iou.tools.ImageLoader;
import com.hm.iou.uikit.HMGrayDividerItemDecoration;
import com.hm.iou.uikit.HMLoadMoreView;
import com.hm.iou.uikit.HMLoadingView;
import com.hm.iou.uikit.HMTopBarView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

/**
 * Created by hjy on 2018/5/28.
 */

public class FeedbackDetailActivity extends BaseActivity<FeedbackDetailPresenter> implements FeedbackDetailContract.View {

    public static final String EXTRA_KEY_ID = "feedback_id";

    @BindView(R2.id.topbar)
    HMTopBarView mTopBarView;
    @BindView(R2.id.lv_feedback_loading)
    HMLoadingView mLoadingView;

    @BindView(R2.id.ll_feedback_question)
    LinearLayout mLlQuestion;
    @BindView(R2.id.tv_feedback_type)
    TextView mTvFeedbackType;
    @BindView(R2.id.tv_feedback_status)
    TextView mTvFeedbackStatus;
    @BindView(R2.id.iv_feedback_header)
    ImageView mIvAvatar;
    @BindView(R2.id.iv_feedback_mytime)
    TextView mTvFeedbackTime;
    @BindView(R2.id.tv_feedback_question)
    TextView mTvQuestion;
    @BindView(R2.id.recyclerView_feedback_img)
    RecyclerView mRecyclerView;

    @BindView(R2.id.ll_feedback_answer)
    LinearLayout mLlAnswer;
    @BindView(R2.id.tv_feedback_answer_time)
    TextView mTvAnswerTime;
    @BindView(R2.id.tv_feedback_answer)
    TextView mTvAnswer;

    @BindView(R2.id.tv_feedback_noreply)
    TextView mTvNoReply;

    private String mFeedbackId;

    @Override
    protected int getLayoutId() {
        return R.layout.msg_activity_feedback_detail;
    }

    @Override
    protected FeedbackDetailPresenter initPresenter() {
        return new FeedbackDetailPresenter(this, this);
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        mFeedbackId = getIntent().getStringExtra(EXTRA_KEY_ID);
        if (bundle != null) {
            mFeedbackId = bundle.getString(EXTRA_KEY_ID);
        }

        mPresenter.getFeedbackDetail(mFeedbackId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_KEY_ID, mFeedbackId);
    }

    @Override
    public void showLoading(boolean show) {
        if (show) {
            mLoadingView.showDataLoading();
        } else {
            mLoadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoadingError(String tips) {
        mLoadingView.showDataFail(tips, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getFeedbackDetail(mFeedbackId);
            }
        });
    }

    @Override
    public void showQuestionLayout(boolean show) {
        mLlQuestion.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAnswerLayout(boolean show) {
        mLlAnswer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showNoReplyLayout(boolean show) {
        mTvNoReply.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showFeedbackType(String typeStr) {
        mTvFeedbackType.setText(typeStr);
    }

    @Override
    public void showFeedbackStatus(String statusStr, int textColor) {
        mTvFeedbackStatus.setText(statusStr);
        mTvFeedbackStatus.setTextColor(textColor);
    }

    @Override
    public void showUserAvatar(String url, int defResId) {
        ImageLoader.getInstance(this)
                .displayImage(url, mIvAvatar, defResId, defResId);
    }

    @Override
    public void showQuestionTime(String time) {
        mTvFeedbackTime.setText(time);
    }

    @Override
    public void showFeedbackQuestion(String question) {
        mTvQuestion.setText(question);
    }

    @Override
    public void showFeedbackImageList(List<String> urlList) {
        if (urlList == null || urlList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            return;
        }
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        QuestionImageAdapter adapter = new QuestionImageAdapter(urlList);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showAnswerTime(String time) {
        mTvAnswerTime.setText(time);
    }

    @Override
    public void showAnswer(String answer) {
        mTvAnswer.setText(answer);
    }

    class QuestionImageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public QuestionImageAdapter(@Nullable List<String> data) {
            super(R.layout.msg_item_feedback_image, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String url) {
            ImageView imageView = helper.getView(R.id.iv_feedback_image);
            ImageLoader.getInstance(FeedbackDetailActivity.this)
                    .displayImage(url, imageView, R.drawable.msg_bg_feedback_item_pic, R.drawable.msg_bg_feedback_item_pic);
        }
    }

}
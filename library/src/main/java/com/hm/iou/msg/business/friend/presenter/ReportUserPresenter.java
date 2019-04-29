package com.hm.iou.msg.business.friend.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hm.iou.base.file.FileApi;
import com.hm.iou.base.file.FileBizType;
import com.hm.iou.base.file.FileUploadResult;
import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.ReportItemBean;
import com.hm.iou.msg.bean.req.ReportUserReqBean;
import com.hm.iou.msg.business.friend.ReportUserContract;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created AFinalStone  on 2017/12/12.
 */

public class ReportUserPresenter extends MvpActivityPresenter<ReportUserContract.View> implements ReportUserContract.Presenter {

    private String mFriendId;
    private int mFeedbackId;
    private String mMemo;

    public ReportUserPresenter(@NonNull Context context, @NonNull ReportUserContract.View view) {
        super(context, view);
    }

    @Override
    public void getReportList() {
        mView.showInitLoadingView();
        MsgApi.getReportList(2)
                .compose(getProvider().<BaseResponse<List<ReportItemBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<ReportItemBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<ReportItemBean>>(mView) {
                    @Override
                    public void handleResult(List<ReportItemBean> noAgreeReasonBeans) {
                        mView.hideInitLoadingView();
                        mView.showData((ArrayList) noAgreeReasonBeans);
                    }

                    @Override
                    public void handleException(Throwable throwable, String code, String msg) {
                        mView.showInitLoadingFailed(msg);
                    }

                    @Override
                    public boolean isShowBusinessError() {
                        return false;
                    }

                    @Override
                    public boolean isShowCommError() {
                        return false;
                    }
                });
    }

    @Override
    public void reportUser(String friendId, int feedbackId, String filePath, String memo) {
        mFriendId = friendId;
        mFeedbackId = feedbackId;
        mMemo = memo;

        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath.replace("file://", ""));
            uploadImage(file);
        } else {
            sendReportToServer(null);
        }
    }

    private void sendReportToServer(String fileId) {
        mView.showLoadingView();
        ReportUserReqBean data = new ReportUserReqBean();
        data.setFeedbackId(mFeedbackId);
        data.setFriendId(mFriendId);
        data.setMemo(mMemo);
        data.setFileId(fileId);
        MsgApi.reportUser(data)
                .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Object>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        mView.dismissLoadingView();
                        mView.toastMessage("感谢您的举报，我们将尽快处理");
                        mView.closeCurrPage();
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.dismissLoadingView();
                    }
                });
    }

    private void uploadImage(final File file) {
        mView.showLoadingView("图片上传中...");
        FileApi.uploadImage(file, FileBizType.UserReport)
                .compose(getProvider().<BaseResponse<FileUploadResult>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<FileUploadResult>handleResponse())
                .subscribeWith(new CommSubscriber<FileUploadResult>(mView) {
                    @Override
                    public void handleResult(FileUploadResult fileUploadResult) {
                        mView.dismissLoadingView();
                        sendReportToServer(fileUploadResult.getFileId());
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.dismissLoadingView();
                    }
                });
    }

}

package com.hm.iou.msg.business.feedback.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hm.iou.base.file.FileApi;
import com.hm.iou.base.file.FileUploadResult;
import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.R;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.FeedbackDetailBean;
import com.hm.iou.msg.bean.FeedbackListItemBean;
import com.hm.iou.msg.business.feedback.FeedbackContract;
import com.hm.iou.msg.business.feedback.FeedbackDetailContract;
import com.hm.iou.msg.dict.FeedbackKind;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.sharedata.model.SexEnum;
import com.hm.iou.sharedata.model.UserInfo;
import com.hm.iou.tools.StringUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by hjy on 18/4/28.<br>
 */

public class FeedbackPresenter extends MvpActivityPresenter<FeedbackContract.View> implements FeedbackContract.Presenter {

    private int mFeedbackType;

    private List<String> mFileList;
    private List<String> mUploadFileUrlList;
    private int mTotalFileSize;

    private String mContent;

    public FeedbackPresenter(@NonNull Context context, @NonNull FeedbackContract.View view) {
        super(context, view);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void setFeedbackType(int type) {
        mFeedbackType = type;
        String desc = FeedbackKind.getDescByValue(mFeedbackType);
        desc = StringUtil.getUnnullString(desc);
        mView.showTopFeedbackType(String.format("#%s#", desc));
        mView.showBottomFeedbackType(desc);
    }

    @Override
    public void sendFeedback(String content, List<String> fileList) {
        if (TextUtils.isEmpty(content) || content.length() < 5) {
            mView.toastMessage("请至少输入5个字");
            return;
        }
        mContent = content;
        List<String> list = new ArrayList<>();
        if (fileList != null && fileList.size() > 0) {
            for (String file : fileList) {
                if (!TextUtils.isEmpty(file)) {
                    list.add(file);
                }
            }
        }

        //没有图片
        if (list.isEmpty()) {
            sendFeedbackToServer(content, "");
        } else {
            //有图片，需要先上传图片
            mFileList = list;
            mUploadFileUrlList = new ArrayList<>();
            mTotalFileSize = list.size();


            File file = new File(mFileList.get(0).replace("file://", ""));
            uploadImage(file, 0);
        }
    }

    private void sendFeedbackToServer(String content, String pics) {
        mView.showLoadingView();
        UserInfo userInfo = UserManager.getInstance(mContext).getUserInfo();
        MsgApi.sendFeedback(mFeedbackType, content, pics, userInfo.getUserId())
                .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Object>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        mView.dismissLoadingView();
                        mView.toastMessage("反馈成功");
                        mView.closeCurrPage();
                    }

                    @Override
                    public void handleException(Throwable throwable, String code, String msg) {
                        mView.dismissLoadingView();
                    }
                });
    }

    private void uploadImage(File file, final int pos) {
        mView.showLoadingView(String.format("图片上传中%d/%d", pos + 1, mTotalFileSize));
        UserInfo userInfo = UserManager.getInstance(mContext).getUserInfo();
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part partFile = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Map<String, Object> map = new HashMap<>();
        map.put("resourceType", "Complain");
        map.put("operId", userInfo.getUserId());
        map.put("operKind", "CUSTOMER");
        map.put("businessModel", "ANDROID");
        map.put("right", "777");
        FileApi.uploadFile(file, map)
                .compose(getProvider().<BaseResponse<FileUploadResult>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<FileUploadResult>handleResponse())
                .subscribeWith(new CommSubscriber<FileUploadResult>(mView) {
                    @Override
                    public void handleResult(FileUploadResult fileUploadResult) {
                        mView.dismissLoadingView();
                        mUploadFileUrlList.add(fileUploadResult.getFileId());

                        if (pos < mTotalFileSize - 1) {
                            File file = new File(mFileList.get(pos + 1).replace("file://", ""));
                            uploadImage(file, pos + 1);
                        } else {
                            //上传结束
                            StringBuffer sb = new StringBuffer();
                            int size = mUploadFileUrlList.size();
                            for (int i =0; i < size ; i++) {
                                sb.append(mUploadFileUrlList.get(i));
                                if (i < size - 1)
                                    sb.append(",");
                            }
                            String pics = sb.toString();
                            Logger.d("upload pic: " + pics);
                            sendFeedbackToServer(mContent, pics);
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.dismissLoadingView();
                    }
                });

    }

}
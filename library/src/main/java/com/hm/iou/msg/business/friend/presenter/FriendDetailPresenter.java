package com.hm.iou.msg.business.friend.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.FriendInfo;
import com.hm.iou.msg.business.friend.FriendDetailContract;
import com.hm.iou.msg.business.friend.view.FriendDetailActivity;
import com.hm.iou.msg.event.AddFriendEvent;
import com.hm.iou.msg.event.DeleteFriendEvent;
import com.hm.iou.msg.event.UpdateFriendEvent;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjy on 2019/4/11.
 */

public class FriendDetailPresenter extends MvpActivityPresenter<FriendDetailContract.View> implements FriendDetailContract.Presenter {

    public static final String ACTION_SET_REMARK_NAME = "设置备注名";
    public static final String ACTION_ADD_BLACK_NAME = "加入黑名单";
    public static final String ACTION_REMOVE_BLACK_NAME = "移出黑名单";
    public static final String ACTION_REPORT = "举报";
    public static final String ACTION_REMOVE_FRIEND = "解除好友关系";

    private FriendInfo mFriendInfo;
    private String mApplyStatus;

    public FriendDetailPresenter(@NonNull Context context, @NonNull FriendDetailContract.View view) {
        super(context, view);
    }

    @Override
    public void getUserInfo(String userId, String applyStatus) {
        mApplyStatus = applyStatus;
        mView.showLoadingView();
        MsgApi.getUserInfoById(userId)
                .compose(getProvider().<BaseResponse<FriendInfo>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<FriendInfo>handleResponse())
                .subscribeWith(new CommSubscriber<FriendInfo>(mView) {
                    @Override
                    public void handleResult(FriendInfo friendInfo) {
                        mView.dismissLoadingView();
                        mFriendInfo = friendInfo;
                        mView.showAvatar(friendInfo.getAvatarUrl());
                        mView.showNickname(friendInfo.getNickName(), friendInfo.getStageName());
                        mView.showUserId(friendInfo.getShowId());
                        mView.showLocation(friendInfo.getLocation());
                        mView.showUserType(friendInfo.getCustomerType() == 1 ? "实名用户" : "普通用户");
                        mView.showBlackNameTips(friendInfo.isBlackStatus() ? View.VISIBLE : View.INVISIBLE);

                        if (!friendInfo.isOwn() && friendInfo.isFriended()) {
                            mView.showButtonText("发消息");
                        } else {
                            if (FriendDetailActivity.APPLY_OVERDUE.equals(mApplyStatus)) {
                                mView.showButtonText("同意");
                            } else if (FriendDetailActivity.APPLY_WAIT_CONFIRM.equals(mApplyStatus)) {
                                mView.showButtonText("同意");
                            } else {
                                mView.showButtonText("添加朋友");
                            }
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String code, String msg) {
                        mView.dismissLoadingView();
                        mView.showDetailError(msg);
                    }

                    @Override
                    public boolean isShowCommError() {
                        return false;
                    }

                    @Override
                    public boolean isShowBusinessError() {
                        return false;
                    }
                });
    }

    @Override
    public void clickBottomMore() {
        if (mFriendInfo == null)
            return;
        List<String> list = new ArrayList<>();
        //非好友
        if (!mFriendInfo.isFriended()) {
            list.add(ACTION_REPORT);
            mView.showActionSheet(list);
            return;
        }
        list.add(ACTION_SET_REMARK_NAME);
        if (mFriendInfo.isBlackStatus()) {
            list.add(ACTION_REMOVE_BLACK_NAME);
        } else {
            list.add(ACTION_ADD_BLACK_NAME);
        }
        list.add(ACTION_REPORT);
        list.add(ACTION_REMOVE_FRIEND);
        mView.showActionSheet(list);
    }

    @Override
    public void clickAction(String action) {
        if (mFriendInfo == null)
            return;
        if (ACTION_ADD_BLACK_NAME.equals(action)) {             //加为黑名单
            mView.showBlackNameConfirmDialog();
        } else if (ACTION_REMOVE_BLACK_NAME.equals(action)) {   //移除黑名单
            removeBlackName();
        } else if (ACTION_SET_REMARK_NAME.equals(action)) {     //设置备注名
            mView.showAddRemarkNameDialog();
        } else if (ACTION_REPORT.equals(action)) {              //举报
            NavigationHelper.toFriendReportPage(mContext, mFriendInfo.getFriendId());
        } else if (ACTION_REMOVE_FRIEND.equals(action)) {       //移除好友关系
            //先查询与好友有关联的借条数目
            mView.showLoadingView();
            MsgApi.countSameIOU(mFriendInfo.getFriendId())
                    .compose(getProvider().<BaseResponse<Integer>>bindUntilEvent(ActivityEvent.DESTROY))
                    .map(RxUtil.<Integer>handleResponse())
                    .subscribeWith(new CommSubscriber<Integer>(mView) {
                        @Override
                        public void handleResult(Integer result) {
                            mView.dismissLoadingView();
                            String msg;
                            if (result > 0) {
                                msg = String.format("您和他(她)有%d份借款合同，确定要解除好友关系吗？", result);
                            } else {
                                msg = "确定要解除好友关系吗？解除后将不能收到该好友信息";
                            }
                            mView.showUnbindFriendRelationConfirmDialog(msg);
                        }

                        @Override
                        public void handleException(Throwable throwable, String s, String s1) {
                            mView.dismissLoadingView();
                        }
                    });
        }
    }

    @Override
    public void clickSubmitButton() {
        if (mFriendInfo == null)
            return;
        if (mFriendInfo.isOwn()) {
            mView.showAlertDialog("不能添加自己为好友");
            return;
        }
        //如果是好友，则跳转到聊天页面
        if (mFriendInfo.isFriended()) {
            NavigationHelper.toSessionDetail(mContext, mFriendInfo.getFriendImAccId());
        } else {
            if (FriendDetailActivity.APPLY_OVERDUE.equals(mApplyStatus)) {
                mView.showFriendApplyOverdueDialog();
            } else if (FriendDetailActivity.APPLY_WAIT_CONFIRM.equals(mApplyStatus)) {
                agreeApplyNewFriend();
            } else {
                if (mFriendInfo.isSysBlackStatus()) {
                    mView.showAlertDialog("该用户已禁止添加好友");
                    return;
                }
                //如果不是好友，则跳转到 发送好友请求 页面
                NavigationHelper.toSendVerifyRequestPage(mContext, mFriendInfo.getFriendId());
            }
        }
    }

    @Override
    public void addBlackName(String userId) {
        if (mFriendInfo == null)
            return;
        mView.showLoadingView();
        MsgApi.addBlackName(userId)
                .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        mView.dismissLoadingView();
                        mFriendInfo.setBlackStatus(true);
                        mView.showBlackNameTips(View.VISIBLE);
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.dismissLoadingView();
                    }
                });
    }

    /**
     * 移除黑名单
     */
    private void removeBlackName() {
        if (mFriendInfo == null)
            return;
        mView.showLoadingView();
        MsgApi.removeBlackName(mFriendInfo.getFriendId())
                .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        mView.dismissLoadingView();
                        mFriendInfo.setBlackStatus(false);
                        mView.showBlackNameTips(View.INVISIBLE);
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.dismissLoadingView();
                    }
                });
    }

    @Override
    public void updateRemarkName(final String remark) {
        if (mFriendInfo == null)
            return;
        if (TextUtils.isEmpty(remark))
            return;
        mView.showLoadingView();
        MsgApi.updateRemarkName(mFriendInfo.getFriendId(), remark)
                .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Object>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        mView.dismissLoadingView();
                        //备注名修改成功
                        mFriendInfo.setStageName(remark);
                        mView.showNickname(mFriendInfo.getNickName(), remark);

                        EventBus.getDefault().post(new UpdateFriendEvent());
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.dismissLoadingView();
                    }
                });
    }

    @Override
    public void removeFriendRelationship() {
        if (mFriendInfo == null)
            return;
        mView.showLoadingView();
        MsgApi.removeFriendById(mFriendInfo.getFriendId())
                .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Object>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        mView.dismissLoadingView();
                        mView.toastMessage("已解除好友关系");
                        mFriendInfo.setFriended(false);
                        mView.showButtonText("添加朋友");
                        EventBus.getDefault().post(new DeleteFriendEvent(mFriendInfo.getFriendId()));
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.dismissLoadingView();
                    }
                });
    }

    /**
     * 同意添加为好友
     */
    private void agreeApplyNewFriend() {
        if (mFriendInfo == null)
            return;
        mView.showLoadingView();
        MsgApi.agreeApply(mFriendInfo.getFriendId())
                .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Object>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        mView.dismissLoadingView();
                        //TODO 添加为好友了，后续通知
                        mFriendInfo.setFriended(true);
                        mApplyStatus = null;
                        mView.showButtonText("发消息");
                        EventBus.getDefault().post(new AddFriendEvent());
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.dismissLoadingView();
                    }
                });
    }
}

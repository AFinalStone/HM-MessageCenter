package com.hm.iou.msg.business.friend.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.FriendDbUtil;
import com.hm.iou.msg.MsgCenterConstants;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.R;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.FriendInfo;
import com.hm.iou.msg.bean.FriendRelationResBean;
import com.hm.iou.msg.business.friend.FriendDetailContract;
import com.hm.iou.msg.business.friend.view.FriendDetailActivity;
import com.hm.iou.msg.event.AddFriendEvent;
import com.hm.iou.msg.event.DeleteFriendEvent;
import com.hm.iou.msg.event.UpdateFriendEvent;
import com.hm.iou.sharedata.event.CommBizEvent;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjy on 2019/4/11.
 */

public class FriendDetailPresenter extends MvpActivityPresenter<FriendDetailContract.View> implements FriendDetailContract.Presenter {

    public static final String ACTION_REMOVE_FRIEND = "删除好友";
    public static final String ACTION_REPORT = "举报";
    public static final String ACTION_ADD_BLACK_NAME = "加入黑名单";
    public static final String ACTION_REMOVE_BLACK_NAME = "移出黑名单";

    private String mApplyStatus;    //只有确认好友请求时，才有数据
    private String mApplyId;
    private String mApplyRemarkName;

    private String mUserId;
    private int mIdType;

    private FriendInfo mFriendInfo;

    public FriendDetailPresenter(@NonNull Context context, @NonNull FriendDetailContract.View view) {
        super(context, view);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setFriendConfirmFlag(String applyStatus, String applyId, String applyRemarkName) {
        mApplyStatus = applyStatus;
        mApplyId = applyId;
        mApplyRemarkName = applyRemarkName;
    }

    @Override
    public void getUserInfo(String userId, int idType) {
        mUserId = userId;
        mIdType = idType;

        mView.showDetailLoading();
        mView.showDetailContent(View.GONE);
        MsgApi.getUserInfoById(userId, idType)
                .compose(getProvider().<BaseResponse<FriendInfo>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<FriendInfo>handleResponse())
                .subscribeWith(new CommSubscriber<FriendInfo>(mView) {
                    @Override
                    public void handleResult(FriendInfo friendInfo) {
                        mView.hideDetailLoading();
                        mView.showDetailContent(View.VISIBLE);
                        mFriendInfo = friendInfo;
                        if ((FriendDetailActivity.APPLY_OVERDUE.equals(mApplyStatus) ||
                                FriendDetailActivity.APPLY_WAIT_CONFIRM.equals(mApplyStatus)) &&
                                TextUtils.isEmpty(mFriendInfo.getStageName())) {
                            //确认好友申请，详情信息里是没有备注名的
                            mFriendInfo.setStageName(mApplyRemarkName);
                        }

                        mView.showAvatar(friendInfo.getAvatarUrl());
                        int sex = friendInfo.getSex();
                        if (sex == 0) {
                            mView.showSexImage(R.mipmap.uikit_ic_gender_woman);
                        } else if (sex == 1) {
                            mView.showSexImage(R.mipmap.uikit_ic_gender_man);
                        } else {
                            mView.showSexImage(0);
                        }
                        mView.showNickname(friendInfo.getNickName(), friendInfo.getStageName());
                        mView.showUserId(String.format("ID：%s（%s）", friendInfo.getShowId(), friendInfo.getNickName()));
                        String location = friendInfo.getLocation();
                        mView.showLocation(TextUtils.isEmpty(location) ? "保密" : location);
                        mView.showUserType(friendInfo.getCustomerType() == 1 ? "实名用户" : "普通用户");

                        mView.showRefuseBtn(View.GONE);
                        mView.updateSubmitButtonStyle(R.drawable.uikit_selector_btn_main, mContext.getResources().getColor(R.color.uikit_selector_btn_main));

                        //如果是自己
                        if (friendInfo.isOwn()) {
                            mView.showSubmitButtonText("不允许添加自己");
                            mView.updateSubmitButtonStyle(R.drawable.uikit_selector_btn_minor, mContext.getResources().getColor(R.color.uikit_text_auxiliary));
                            mView.showBottomMore(false);
                            mView.showCommentNameView(View.GONE, null);
                        } else if (friendInfo.isFriended()) {
                            //如果不是自己本人，并且对方是自己的好友
                            mView.showSubmitButtonText("发消息");
                            //下面显示"..."icon
                            mView.showBottomMore(true);
                            mView.showCommentNameView(View.VISIBLE, friendInfo.getStageName());
                        } else {
                            if (FriendDetailActivity.APPLY_OVERDUE.equals(mApplyStatus)) {
                                mView.showSubmitButtonText("同意并添加");
                                mView.showRefuseBtn(View.VISIBLE);
                                mView.showCommentNameView(View.VISIBLE, friendInfo.getStageName());
                                mView.showBottomMore(true);
                            } else if (FriendDetailActivity.APPLY_WAIT_CONFIRM.equals(mApplyStatus)) {
                                mView.showSubmitButtonText("同意并添加");
                                mView.showRefuseBtn(View.VISIBLE);
                                mView.showCommentNameView(View.VISIBLE, friendInfo.getStageName());
                                mView.showBottomMore(true);
                            } else {
                                mView.showSubmitButtonText("添加好友");
                                mView.showBottomMore(false);
                                mView.showCommentNameView(View.GONE, null);
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
        //如果已经是好友
        list.add(ACTION_REMOVE_FRIEND);
        list.add(ACTION_REPORT);
        if (mFriendInfo.isBlackStatus()) {
            list.add(ACTION_REMOVE_BLACK_NAME);
        } else {
            list.add(ACTION_ADD_BLACK_NAME);
        }

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
                                msg = "删除好友之后，系统会同步清空本地所有记录";
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
            mView.toastMessage("不允许添加自己");
            return;
        }
        //如果是好友，则跳转到聊天页面
        if (mFriendInfo.isFriended()) {
            NavigationHelper.toSessionDetail(mContext, mFriendInfo.getFriendImAccId(), true);
        } else {
            if (FriendDetailActivity.APPLY_OVERDUE.equals(mApplyStatus)) {
                mView.showFriendApplyOverdueDialog();
            } else if (FriendDetailActivity.APPLY_WAIT_CONFIRM.equals(mApplyStatus)) {
                //同意添加好友
                NavigationHelper.toSendVerifyRequestPage((Activity) mContext, mFriendInfo.getFriendId(), false, mFriendInfo);
            } else {
                checkFriendRelation();
            }
        }
    }

    /**
     * 检查好友关系，判断是否黑名单，是否已经申请过，是否能添加
     */
    private void checkFriendRelation() {
        mView.showLoadingView();
        MsgApi.findFriendRelation(mFriendInfo.getFriendId())
                .compose(getProvider().<BaseResponse<FriendRelationResBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<FriendRelationResBean>handleResponse())
                .subscribeWith(new CommSubscriber<FriendRelationResBean>(mView) {
                    @Override
                    public void handleResult(FriendRelationResBean data) {
                        mView.dismissLoadingView();
                        int code = data.getRelationCode();
                        if (code == 1) {            //是自己
                            mView.showAlertDialog("不能添加自己为好友");
                        } else if (code == 2) {     //直接添加，对方不需要确认就可以加为好友
                            NavigationHelper.toSendVerifyRequestPage((Activity) mContext, mFriendInfo.getFriendId(), true, mFriendInfo);
                        } else if (code == 3) {     //需要申请
                            NavigationHelper.toSendVerifyRequestPage((Activity) mContext, mFriendInfo.getFriendId(), true, mFriendInfo);
                        } else if (code == 4) {     //等待对方确认
                            NavigationHelper.toWaitProcessPage(mContext, mFriendInfo.getFriendId(),
                                    mFriendInfo.getSex(), mFriendInfo.getAvatarUrl(), data.getDesc(), data.isOverdue());
                        } else if (code == 5) {     //已经是好友
                            NavigationHelper.toSessionDetail(mContext, data.getImAccid(), false);
                            mView.closeCurrPage();
                        } else if (code == 6) {     //被对方拉黑了
                            NavigationHelper.toBlackNamePage(mContext, mFriendInfo.getFriendId(), data.getDesc(), true, mFriendInfo);
                        } else if (code == 7) {     //拉黑了对方
                            NavigationHelper.toBlackNamePage(mContext, mFriendInfo.getFriendId(), data.getDesc(), false, mFriendInfo);
                        } else if (code == 8) {     //对方已注销
                            NavigationHelper.toAccountClosedPage(mContext, mFriendInfo, data.getDesc(), true);
                        } else if (code == 9) {     //账户被系统拉黑
                            NavigationHelper.toAccountClosedPage(mContext, mFriendInfo, data.getDesc(), false);
                        }
                    }

                    @Override
                    public void handleException(Throwable t, String s, String s1) {
                        mView.dismissLoadingView();
                    }
                });
    }

    @Override
    public void addBlackName() {
        if (mFriendInfo == null)
            return;
        mView.showLoadingView();
        MsgApi.addBlackAndRemoveFriend(mFriendInfo.getFriendId())
                .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        mView.dismissLoadingView();
                        mFriendInfo.setBlackStatus(true);
                        mView.toastMessage("已拉黑该用户");
                        FriendDbUtil.deleteFriendByUserId(mFriendInfo.getFriendId());
                        EventBus.getDefault().post(new CommBizEvent(MsgCenterConstants.KEY_DELTE_IM_FRIEND, mFriendInfo.getFriendImAccId()));
                        EventBus.getDefault().post(new DeleteFriendEvent(mFriendInfo.getFriendId(), mFriendInfo.getFriendImAccId()));
                        mView.closeCurrPage();
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.dismissLoadingView();
                    }
                });
    }

    @Override
    public void updateRemarkName() {
        if (mFriendInfo == null)
            return;
        NavigationHelper.toUpdateRemarkName(mContext, mFriendInfo.getFriendId(), mFriendInfo.isFriended(), mFriendInfo.getStageName());
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
                        FriendDbUtil.deleteFriendByUserId(mFriendInfo.getFriendId());
                        EventBus.getDefault().post(new CommBizEvent(MsgCenterConstants.KEY_DELTE_IM_FRIEND, mFriendInfo.getFriendImAccId()));
                        EventBus.getDefault().post(new DeleteFriendEvent(mFriendInfo.getFriendId(), mFriendInfo.getFriendImAccId()));
                        mView.closeCurrPage();
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.dismissLoadingView();
                    }
                });
    }

    @Override
    public void refuseFriend(final String applyId) {
        mView.showLoadingView();
        MsgApi.refuseAndDelApplyRecord(applyId)
                .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Object>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        mView.dismissLoadingView();
                        //本地数据库删除该条数据
                        FriendDbUtil.deleteFriendApplyRecordByApplyId(applyId);
                        if (mFriendInfo != null) {
                            EventBus.getDefault().post(new DeleteFriendEvent(mFriendInfo.getFriendId(), null));
                        }
                        mView.closeCurrPage();
                    }

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
                        mView.toastMessage("移除成功");
                        mView.dismissLoadingView();
                        mFriendInfo.setBlackStatus(false);
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.dismissLoadingView();
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDeleteFriend(DeleteFriendEvent event) {
        mView.closeCurrPage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventAddFriend(AddFriendEvent event) {
        getUserInfo(mUserId, mIdType);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdateFriend(UpdateFriendEvent event) {
        //备注名修改成功
        if (TextUtils.isEmpty(event.reamrkName))
            return;
        String remark = event.reamrkName;
        mFriendInfo.setStageName(remark);
        mView.showNickname(mFriendInfo.getNickName(), remark);
        mView.showCommentNameView(View.VISIBLE, remark);
    }

}

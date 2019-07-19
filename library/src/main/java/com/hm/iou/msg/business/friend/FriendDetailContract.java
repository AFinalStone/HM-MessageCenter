package com.hm.iou.msg.business.friend;

import com.hm.iou.base.mvp.BaseContract;

import java.util.List;

public class FriendDetailContract {

    public interface View extends BaseContract.BaseView {

        /**
         * 显示加载详情失败后的错误信息
         *
         * @param errMsg
         */
        void showDetailError(String errMsg);

        void showDetailLoading();

        void hideDetailLoading();

        void showDetailContent(int visibility);

        /**
         * 底部显示"..."更多按钮
         *
         * @param isShow
         */
        void showBottomMore(boolean isShow);

        void showAvatar(String avatar);

        //显示性别图标
        void showSexImage(int sexResId);

        void showNickname(String nickname, String remarkName);

        void showUserId(String showId);

        void showUserType(String userType);

        void showLocation(String city);

        void showCommentNameView(int visibility, String name);

        /**
         * 显示提交按钮上的文字
         *
         * @param text
         */
        void showSubmitButtonText(String text);

        /**
         * 更新提交按钮的背景
         *
         * @param bgResId   背景
         * @param textColor 字体颜色
         */
        void updateSubmitButtonStyle(int bgResId, int textColor);

        void showActionSheet(List<String> list);

        void showAlertDialog(String msg);

        void showBlackNameConfirmDialog();

        /**
         * 显示删除好友关系的确认对话框
         *
         * @param msg
         */
        void showUnbindFriendRelationConfirmDialog(String msg);

        /**
         * 显示添加备注名对话框
         */
        void showAddRemarkNameDialog();

        /**
         * 显示好友申请信息已过期的弹窗
         */
        void showFriendApplyOverdueDialog();

        /**
         * 显示拒绝并删除按钮
         *
         * @param visibility
         */
        void showRefuseBtn(int visibility);
    }

    public interface Presenter extends BaseContract.BasePresenter {

        /**
         * 设置确认好友的状态标记
         *
         * @param applyStatus     "1"-表示好友申请已过期，"2"-表示等待确认好友申请，其他表示查看好友详情
         * @param applyId         好友申请记录id
         * @param applyRemarkName 好友申请时的备注名
         */
        void setFriendConfirmFlag(String applyStatus, String applyId, String applyRemarkName);

        /**
         * 获取好友详情信息
         *
         * @param userId 用户id
         * @param idType 1-嘿马uid，2-云信accId
         */
        void getUserInfo(String userId, int idType);

        /**
         * 点击底部"更多"按钮
         */
        void clickBottomMore();

        void clickAction(String action);

        void clickSubmitButton();

        /**
         * 添加黑名单
         */
        void addBlackName();

        /**
         * 更新备注名
         *
         * @param remark
         */
        void updateRemarkName(String remark);

        /**
         * 解除好友关系
         */
        void removeFriendRelationship();

        void refuseFriend(String applyId);
    }

}

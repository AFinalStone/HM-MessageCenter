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

        void showAvatar(String avatar);

        void showNickname(String nickname, String remarkName);

        void showUserId(String showId);

        void showUserType(String userType);

        void showLocation(String city);

        void showBlackNameTips(int visibility);

        void showButtonText(String text);

        void showActionSheet(List<String> list);

        void showAlertDialog(String msg);

        void showBlackNameConfirmDialog();

        /**
         * 显示解除好友关系的确认对话框
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
    }

    public interface Presenter extends BaseContract.BasePresenter {

        void getUserInfo(String userId, int idType, String applyStatus);

        /**
         * 点击底部"更多"按钮
         */
        void clickBottomMore();

        void clickAction(String action);

        void clickSubmitButton();

        /**
         * 添加黑名单
         *
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
    }

}

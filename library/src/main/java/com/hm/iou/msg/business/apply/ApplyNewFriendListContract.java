package com.hm.iou.msg.business.apply;

import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.msg.business.apply.view.IApplyNewFriend;

import java.util.List;

/**
 * @author : syl
 * @Date : 2018/5/30 11:01
 * @E-Mail : shiyaolei@dafy.com
 * 通讯录
 */
public class ApplyNewFriendListContract {

    public interface View extends BaseContract.BaseView {

        /**
         * 显示消息数据列表
         *
         * @param list
         */
        void showMsgList(List<IApplyNewFriend> list);


        /**
         * 允许刷新
         */
        void enableRefresh();

        /**
         * 隐藏下拉刷新View
         */
        void hidePullDownRefresh();

        /**
         * 显示初始化动画
         */
        void showInitLoading();

        /**
         * 初始化失败
         */
        void showInitFailed();

        /**
         * 关闭初始化动画
         */
        void hideInitLoading();

        /**
         * 显示数据为空
         */
        void showDataEmpty();


        void removeData(String applyId);
    }

    public interface Presenter extends BaseContract.BasePresenter {

        /**
         * 获取消息列表
         */
        void getMsgList();

        /**
         * 初始化
         */
        void init();

        /**
         * 删除好友申请请求
         *
         * @param applyId
         */
        void deleteApplyRecord(String applyId);
    }
}

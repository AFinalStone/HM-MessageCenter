package com.hm.iou.msg.business.message;

import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.msg.bean.ChatMsgBean;
import com.hm.iou.msg.bean.MsgListHeaderBean;

import java.util.List;

/**
 * @author : syl
 * @Date : 2018/5/30 11:01
 * @E-Mail : shiyaolei@dafy.com
 */
public class MsgCenterContract {

    public interface View extends BaseContract.BaseView {

        /**
         * 显示消息数据列表
         *
         * @param list
         */
        void showMsgList(List<ChatMsgBean> list);

        /**
         * 显示顶部消息
         *
         * @param list
         */
        void showHeaderModule(List<MsgListHeaderBean> list);

        /**
         * 更新头部条目样式
         *
         * @param msgListHeaderBean
         */
        void refreshHeaderModule(MsgListHeaderBean msgListHeaderBean);

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
         * 关闭初始化动画
         */
        void hideInitLoading();

        /**
         * 红色标记的数量
         *
         * @param redFlagCount
         */
        void updateRedFlagCount(String redFlagCount);

    }

    public interface Presenter extends BaseContract.BasePresenter {

        /**
         * 获取顶部模块
         */
        void getHeaderModules();

        /**
         * 获取会话列表
         */
        void getChatList();

        /**
         * 获取未读消息数量
         */
        void getUnReadMsgNum();

        /**
         * 标记为已读
         *
         * @param position 条目位置
         */
        void markHaveRead(int position);

        /**
         * 需要刷新未读消息数量
         */
        void onResume();

        /**
         * 获取头部红点数量
         */
        void getRedFlagCount();

    }
}

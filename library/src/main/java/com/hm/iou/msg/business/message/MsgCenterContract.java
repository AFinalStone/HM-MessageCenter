package com.hm.iou.msg.business.message;

import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.msg.business.message.view.IMsgItem;

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
        void showMsgList(List<IMsgItem> list);

        /**
         * 更新条目样式
         *
         * @param position
         */
        void refreshItem(int position);

        /**
         * 允许刷新
         */
        void enableRefresh();

        /**
         * 隐藏下拉刷新View
         */
        void hidePullDownRefresh();

        /**
         * 显示数据为空
         */
        void showDataEmpty();

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
         * 初始化
         */
        void init();

        /**
         * 从服务端获取消息列表
         */
        void getMsgListFromServer();

        /**
         * 从缓存中获取消息列表
         */
        void getMsgListFromCache();

        /**
         * 标记为已读
         *
         * @param position 条目位置
         */
        void markHaveRead(int position);

        /**
         * 获取头部红点数量
         */
        void getRedFlagCount();

    }
}

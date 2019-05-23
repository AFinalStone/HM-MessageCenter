package com.hm.iou.msg.business.hmmsg;

import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.msg.business.hmmsg.view.IHmMsgItem;

import java.util.List;

/**
 * @author : syl
 * @Date : 2018/5/30 11:01
 * @E-Mail : shiyaolei@dafy.com
 */
public class HmMsgListContract {

    public interface View extends BaseContract.BaseView {


        /**
         * 显示消息数据列表
         *
         * @param list
         */
        void showMsgList(List<IHmMsgItem> list);

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
        void showInitFailed(String msg);

        /**
         * 关闭初始化动画
         */
        void hideInitLoading();

        /**
         * 显示数据为空
         */
        void showDataEmpty();

        /**
         * 滚动到底部
         */
        void scrollToBottom();

        /**
         * 显示全部数据加载完毕，没有更多数据了
         */
        void showLoadMoreEnd();

        /**
         * 修改条目
         *
         * @param item
         */
        void notifyItem(IHmMsgItem item, int position);
    }

    public interface Presenter extends BaseContract.BasePresenter {
        /**
         * 初始化
         */
        void init();

        /**
         * 获取消息列表
         */
        void getMsgList();

        /**
         * 设置消息为已读
         *
         * @param position
         */
        void makeSingleMsgHaveRead(IHmMsgItem item, int position);

        void makeTypeMsgHaveRead();

    }
}

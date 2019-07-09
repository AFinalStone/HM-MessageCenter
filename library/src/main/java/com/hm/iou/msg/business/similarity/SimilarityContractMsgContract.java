package com.hm.iou.msg.business.similarity;

import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.msg.business.similarity.view.ISimilarityContractMsgItem;

import java.util.List;

/**
 * @author : syl
 * @Date : 2018/5/30 11:01
 * @E-Mail : shiyaolei@dafy.com
 */
public class SimilarityContractMsgContract {

    public interface View extends BaseContract.BaseView {

        /**
         * 显示消息数据列表
         *
         * @param list
         */
        void showMsgList(List<ISimilarityContractMsgItem> list);

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
         * 设置是否显示底部清空icon
         *
         * @param isShow
         */
        void setBottomMoreIconVisible(boolean isShow);

        /**
         * 显示红点数
         *
         * @param c
         */
        void showRedDot(int c);

        void removeData(String msgId);
        void updateData(ISimilarityContractMsgItem msgItem);
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
        void makeSingleMsgHaveRead(ISimilarityContractMsgItem item, int position);

        /**
         * 收录所有的疑似合同，并把所有疑似合同消息设置为已读
         */
        void includeAllSimilarity();

        /**
         * 删除单条消息
         *
         * @param item
         */
        void deleteMsg(ISimilarityContractMsgItem item);

        /**
         * 清空所有已读数据
         */
        void clearAllReadData();
    }
}

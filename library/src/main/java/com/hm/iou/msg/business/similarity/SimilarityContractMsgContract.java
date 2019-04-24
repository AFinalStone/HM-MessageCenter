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
         * 自动刷新数据
         */
        void autoRefresh();


        /**
         * 显示更多数据
         *
         * @param list
         */
        void showMoreNewsList(List<ISimilarityContractMsgItem> list);

        /**
         * 显示加载更多失败
         */
        void showLoadMoreFail();

        /**
         * 显示全部数据加载完毕，没有更多数据了
         */
        void showLoadMoreEnd();

        /**
         * 显示数据加载成功，可以加载下一页数据了
         */
        void showLoadMoreComplete();

    }

    public interface Presenter extends BaseContract.BasePresenter {


        /**
         * 获取更多数据
         */
        void getMoreData();

        /**
         * 初始化
         */
        void init();

        /**
         * 刷新数据
         */
        void refreshData();

    }
}

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

    }
}

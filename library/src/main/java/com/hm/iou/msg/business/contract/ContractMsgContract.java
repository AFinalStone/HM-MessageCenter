package com.hm.iou.msg.business.contract;

import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.msg.business.contract.view.IContractMsgItem;
import com.hm.iou.msg.business.message.view.ChatMsgModel;

import java.util.List;

/**
 * @author : syl
 * @Date : 2018/5/30 11:01
 * @E-Mail : shiyaolei@dafy.com
 */
public class ContractMsgContract {

    public interface View extends BaseContract.BaseView {

        /**
         * 显示消息数据列表
         *
         * @param list
         */
        void showMsgList(List<IContractMsgItem> list);


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
         * 显示数据为空
         */
        void showDataEmpty();

    }

    public interface Presenter extends BaseContract.BasePresenter {

        /**
         * 获取合同消息列表
         */
        void getContractList();

    }
}

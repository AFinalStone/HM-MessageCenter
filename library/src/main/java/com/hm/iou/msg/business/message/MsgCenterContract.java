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
         * 隐藏下拉刷新View
         */
        void hidePullDownRefresh();

        /**
         * 显示加载失败
         */
        void showDataEmpty();
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

    }
}

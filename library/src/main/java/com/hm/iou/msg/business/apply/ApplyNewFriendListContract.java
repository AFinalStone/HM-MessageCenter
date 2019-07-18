package com.hm.iou.msg.business.apply;

import android.graphics.Bitmap;

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
        void enableRefresh(boolean enabled);

        /**
         * 隐藏下拉刷新View
         */
        void hidePullDownRefresh();

        void removeData(String applyId);

        void removeDataByFriendId(String friendId);

        /**
         * 显示用户头部数据
         *
         * @param headerUrl
         * @param nickName
         * @param showId
         */
        void showHeaderData(String headerUrl, String nickName, String showId);

        /**
         * 显示个人名片二维码
         *
         * @param bitmap
         */
        void showQRCodeImage(Bitmap bitmap);
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

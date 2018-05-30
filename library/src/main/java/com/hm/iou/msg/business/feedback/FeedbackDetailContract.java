package com.hm.iou.msg.business.feedback;

import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.msg.business.feedback.view.IFeedbackListItem;

import java.util.List;

/**
 * Created by hjy on 2018/5/29.
 */

public interface FeedbackDetailContract {


    interface View extends BaseContract.BaseView {

        void showLoading(boolean show);

        void showQuestionLayout(boolean show);

        void showAnswerLayout(boolean show);

        void showNoReplyLayout(boolean show);

        void showLoadingError(String tips);

        /**
         * 显示反馈类别
         *
         * @param typeStr
         */
        void showFeedbackType(String typeStr);

        /**
         * 显示反馈状态：已处理、未处理
         *
         * @param statusStr 状态
         * @param textColor 文字颜色
         */
        void showFeedbackStatus(String statusStr, int textColor);

        /**
         * 显示用户头像
         *
         * @param url
         * @param defImgId
         */
        void showUserAvatar(String url, int defImgId);

        /**
         * 显示反馈时间
         *
         * @param time 时间
         */
        void showQuestionTime(String time);

        /**
         * 显示反馈的问题
         *
         * @param question
         */
        void showFeedbackQuestion(String question);

        /**
         * 设置反馈的图片
         *
         * @param urlList
         */
        void showFeedbackImageList(List<String> urlList);

        /**
         * 显示回答时间
         *
         * @param time
         */
        void showAnswerTime(String time);

        /**
         * 显示回答答案
         *
         * @param answer
         */
        void showAnswer(String answer);
    }

    interface Presenter extends BaseContract.BasePresenter {

        /**
         * 获取反馈详情信息
         *
         * @param id
         */
        void getFeedbackDetail(String id);

    }

}

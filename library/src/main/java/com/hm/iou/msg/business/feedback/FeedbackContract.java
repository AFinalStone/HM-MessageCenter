package com.hm.iou.msg.business.feedback;

import com.hm.iou.base.mvp.BaseContract;

import java.util.List;

/**
 * Created by hjy on 2018/5/29.
 */

public interface FeedbackContract {


    interface View extends BaseContract.BaseView {

        void showTopFeedbackType(String typeStr);

        void showBottomFeedbackType(String typeStr);
    }

    interface Presenter extends BaseContract.BasePresenter {

        void setFeedbackType(int type);

        void sendFeedback(String content, List<String> urlList);
    }

}

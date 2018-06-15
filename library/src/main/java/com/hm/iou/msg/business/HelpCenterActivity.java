package com.hm.iou.msg.business;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hm.iou.base.BaseBizAppLike;
import com.hm.iou.base.webview.BaseWebviewActivity;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.R;
import com.hm.iou.msg.dict.FeedbackKind;
import com.hm.iou.router.Router;
import com.hm.iou.uikit.dialog.IOSActionSheetItem;
import com.hm.iou.uikit.dialog.IOSActionSheetTitleDialog;

/**
 * Created by hjy on 2018/5/30.
 */

public class HelpCenterActivity extends BaseWebviewActivity implements View.OnClickListener {

    private FeedbackKind[] mFeedbackKindArr = new FeedbackKind[] {
            FeedbackKind.FlashWrong,
            FeedbackKind.DataWrong,
            FeedbackKind.NoCode,
            FeedbackKind.IouFail,
            FeedbackKind.Else
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent data = new Intent();
        data.putExtra(EXTRA_KEY_WEB_URL, BaseBizAppLike.getInstance().getH5Server() + "/help/help.html");
        data.putExtra(EXTRA_KEY_WEB_TITLE, "帮助中心");
        data.putExtra(EXTRA_KEY_SHOW_TITLE, "true");
        data.putExtra(EXTRA_KEY_SHOW_DIVIDER, "false");
        setIntent(data);
        super.onCreate(savedInstanceState);
        findViewById(R.id.tv_feedback).setOnClickListener(this);
        findViewById(R.id.tv_feedbackHistory).setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.msgcenter_activity_help_center;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_feedback) {
            showFeedbackTypeDialog();
        } else if (v.getId() == R.id.tv_feedbackHistory) {
            Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/message/feedback_history")
                    .navigation(this);
        }
    }

    private void showFeedbackTypeDialog() {
        IOSActionSheetTitleDialog.Builder builder = new IOSActionSheetTitleDialog.Builder(this);
        for (FeedbackKind kind : mFeedbackKindArr) {
            builder.addSheetItem(IOSActionSheetItem.create(kind.getDesc()).setItemClickListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Logger.d("click pos = " + which );
                    int type = mFeedbackKindArr[which].getValue();
                    Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/message/feedback")
                            .withString("type", type + "")
                            .navigation(HelpCenterActivity.this);
                }
            }));
        }
        builder.show();
    }

}

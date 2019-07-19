package com.hm.iou.msg.business;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.hm.iou.base.BaseActivity;
import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.logger.Logger;
import com.hm.iou.router.Router;
import com.hm.iou.sharedata.UserManager;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

/**
 * Created by hjy on 2019/4/18.
 */

public class NotificationEntranceActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected MvpActivityPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initEventAndData(Bundle bundle) {
        //如果已经登出了，则不跳转进去
        if (!UserManager.getInstance(this).isLogin()) {
            finish();
            return;
        }
        ArrayList<IMMessage> msgList = (ArrayList<IMMessage>) getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
        Logger.d("IM msg size:" + (msgList != null ? msgList.size() : 0));

        if (msgList != null && !msgList.isEmpty()) {
            IMMessage message = msgList.get(0);
            String sessionId = message.getSessionId();
            SessionTypeEnum type = message.getSessionType();
            if (type == SessionTypeEnum.P2P) {
                StringBuffer sb = new StringBuffer();
                sb.append("hmiou://m.54jietiao.com/message/session_detail")
                        .append("?friend_im_id=")
                        .append(Uri.encode(sessionId))
                        .append("&if_check_status=")
                        .append(Uri.encode("false"));
                Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/main/index")
                        .withString("tab_type", "tag_message")
                        .withString("url", sb.toString())
                        .navigation(mContext);
                finish();
                return;
            }
        }
        PackageManager packageManager = getPackageManager();
        String packageName = getPackageName();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        finish();
    }
}

package com.hm.iou.msg.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hm.iou.msg.MsgCenterAppLike;
import com.hm.iou.msg.business.alipay.list.view.AliPayMsgActivity;
import com.hm.iou.msg.business.contractmsg.view.ContractMsgActivity;
import com.hm.iou.msg.business.friend.view.FriendDetailActivity;
import com.hm.iou.msg.business.hmmsg.view.HmMsgListActivity;
import com.hm.iou.msg.business.message.view.SessionDetailPreviewActivity;
import com.hm.iou.msg.business.remindback.view.RemindBackMsgActivity;
import com.hm.iou.msg.business.similarity.view.SimilarityContractMsgActivity;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.event.CommBizEvent;
import com.hm.iou.sharedata.event.LoginSuccEvent;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.sharedata.model.UserInfo;
import com.hm.iou.tools.ToastUtil;
import com.sina.weibo.sdk.utils.MD5;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, IMTestActivity.class));
            }
        });

        findViewById(R.id.btn_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
            }
        });

        findViewById(R.id.btn_msgCenter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestActivity.class));
            }
        });
        findViewById(R.id.btn_getMsgCenterNoReadNum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBusHelper.postEventBusGetMsgNoReadNum(true);
            }
        });
        findViewById(R.id.btn_get_contract_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ContractMsgActivity.class));
            }
        });
        findViewById(R.id.btn_get_remind_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RemindBackMsgActivity.class));
            }
        });
        findViewById(R.id.btn_get_similarity_contract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SimilarityContractMsgActivity.class));
            }
        });
        findViewById(R.id.btn_get_hm_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HmMsgListActivity.class));
            }
        });


        findViewById(R.id.btn_add_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //        startActivity(new Intent(MainActivity.this, AddFriendIndexActivity.class));

                Intent intent = new Intent(MainActivity.this, FriendDetailActivity.class);
                intent.putExtra(FriendDetailActivity.EXTRA_KEY_USER_ID, "1362");
                intent.putExtra(FriendDetailActivity.EXTRA_KEY_APPLY_STATUS, FriendDetailActivity.APPLY_WAIT_CONFIRM);
                startActivity(intent);


//                NavigationHelper.toSendVerifyRequestPage(MainActivity.this, "1383");
            }
        });

        findViewById(R.id.btn_add_friend2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, AddFriendIndexActivity.class));

/*                System.out.println(TimeUtil.formatContractMsgStartTime("2019-04-26 12:12:34"));
                System.out.println(TimeUtil.formatContractMsgStartTime("2019-04-25 23:12:34"));
                System.out.println(TimeUtil.formatContractMsgStartTime("2019-04-25 12:12:34"));
                System.out.println(TimeUtil.formatContractMsgStartTime("2019-04-25 8:12:34"));
                System.out.println(TimeUtil.formatContractMsgStartTime("2019-04-24 23:59:59"));*/

            }
        });
        findViewById(R.id.btn_get_alipay_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AliPayMsgActivity.class));
            }
        });
        findViewById(R.id.btn_session_preview_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SessionDetailPreviewActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void login() {
        String pwd = MD5.hexdigest("123456".getBytes());
        MobileLoginReqBean reqBean = new MobileLoginReqBean();
//        reqBean.setMobile("15267163669");
        reqBean.setMobile("17681832816");
//        reqBean.setMobile("15967132742");
        reqBean.setMobile("15967132742");
//        reqBean.setMobile("13186975702");
        reqBean.setQueryPswd(pwd);
        HttpReqManager.getInstance().getService(MsgCenterService.class)
                .mobileLogin(reqBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<UserInfo>>() {
                    @Override
                    public void accept(BaseResponse<UserInfo> userInfoBaseResponse) throws Exception {
                        ToastUtil.showMessage(MainActivity.this, "登录成功");
                        UserInfo userInfo = userInfoBaseResponse.getData();
                        UserManager.getInstance(MainActivity.this).updateOrSaveUserInfo(userInfo);
                        HttpReqManager.getInstance().setUserId(userInfo.getUserId());
                        HttpReqManager.getInstance().setToken(userInfo.getToken());
                        EventBus.getDefault().post(new LoginSuccEvent());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        t.printStackTrace();
                    }
                });
    }

    public void toBlackName(View v) {
//        NavigationHelper.toBlackNamePage(this);
    }

    public void toWaitProcess(View v) {
      //  NavigationHelper.toWaitProcessPage(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenBusMsgCenterNoRead(CommBizEvent commBizEvent) {
        if (MsgCenterAppLike.EXTRA_KEY_GET_NO_READ_NUM_SUCCESS.equals(commBizEvent.key)) {
            ToastUtil.showMessage(this, "未读消息数量：" + commBizEvent.content);
        }
    }

}

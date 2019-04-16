package com.hm.iou.msg;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;

import com.hm.iou.base.file.FileUtil;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.UnReadMsgNumBean;
import com.hm.iou.msg.im.MsgViewHolderTip;
import com.hm.iou.msg.util.CacheDataUtil;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.event.CommBizEvent;
import com.hm.iou.sharedata.event.LoginSuccEvent;
import com.hm.iou.sharedata.event.LogoutEvent;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.UIKitOptions;
import com.netease.nim.uikit.api.model.session.SessionEventListener;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * @author syl
 * @time 2018/6/1 下午5:48
 */
public class MsgCenterAppLike {

    public static final String EXTRA_KEY_GET_NO_READ_NUM = "MsgCenter_getNoReadNum";
    public static final String EXTRA_KEY_GET_NO_READ_NUM_SUCCESS = "MsgCenter_getNoReadNumSuccess";
    public static final String EXTRA_KEY_INIT_IM = "MsgCenter_initIM";

    private static MsgCenterAppLike mApp;
    public boolean mHaveInitIM = false;//是否已经初始化IM


    public static MsgCenterAppLike getInstance() {
        if (mApp == null) {
            throw new RuntimeException("MsgCenterAppLike should init first.");
        }
        return mApp;
    }

    private Disposable mListDisposable;
    private Context mContext;
    private String mTopHeadRedFlagCount;    //导航栏上红点标记数字

    public void onCreate(Context context) {
        mContext = context;
        mApp = this;
        EventBus.getDefault().register(this);
        //初始化IM
        initIM();
    }

    public void initIM() {
        //TODO
        LoginInfo loginInfo = getLoginInfo();
//        if (loginInfo == null) {
//            return;
//        }
        if (!mHaveInitIM) {
            NIMClient.init(mContext, loginInfo, options());
            String packageName = mContext.getPackageName();
            String processName = getProcessName();
            if (packageName.equals(processName)) {
                // 初始化UIKit模块
                NimUIKit.init(mContext, buildUIKitOptions());
                //黑名单
                NimUIKit.registerTipMsgViewHolder(MsgViewHolderTip.class);
                //头像点击事件
                SessionEventListener listener = new SessionEventListener() {
                    @Override
                    public void onAvatarClicked(Context context, IMMessage message) {
                        // 一般用于打开用户资料页面
//                if (message.getMsgType() == MsgTypeEnum.robot && message.getDirect() == MsgDirectionEnum.In) {
//                    RobotAttachment attachment = (RobotAttachment) message.getAttachment();
//                    if (attachment.isRobotSend()) {
//                        RobotProfileActivity.start(context, attachment.getFromRobotAccount());
//                        return;
//                    }
//                }
//                UserProfileActivity.start(context, message.getFromAccount());
                        Logger.d("onAvatarClicked");
                    }

                    @Override
                    public void onAvatarLongClicked(Context context, IMMessage message) {
                        // 一般用于群组@功能，或者弹出菜单，做拉黑，加好友等功能
                        Logger.d("onAvatarLongClicked");
                    }

                    @Override
                    public void onAckMsgClicked(Context context, IMMessage message) {
                        // 已读回执事件处理，用于群组的已读回执事件的响应，弹出消息已读详情
//                AckMsgInfoActivity.start(context, message);
                        Logger.d("onAckMsgClicked");
                    }
                };
                NimUIKit.setSessionListener(listener);
                //登陆
                NimUIKit.login(loginInfo, new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {

                    }

                    @Override
                    public void onFailed(int code) {

                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
            }
            mHaveInitIM = true;
        }
    }

    private UIKitOptions buildUIKitOptions() {
        UIKitOptions options = new UIKitOptions();
        // 设置app图片/音频/日志等缓存目录
        options.appCacheDir = FileUtil.getExternalCacheDirPath(mContext) + "/app";
        return options;
    }

    // 如果返回值为 null，则全部使用默认参数。
    private SDKOptions options() {
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
//        config.notificationEntrance = MainActivity.class; // 点击通知栏跳转到该Activity
//        config.notificationSmallIconId = R.drawable.ic_launcher_background;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://" + mContext.getPackageName() + "/raw/msg";
        options.statusBarNotificationConfig = config;

        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用采用默认路径作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        String sdkPath = FileUtil.getExternalCacheDirPath(mContext) + "/nim"; // 可以不设置，那么将采用默认路径
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        options.sdkStorageRootPath = sdkPath;

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;
        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
//        options.thumbnailSize =  /2;
        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = new UserInfoProvider() {

            @Override
            public UserInfo getUserInfo(String account) {
                UserInfo userInfo = new UserInfo() {
                    @Override
                    public String getAccount() {
                        return "123";
                    }

                    @Override
                    public String getName() {
                        return "石头";
                    }

                    @Override
                    public String getAvatar() {

                        return "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1554904754963&di=0190a2aa4e57906eb5135d0667e9656e&imgtype=0&src=http%3A%2F%2Fwww.lovehhy.net%2Flib%2Fimg%2F9555118%2F1336676_0002011295.jpg";
                    }
                };
                return userInfo;
            }

            @Override
            public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType) {
                return null;
            }

            @Override
            public Bitmap getAvatarForMessageNotifier(SessionTypeEnum sessionType, String sessionId) {
                return null;
            }
        };
        return options;
    }

    private LoginInfo getLoginInfo() {
        com.hm.iou.sharedata.model.UserInfo userInfo = UserManager.getInstance(mContext).getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getImAccId()) || TextUtils.isEmpty(userInfo.getImToken())) {
            return null;
        }
        String imId = userInfo.getImAccId();
        String imToken = userInfo.getImToken();
        return new LoginInfo(imId, imToken);
    }

    /**
     * 获取当前进程名称
     *
     * @return
     */
    private String getProcessName() {
        String processName = null;

        // ActivityManager
        ActivityManager am = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE));

        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;
                    break;
                }
            }

            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }


    public String getTopHeadRedFlagCount() {
        return mTopHeadRedFlagCount;
    }

    /**
     * 获取消息中心未读消息数量
     */
    public void getMsgCenterNoReadNum() {
        if (mListDisposable != null && !mListDisposable.isDisposed()) {
            mListDisposable.dispose();
        }
        mListDisposable = MsgApi.getUnReadMsgNum()
                .map(RxUtil.<UnReadMsgNumBean>handleResponse())
                .subscribe(new Consumer<UnReadMsgNumBean>() {
                    @Override
                    public void accept(UnReadMsgNumBean unReadMsgNumBean) throws Exception {
                        int numNoRead = unReadMsgNumBean.getButlerMessageNumber()
                                + unReadMsgNumBean.getContractNumber()
                                + unReadMsgNumBean.getSimilarContractNumber()
                                + unReadMsgNumBean.getWaitRepayNumber();
                        EventBusHelper.postEventBusGetMsgNoReadNumSuccess(String.valueOf(numNoRead));
                        CacheDataUtil.setNoReadMsgNum(mContext, unReadMsgNumBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        getMsgCenterNoReadNumFromCache();
                    }
                });
    }

    /**
     * 从缓存中获取消息中心未读消息数量
     */
    public void getMsgCenterNoReadNumFromCache() {
        UnReadMsgNumBean unReadMsgNumBean = CacheDataUtil.getNoReadMsgNum(mContext);
        int numNoRead = 0;
        if (unReadMsgNumBean != null) {
            numNoRead = unReadMsgNumBean.getButlerMessageNumber()
                    + unReadMsgNumBean.getContractNumber()
                    + unReadMsgNumBean.getSimilarContractNumber()
                    + unReadMsgNumBean.getWaitRepayNumber();
        }
        EventBusHelper.postEventBusGetMsgNoReadNumSuccess(String.valueOf(numNoRead));
    }

    /**
     * 获取消息中心未读消息数量
     *
     * @param -commBizEvent.key    == getMsgCenterNoReadNum
     * @param -commBizEvent.cotent ture的时候从服务端获取最新的消息，否则直接从缓存中获取
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenBusgetMsgCenterNoReadNum(CommBizEvent commBizEvent) {
        if (EXTRA_KEY_GET_NO_READ_NUM.equals(commBizEvent.key)) {
            if ("true".equals(commBizEvent.content)) {
                getMsgCenterNoReadNum();
            } else {
                getMsgCenterNoReadNumFromCache();
            }
        }
    }

    /**
     * 成功获取个人中心红色标记数量
     *
     * @param commBizEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenBusUserInfoHomeLeftMenuRedFlagCount(CommBizEvent commBizEvent) {
        if ("userInfo_homeLeftMenu_redFlagCount".equals(commBizEvent.key)) {
            mTopHeadRedFlagCount = commBizEvent.content;
        }
    }

    /**
     * 用户登陆成功
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventLogout(LoginSuccEvent event) {
        initIM();
    }

    /**
     * 用户退出
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventLogout(LogoutEvent event) {
        CacheDataUtil.clearAllCache(mContext);
    }


}

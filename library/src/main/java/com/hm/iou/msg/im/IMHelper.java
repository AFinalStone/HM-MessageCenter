package com.hm.iou.msg.im;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Process;
import android.text.TextUtils;

import com.hm.iou.base.file.FileUtil;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.NavigationHelper;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.ChatMsgBean;
import com.hm.iou.msg.bean.GetOrRefreshIMTokenBean;
import com.hm.iou.msg.business.NotificationEntranceActivity;
import com.hm.iou.msg.util.DataChangeUtil;
import com.hm.iou.msg.util.MsgCenterMsgUtil;
import com.hm.iou.network.HttpReqManager;
import com.hm.iou.router.Router;
import com.hm.iou.sharedata.UserManager;
import com.hm.iou.sharedata.event.LogoutEvent;
import com.hm.iou.tools.ImageLoader;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.UIKitOptions;
import com.netease.nim.uikit.api.model.session.SessionEventListener;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.misc.DirCacheFileType;
import com.netease.nimlib.sdk.misc.MiscService;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author syl
 * @time 2019/4/16 7:33 PM
 * IM初始化工具类
 */

public class IMHelper {

    private static IMHelper mImHelper;
    private Context mContext;
    //  创建观察者对象
    private Observer<List<RecentContact>> mChatListObserver;
    private List<OnChatListChangeListener> mOnChatListChangeListenerList;
    private Disposable mDisLogin;

    public static IMHelper getInstance(Context context) {
        if (mImHelper == null) {
            mImHelper = new IMHelper(context.getApplicationContext());
        }
        return mImHelper;
    }

    private IMHelper(Context context) {
        this.mContext = context;
    }

    public void initIM() {
        NIMClient.init(mContext, getLoginInfo(), options());
        String packageName = mContext.getPackageName();
        String processName = getProcessName();
        if (packageName.equals(processName)) {
            // 初始化UIKit模块
            NimUIKit.init(mContext, buildUIKitOptions());
            //监听登陆状态
            listenerLoginStatus();
            //登陆
            login();
            //订制聊天页面的头像点击
            NimUIKit.setSessionListener(new SessionEventListener() {
                @Override
                public void onAvatarClicked(Context context, IMMessage imMessage) {
                    String sessionId = imMessage.getSessionId();
                    if (imMessage.getDirect() == MsgDirectionEnum.Out) {
                        //说明点击的是 我本人的头像
                        com.hm.iou.sharedata.model.UserInfo userInfo = UserManager.getInstance(mContext)
                                .getUserInfo();
                        NavigationHelper.toMyDetailPageFromSession(mContext, userInfo.getUserId());
                        return;
                    }
                    NavigationHelper.toFriendDetailPageFromSession(mContext, sessionId);
                }

                @Override
                public void onAvatarLongClicked(Context context, IMMessage imMessage) {

                }

                @Override
                public void onAckMsgClicked(Context context, IMMessage imMessage) {

                }
            });
            //黑名单
            NimUIKit.registerTipMsgViewHolder(HmNotificationMsgViewHolder.class);
            //会话列表变更监听对象
            if (mChatListObserver == null) {
                mChatListObserver = new Observer<List<RecentContact>>() {
                    @Override
                    public void onEvent(List<RecentContact> messages) {
                        if (mOnChatListChangeListenerList == null) {
                            return;
                        }
                        //从缓存中获取
                        MsgCenterMsgUtil.getMsgCenterNoReadNumFromCache(mContext);
                        //监听回调
                        for (OnChatListChangeListener listener : mOnChatListChangeListenerList) {
                            List<ChatMsgBean> chatList = DataChangeUtil.changeRecentContactToIChatMsgItem(messages);
                            listener.onDataChange(chatList);
                        }
                    }
                };
            }
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
        config.notificationEntrance = NotificationEntranceActivity.class; // 点击通知栏跳转到该Activity
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
                return NIMClient.getService(UserService.class).getUserInfo(account);
            }

            @Override
            public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType) {
                return UserInfoHelper.getUserDisplayName(account);
            }

            @Override
            public Bitmap getAvatarForMessageNotifier(SessionTypeEnum sessionType, String sessionId) {
                String headerUrl = NIMClient.getService(UserService.class).getUserInfo(sessionId).getAvatar();
                return ImageLoader.getInstance(mContext).getImageBitmap(headerUrl);
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
        android.app.ActivityManager am = (android.app.ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        if (list == null) {
            return null;
        }
        for (android.app.ActivityManager.RunningAppProcessInfo processInfo : list) {
            if (processInfo.pid == Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }

    /**
     * 删除最近会话
     *
     * @param account
     */
    public void deleteRecentContract(String account) {
        NIMClient.getService(MsgService.class)
                .deleteRecentContact2(account, SessionTypeEnum.P2P);
    }

    /**
     * 刷新token并登陆
     */
    public void refreshTokenAndLogin() {
        if (mDisLogin != null && !mDisLogin.isDisposed()) {
            mDisLogin.dispose();
        }
        mDisLogin = MsgApi.getOrRefreshIMToken()
                .map(RxUtil.<GetOrRefreshIMTokenBean>handleResponse())
                .subscribe(new Consumer<GetOrRefreshIMTokenBean>() {
                    @Override
                    public void accept(GetOrRefreshIMTokenBean getOrRefreshIMTokenBean) throws Exception {
                        UserManager.getInstance(mContext).updateIMId(getOrRefreshIMTokenBean.getImAccId());
                        UserManager.getInstance(mContext).updateIMToken(getOrRefreshIMTokenBean.getImToken());
                        login();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    public void login() {
        //为UIKIT模块保存用户账号
        if (getLoginInfo() == null) {
            return;
        }
        NimUIKit.login(getLoginInfo(), new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                //  注册会话列表观察者对象
                NIMClient.getService(MsgServiceObserve.class)
                        .observeRecentContact(mChatListObserver, true);
            }

            @Override
            public void onFailed(int code) {
                NimUIKitImpl.loginSuccess(getLoginInfo().getAccount());
            }

            @Override
            public void onException(Throwable exception) {
                NimUIKitImpl.loginSuccess(getLoginInfo().getAccount());
            }
        });
    }


    /**
     * 登出，释放相关资源
     */
    public void logout() {
        //注销会话列表观察者
        mOnChatListChangeListenerList = null;
        NIMClient.getService(AuthService.class).logout();
        //调用登出接口
        NimUIKitImpl.logout();
        //  注册会话列表观察者对象
        NIMClient.getService(MsgServiceObserve.class)
                .observeRecentContact(mChatListObserver, false);
        deleteCache();
    }

    private void deleteCache() {
        List<DirCacheFileType> fileTypes = new ArrayList<>();
        fileTypes.add(DirCacheFileType.IMAGE);
        fileTypes.add(DirCacheFileType.VIDEO);
        fileTypes.add(DirCacheFileType.THUMB);
        fileTypes.add(DirCacheFileType.AUDIO);
        fileTypes.add(DirCacheFileType.OTHER);
        fileTypes.add(DirCacheFileType.LOG);

        NIMClient.getService(MiscService.class).clearDirCache(fileTypes, 0, 0).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void size) {
                // 删除成功
            }

            @Override
            public void onFailed(int code) {
            }

            @Override
            public void onException(Throwable exception) {
            }
        });
    }

    /**
     * 获取未读消息总数
     *
     * @return
     */
    public int getTotalUnReadMsgCount() {
        return NIMClient.getService(MsgService.class).getTotalUnreadCount();
    }

    /**
     * 监听登陆状态
     */
    private void listenerLoginStatus() {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(
                new Observer<StatusCode>() {
                    public void onEvent(StatusCode status) {
                        Logger.d("用户在线状态发生改变StatusCode==" + status.getValue());
                        if (StatusCode.PWD_ERROR.getValue() == status.getValue()) {//账号或密码错误
                            refreshTokenAndLogin();
                        } else if (StatusCode.UNLOGIN.getValue() == status.getValue()) {
                            login();
                        } else if (StatusCode.KICKOUT.getValue() == status.getValue()
                                || StatusCode.KICK_BY_OTHER_CLIENT.getValue() == status.getValue()
                                || StatusCode.FORBIDDEN.getValue() == status.getValue()) {//用户被禁止登陆或者被踢下线
                            EventBus.getDefault().post(new LogoutEvent());
                            HttpReqManager.getInstance().setUserId("");
                            HttpReqManager.getInstance().setToken("");
                            UserManager.getInstance(mContext).logout();
                            com.hm.iou.base.ActivityManager.getInstance().exitAllActivities();
                            Router.getInstance().buildWithUrl("hmiou://m.54jietiao.com/login/selecttype")
                                    .navigation(mContext);
                        }
                    }
                }, true);
    }

    /**
     * 添加监听
     */
    public void addOnChatListChangeListener(OnChatListChangeListener onChatListChangeListener) {
        if (mOnChatListChangeListenerList == null) {
            mOnChatListChangeListenerList = new ArrayList<>();
        }
        mOnChatListChangeListenerList.add(onChatListChangeListener);
    }

    /**
     * 移除监听
     */
    public void removeOnChatListChangeListener(OnChatListChangeListener onChatListChangeListener) {
        if (mOnChatListChangeListenerList != null) {
            mOnChatListChangeListenerList.remove(onChatListChangeListener);
        }
    }

    /**
     * 会话列表变化监听对象
     */
    public interface OnChatListChangeListener {
        /**
         * 数据发生了变化
         *
         * @param chatMsgBeanList
         */
        void onDataChange(List<ChatMsgBean> chatMsgBeanList);
    }

}

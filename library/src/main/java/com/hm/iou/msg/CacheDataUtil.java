package com.hm.iou.msg;

import android.content.Context;
import android.text.TextUtils;

import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.MsgCenterDbData;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.bean.MsgDetailBean;
import com.hm.iou.tools.SPUtil;
import com.hm.iou.tools.TimeUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author syl
 * @time 2018/5/30 下午6:57
 */
public class CacheDataUtil {

    /**
     * 获取未读消息数量
     *
     * @return
     */
    public static synchronized long getNoReadMsgNum() {
        return MsgCenterDbHelper.queryMsgCenterNoReadCount();
    }

    /**
     * 添加list到cache中
     *
     * @return
     */
    public static synchronized void addMsgListToCache(List<MsgDetailBean> list) {
        Flowable.just(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<List<MsgDetailBean>, List<MsgCenterDbData>>() {
                    @Override
                    public List<MsgCenterDbData> apply(List<MsgDetailBean> list) throws Exception {
                        List<MsgCenterDbData> listMsgCenter = new ArrayList<>();
                        for (MsgDetailBean msgDetailBean : list) {
                            MsgCenterDbData dbData = makeMsgDetailBeanToMsgCenterDbData(msgDetailBean);
                            listMsgCenter.add(dbData);
                        }
                        return listMsgCenter;
                    }
                })
                .map(new Function<List<MsgCenterDbData>, Void>() {
                    @Override
                    public Void apply(List<MsgCenterDbData> list) throws Exception {
                        MsgCenterDbHelper.addOrUpdateDataToMsgCenter(list);
                        return null;
                    }
                })
                .subscribe(new Consumer<Void>() {
                    @Override
                    public void accept(Void aVoid) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.e(throwable.getMessage());
                    }
                });
    }

    /**
     * 新增或者更新消息都消息中心
     *
     * @param msgDetailBean 需要新增或者更新的item
     */
    public static synchronized void updateMsgItemToCache(MsgDetailBean msgDetailBean) {
        MsgCenterDbData dbData = makeMsgDetailBeanToMsgCenterDbData(msgDetailBean);
        MsgCenterDbHelper.addOrUpdateDataToMsgCenter(dbData);
    }

    /**
     * 读取消息中心缓存列表数据,超过15天的自动丢弃掉
     *
     * @return
     */
    public static synchronized List<MsgDetailBean> readMsgListFromCacheData() {
        List<MsgCenterDbData> listCache = MsgCenterDbHelper.queryMsgCenterListData();
        List<MsgDetailBean> list = new ArrayList<>();
        for (MsgCenterDbData dbData : listCache) {
            MsgDetailBean data = makeMsgCenterDbDataToMsgDetailBean(dbData);
            list.add(data);
        }
        return list;
    }

    /**
     * 清除缓存消息中心的缓存列表
     */
    public static synchronized void clearMsgListCache() {
        MsgCenterDbHelper.deleteMsgCenterAllListData();
    }

    /**
     * 把MsgDetailBean转换为MsgCenterDbData
     *
     * @param msgDetailBean
     * @return
     */
    private static MsgCenterDbData makeMsgDetailBeanToMsgCenterDbData(MsgDetailBean msgDetailBean) {
        MsgCenterDbData data = new MsgCenterDbData();
        data.setAutoId(msgDetailBean.getAutoId());
        data.setType(msgDetailBean.getType());
        data.setPushDate(msgDetailBean.getPushDate());
        data.setImageUrl(msgDetailBean.getImageUrl());
        data.setTitle(msgDetailBean.getTitle());
        data.setInfoLinkUrl(msgDetailBean.getInfoLinkUrl());
        data.setNotice(msgDetailBean.getNotice());
        data.setRead(msgDetailBean.isRead());
        return data;
    }

    /**
     * 把MsgCenterDbData转换为MsgDetailBean
     *
     * @param dbData
     * @return
     */
    private static MsgDetailBean makeMsgCenterDbDataToMsgDetailBean(MsgCenterDbData dbData) {
        MsgDetailBean data = new MsgDetailBean();
        data.setAutoId(dbData.getAutoId());
        data.setType(dbData.getType());
        data.setPushDate(dbData.getPushDate());
        data.setImageUrl(dbData.getImageUrl());
        data.setTitle(dbData.getTitle());
        data.setInfoLinkUrl(dbData.getInfoLinkUrl());
        data.setNotice(dbData.getNotice());
        data.setRead(dbData.isRead());
        return data;
    }

}
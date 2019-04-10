package com.hm.iou.msg;

import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.MsgCenterDbData;
import com.hm.iou.msg.bean.HmMsgBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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
    public static synchronized void addMsgListToCache(List<HmMsgBean> list) {
        Flowable.just(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<List<HmMsgBean>, List<MsgCenterDbData>>() {
                    @Override
                    public List<MsgCenterDbData> apply(List<HmMsgBean> list) throws Exception {
                        List<MsgCenterDbData> listMsgCenter = new ArrayList<>();
                        for (HmMsgBean hmMsgBean : list) {
                            MsgCenterDbData dbData = makeMsgDetailBeanToMsgCenterDbData(hmMsgBean);
                            listMsgCenter.add(dbData);
                        }
                        return listMsgCenter;
                    }
                })
                .subscribe(new Consumer<List<MsgCenterDbData>>() {
                    @Override
                    public void accept(List<MsgCenterDbData> msgCenterDbData) throws Exception {
                        MsgCenterDbHelper.addOrUpdateDataToMsgCenter(msgCenterDbData);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 新增或者更新消息都消息中心
     *
     * @param hmMsgBean 需要新增或者更新的item
     */
    public static synchronized void updateMsgItemToCache(HmMsgBean hmMsgBean) {
        MsgCenterDbData dbData = makeMsgDetailBeanToMsgCenterDbData(hmMsgBean);
        MsgCenterDbHelper.addOrUpdateDataToMsgCenter(dbData);
    }

    /**
     * 读取消息中心缓存列表数据,超过15天的自动丢弃掉
     *
     * @return
     */
    public static synchronized List<HmMsgBean> readMsgListFromCacheData() {
        List<MsgCenterDbData> listCache = MsgCenterDbHelper.queryMsgCenterListData();
        List<HmMsgBean> list = new ArrayList<>();
        for (MsgCenterDbData dbData : listCache) {
            HmMsgBean data = makeMsgCenterDbDataToMsgDetailBean(dbData);
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
     * @param hmMsgBean
     * @return
     */
    private static MsgCenterDbData makeMsgDetailBeanToMsgCenterDbData(HmMsgBean hmMsgBean) {
        MsgCenterDbData data = new MsgCenterDbData();
        data.setAutoId(hmMsgBean.getAutoId());
        data.setType(hmMsgBean.getType());
        data.setPushDate(hmMsgBean.getPushDate());
        data.setImageUrl(hmMsgBean.getImageUrl());
        data.setTitle(hmMsgBean.getTitle());
        data.setInfoLinkUrl(hmMsgBean.getInfoLinkUrl());
        data.setNotice(hmMsgBean.getNotice());
        data.setRead(hmMsgBean.isRead());
        return data;
    }

    /**
     * 把MsgCenterDbData转换为MsgDetailBean
     *
     * @param dbData
     * @return
     */
    private static HmMsgBean makeMsgCenterDbDataToMsgDetailBean(MsgCenterDbData dbData) {
        HmMsgBean data = new HmMsgBean();
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
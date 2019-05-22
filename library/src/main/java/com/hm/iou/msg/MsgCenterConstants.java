package com.hm.iou.msg;

/**
 * @author syl
 * @time 2019/4/12 3:02 PM
 */
public class MsgCenterConstants {


    /**
     * 消息中心未读消息数量
     */
    public static final String KEY_UN_READ_MSG_NUM = "msgcenter_key_un_read_msg_num";


    //消息中心模块用的 SharedPreferences 文件名
    public static final String SP_MSG_CENTER = "msgcenter";

    /**
     * 最近一次获取好友的时间
     */
    public static final String KEY_LAST_FRIEND_UPDATE_TIME = "msgcenter_friend_update_time";

    /**
     * 最近一次获取好友申请记录的时间
     */
    public static final String KEY_LAST_FRIEND_APPLY_RECORD_TIME = "msgcenter_apply_record";
    /**
     * 最近一次拉取支付宝回单的时间
     */
    public static final String KEY_LAST_PULL_ALIPAY_LIST_MSG_RECORD_TIME = "msgcenter_pull_alipay_list_time";
    /**
     * 最近一次拉取管家消息的时间
     */
    public static final String KEY_LAST_PULL_HM_LIST_MSG_RECORD_TIME = "msgcenter_pull_hm_list_time";
    /**
     * 最近一次拉取疑似合同消息的时间
     */
    public static final String KEY_LAST_PULL_SIMILARITY_CONTRACT_LIST_MSG_RECORD_TIME = "msgcenter_pull_similarity_contract_list_time";
    /**
     * 最近一次拉取合同消息的时间
     */
    public static final String KEY_LAST_PULL_CONTRACT_LIST_MSG_RECORD_TIME = "msgcenter_contract_list_msg_time";
    /**
     * 最近一次拉取待还提醒消息的时间
     */
    public static final String KEY_LAST_PULL_REMIND_BACK_LIST_MSG_RECORD_TIME = "msgcenter_remind_back_list_msg_time";


    public static final String KEY_DELTE_IM_FRIEND = "msgcenter_delelte_friend";

    // 通过邮箱上传（电子）凭证
    public static final String H5_URL_UPLOAD_PDF_BY_MEAIL = "/apph5/iou-case/alipay-receipt.html";

}

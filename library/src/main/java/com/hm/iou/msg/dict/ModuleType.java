package com.hm.iou.msg.dict;

/**
 * @author syl
 * @time 2019/4/9 10:14 AM
 */
public enum ModuleType {

    CONTRACT_MSG("msg_center_msg_list_header_contract_msg", "合同消息", 100),
    REMIND_BACK_MSG("msg_center_msg_list_header_remind_to_back", "待还提醒", 200),
    SIMILARITY_CONTRACT_MSG("msg_center_msg_list_header_similarity_contract", "疑似合同", 300),
    HM_MSG("msg_center_msg_list_header_hm_msg", "管家消息", 400),
    ALIPAY_MSG("msg_center_msg_list_header_alipay_msg", "支付宝回单", 500),
    NEW_APPLY_FRIEND("msg_center_msg_list_new_apply_friend", "新的朋友", 0);

    private String typeId;
    private String title;
    private int typeValue;

    ModuleType(String typeId, String title, int typeValue) {
        this.typeId = typeId;
        this.title = title;
        this.typeValue = typeValue;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(int typeValue) {
        this.typeValue = typeValue;
    }
}

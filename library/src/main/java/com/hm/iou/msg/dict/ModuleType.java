package com.hm.iou.msg.dict;

/**
 * @author syl
 * @time 2019/4/9 10:14 AM
 */
public enum ModuleType {

    CONTRACT_MSG("msg_center_msg_list_header_contract_msg", "合同消息"),
    REMIND_BACK_MSG("msg_center_msg_list_header_remind_to_back", "待还提醒"),
    SIMILARITY_CONTRACT_MSG("msg_center_msg_list_header_similarity_contract", "疑似合同"),
    HM_MSG("msg_center_msg_list_header_hm_msg", "管家消息"),;

    private String typeId;
    private String title;

    ModuleType(String typeId, String title) {
        this.typeId = typeId;
        this.title = title;
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
}

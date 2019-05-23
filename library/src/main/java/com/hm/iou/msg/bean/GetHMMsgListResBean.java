package com.hm.iou.msg.bean;

import com.hm.iou.database.table.msg.AliPayMsgDbData;
import com.hm.iou.database.table.msg.HmMsgDbData;

import java.util.List;

import lombok.Data;

/**
 * @author syl
 * @time 2019/5/21 11:13 AM
 */
@Data
public class GetHMMsgListResBean {


    /**
     * lastReqDate : 2019-05-22T02:18:45.309Z
     * list : [{"msgId":0,"type":0,"title":"恭喜：关联成功 ","content":"关联合同：2938488588343421 ","jumpUrl":"hmiou://m.54jietiao.com/iou/elec_borrow_detail_v2?justiceId=190423101820000429&iouId=94c3f92f75404193b523633435776327&iou_id=94c3f92f75404193b523633435776327","createTime":"2019-04-24 21:01:15"},{"msgId":0,"type":0,"title":"恭喜：关联成功 ","content":"关联合同：2938488588343421 ","jumpUrl":"hmiou://m.54jietiao.com/iou/elec_borrow_detail_v2?justiceId=190423101820000429&iouId=94c3f92f75404193b523633435776327&iou_id=94c3f92f75404193b523633435776327","createTime":"2019-04-24 21:01:55"},{"msgId":0,"type":0,"title":"恭喜：关联成功 ","content":"关联合同：2938488588343421 ","jumpUrl":"hmiou://m.54jietiao.com/iou/elec_borrow_detail_v2?justiceId=190423101820000429&iouId=94c3f92f75404193b523633435776327&iou_id=94c3f92f75404193b523633435776327","createTime":"2019-04-26 21:01:15"},{"msgId":0,"type":0,"title":"恭喜：关联成功 ","content":"关联合同：2938488588343421 ","jumpUrl":"hmiou://m.54jietiao.com/iou/elec_borrow_detail_v2?justiceId=190423101820000429&iouId=94c3f92f75404193b523633435776327&iou_id=94c3f92f75404193b523633435776327","createTime":"2019-04-28 12:01:15"}]
     */

    private String lastReqDate;
    private List<HmMsgDbData> list;

}

package com.hm.iou.msg.bean;

import com.hm.iou.database.table.msg.SimilarityContractMsgDbData;

import java.util.List;

import lombok.Data;

/**
 * Created by syl on 2019/4/24.
 */

@Data
public class GetSimilarityContractListResBean {


    /**
     * list : [{"amount":"string","borrowerName":"string","createTime":"2019-04-24T01:50:35.106Z","iouKind":0,"iouStatus":0,"jumpUrl":"string","loanerName":"string","returnDate":"2019-04-24T01:50:35.106Z","returnWayDesc":"string","title":"string"}]
     * page : 0
     * size : 0
     * total : 0
     */

    private String lastReqDate;
    private List<SimilarityContractMsgDbData> list;


}

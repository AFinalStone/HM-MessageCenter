package com.hm.iou.msg.bean;

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

    private int page;
    private int size;
    private int total;
    private List<ListBean> list;

    @Data
    public static class ListBean {
        /**
         * amount : string
         * borrowerName : string
         * createTime : 2019-04-24T01:50:35.106Z
         * iouKind : 0
         * iouStatus : 0
         * jumpUrl : string
         * loanerName : string
         * returnDate : 2019-04-24T01:50:35.106Z
         * returnWayDesc : string
         * title : string
         */

        private String amount;
        private String borrowerName;
        private String createTime;
        private int iouKind;
        private int iouStatus;//1=草稿, 2=正式/已签署完, Del(3), Close(4), 5=已结清，12=等待确认，13=超时未签 ,
        private String jumpUrl;
        private String loanerName;
        private String returnDate;
        private String returnWayDesc;
        private String title;

    }
}

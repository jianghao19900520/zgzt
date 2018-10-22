package com.zgzt.pos.node;

import java.util.List;

/**
 * Created by zixing
 * Date 2018/10/22.
 * desc ：
 */

public class PayMangerNode {

    /**
     * code : 0
     * message : null
     * result : {"pageSize":25,"pageIndex":0,"recordIndex":0,"recordCount":1,"list":[{"id":"2vfpqhx93pd","rowNumber":1,"createdTime":1539859667000,"createdId":null,"createdName":null,"updatedTime":null,"updatedId":null,"updatedName":null,"statisticsType":2,"memberId":"2uz8xnmzmrl","orderTotal":134,"orderCount":1,"orderFinanceRebate":13.4,"orderIntegralRebate":0,"statisticsTime":1540205267000,"isDel":0,"sortNo":null}],"pageCount":1,"lastPage":true,"firstPage":true}
     */

    private int code;
    private Object message;
    private ResultBean result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * pageSize : 25
         * pageIndex : 0
         * recordIndex : 0
         * recordCount : 1
         * list : [{"id":"2vfpqhx93pd","rowNumber":1,"createdTime":1539859667000,"createdId":null,"createdName":null,"updatedTime":null,"updatedId":null,"updatedName":null,"statisticsType":2,"memberId":"2uz8xnmzmrl","orderTotal":134,"orderCount":1,"orderFinanceRebate":13.4,"orderIntegralRebate":0,"statisticsTime":1540205267000,"isDel":0,"sortNo":null}]
         * pageCount : 1
         * lastPage : true
         * firstPage : true
         */

        private int pageSize;
        private int pageIndex;
        private int recordIndex;
        private int recordCount;
        private int pageCount;
        private boolean lastPage;
        private boolean firstPage;
        private List<PayMangerItemNode> list;

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public int getRecordIndex() {
            return recordIndex;
        }

        public void setRecordIndex(int recordIndex) {
            this.recordIndex = recordIndex;
        }

        public int getRecordCount() {
            return recordCount;
        }

        public void setRecordCount(int recordCount) {
            this.recordCount = recordCount;
        }

        public int getPageCount() {
            return pageCount;
        }

        public void setPageCount(int pageCount) {
            this.pageCount = pageCount;
        }

        public boolean isLastPage() {
            return lastPage;
        }

        public void setLastPage(boolean lastPage) {
            this.lastPage = lastPage;
        }

        public boolean isFirstPage() {
            return firstPage;
        }

        public void setFirstPage(boolean firstPage) {
            this.firstPage = firstPage;
        }

        public List<PayMangerItemNode> getList() {
            return list;
        }

        public void setList(List<PayMangerItemNode> list) {
            this.list = list;
        }

    }
}

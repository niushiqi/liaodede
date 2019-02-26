package com.dyyj.idd.chatmore.model.network.result;

import java.util.List;

/**
 * Created by Who are you~ on 2018/12/4.
 */

public class FeedBackResult {

    /**
     * errorCode : 200
     * errorMsg :
     * data : {"rs":"1","reason":""}
     */

    private int errorCode;
    private String errorMsg;
    private DataBean data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * rs : 1
         * reason :
         */

        private String rs;
        private String reason;

        public String getRs() {
            return rs;
        }

        public void setRs(String rs) {
            this.rs = rs;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

}


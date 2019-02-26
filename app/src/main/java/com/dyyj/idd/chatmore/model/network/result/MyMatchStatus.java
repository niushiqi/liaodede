package com.dyyj.idd.chatmore.model.network.result;

/**
 * Created by Corey_Jia on 2018/12/18.
 */
public class MyMatchStatus {

    /**
     * errorCode : 200
     * errorMsg :
     * data : {"matchingEnable":"1"}
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
         * matchingEnable : 1
         */

        private String matchingEnable;

        public String getMatchingEnable() {
            return matchingEnable;
        }

        public void setMatchingEnable(String matchingEnable) {
            this.matchingEnable = matchingEnable;
        }
    }
}

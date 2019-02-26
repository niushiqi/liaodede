package com.dyyj.idd.chatmore.model.network.result;

/**
 * Created by Corey_Jia on 2018/12/17.
 */
public class MatchingButtonEntity {


    /**
     * errorCode : 200
     * errorMsg :
     * data : {"textEnable":"1","voiceEnable":"1","textTip":{"title":"随缘匹配","line1":"3分钟随缘，10魔石每次","line2":"今日还剩余2次机会","remainTimes":"2","timeOutButton":"随缘匹配超时"},"voiceTip":{"title":"开启聊天","line3":"聊天听歌打游戏","line4":"3魔石每次","timeOutButton":"语音匹配超时"}}
     */

    private int errorCode;
    private String errorMsg;
    public DataBean data;

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
         * textEnable : 1
         * voiceEnable : 1
         * textTip : {"title":"随缘匹配","line1":"3分钟随缘，10魔石每次","line2":"今日还剩余2次机会","remainTimes":"2","timeOutButton":"随缘匹配超时"}
         * voiceTip : {"title":"开启聊天","line3":"聊天听歌打游戏","line4":"3魔石每次","timeOutButton":"语音匹配超时"}
         */

        private String textEnable;
        private String voiceEnable;
        private TextTipBean textTip;
        private VoiceTipBean voiceTip;

        public String getTextEnable() {
            return textEnable;
        }

        public void setTextEnable(String textEnable) {
            this.textEnable = textEnable;
        }

        public String getVoiceEnable() {
            return voiceEnable;
        }

        public void setVoiceEnable(String voiceEnable) {
            this.voiceEnable = voiceEnable;
        }

        public TextTipBean getTextTip() {
            return textTip;
        }

        public void setTextTip(TextTipBean textTip) {
            this.textTip = textTip;
        }

        public VoiceTipBean getVoiceTip() {
            return voiceTip;
        }

        public void setVoiceTip(VoiceTipBean voiceTip) {
            this.voiceTip = voiceTip;
        }

        public static class TextTipBean {
            /**
             * title : 随缘匹配
             * line1 : 3分钟随缘，10魔石每次
             * line2 : 今日还剩余2次机会
             * remainTimes : 2
             * timeOutButton : 随缘匹配超时
             */

            private String title;
            private String line1;
            private String line2;
            private int remainTimes;
            private String timeOutButton;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getLine1() {
                return line1;
            }

            public void setLine1(String line1) {
                this.line1 = line1;
            }

            public String getLine2() {
                return line2;
            }

            public void setLine2(String line2) {
                this.line2 = line2;
            }

            public int getRemainTimes() {
                return remainTimes;
            }

            public void setRemainTimes(int remainTimes) {
                this.remainTimes = remainTimes;
            }

            public String getTimeOutButton() {
                return timeOutButton;
            }

            public void setTimeOutButton(String timeOutButton) {
                this.timeOutButton = timeOutButton;
            }
        }

        public static class VoiceTipBean {
            /**
             * title : 开启聊天
             * line3 : 聊天听歌打游戏
             * line4 : 3魔石每次
             * timeOutButton : 语音匹配超时
             */

            private String title;
            private String line3;
            private String line4;
            private String timeOutButton;
            private String timeOutTip;

            public String getTimeOutTip() {
                return timeOutTip;
            }

            public void setTimeOutTip(String timeOutTip) {
                this.timeOutTip = timeOutTip;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getLine3() {
                return line3;
            }

            public void setLine3(String line3) {
                this.line3 = line3;
            }

            public String getLine4() {
                return line4;
            }

            public void setLine4(String line4) {
                this.line4 = line4;
            }

            public String getTimeOutButton() {
                return timeOutButton;
            }

            public void setTimeOutButton(String timeOutButton) {
                this.timeOutButton = timeOutButton;
            }
        }
    }
}

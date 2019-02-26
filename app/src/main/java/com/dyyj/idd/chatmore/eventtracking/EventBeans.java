package com.dyyj.idd.chatmore.eventtracking;

/**
 * Created by wangbin on 2018/11/22.
 */

public class EventBeans {
    private String eventName;
    private String extra;

    public EventBeans(String eventName, String extra) {
        this.eventName = eventName;
        this.extra = extra;
    }

    public String getEventName() {
        return eventName;
    }

    public String getExtra() {
        return extra;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}

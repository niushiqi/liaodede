package com.dyyj.idd.chatmore.eventtracking;

/**
 * Created by Corey_Jia on 2018/12/14.
 */
public class EventConstant {
    public interface TAG{
        String TAG_CALLOUTGOING_FRAGMENT = "TAG_CALLOUTGOING_FRAGMENT";
        String TAG_CHAT_ACTIVITY = "TAG_CHAT_ACTIVITY";
        String TAG_OPENCALL_FRAGMENT = "TAG_OPENCALL_FRAGMENT";
        String TAG_MESSAGE_SYSTEM_FRAGMENT = "TAG_MESSAGE_SYSTEM_FRAGMENT";
        String TAG_MESSAGE_UNREAD_COUNT = "TAG_MESSAGE_UNREAD_COUNT";
    }
    public interface WHAT{
        int WHAT_TEXT_MATCHING = 10001;
        int WHAT_CHAT_EXIT = 10002;
        int WHAT_REPORT = 10003;
        int WHAT_REPORT_LIKE_OR_NO = 10004;
        int WHAT_FRIEND_ADD_VOICE_CLICK = 10005;
        int WHAT_MATCHBUTTON = 10006;
        int WHAT_MATCH_STATUS = 10007;
        int WHAT_UNREAD_FRIEND_COUNT = 10008;
        int WHAT_UNREAD_CIRCLE_COUNT = 10009;
        int WHAT_UNREAD_MESSAGE_COUNT = 10010;
        int WHAT_IS_FRIEND = 10011;
        int WHAT_IS_SHOW_ADDFRIEND = 10012;
        int WHAT_TXT_MATCH_FAIL = 10013;
        int WHAT_ADD_FRIEND_AND_EXIT = 10014;
        int HWAT_MESSAGE_END_TIME = 10015;
        int WHAT_INIT_RECYCLER = 10016;
        int WHAT_UNREAD_SQUARE_COUNT = 10017;
    }
}

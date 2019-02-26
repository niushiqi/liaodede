package com.dyyj.idd.chatmore.model.mqtt;

/**
 * Author：LvQingYang
 * Date：2017/8/29
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public class MqttTag {
    public final static int MQTT_STATE_CONNECTED = 1;
    public final static int MQTT_STATE_LOST = 2;
    public final static int MQTT_STATE_FAIL = 3;
    public final static int MQTT_STATE_RECEIVE = 4;
    public final static int MQTT_STATE_SEND_SUCC = 5;
    public final static String MQTT_TOPIC = "/ldd/heartbeat";
    public final static int MQTT_QOS = 2;

    public final static String SUBSCRIBE_NEWBIE_TASK = "newpeople_firstlogin"; //新手任务-首次登录奖励
    public final static String SUBSCRIBE_NEWPEOPLE_FIRSTMATCHING = "newpeople_firstmatching";//新手任务-首次匹配奖励
//    public final static String SUBSCRIBE_LEVEL_UPGRADE = "reward_level"; //等级提升奖励
    public final static String SUBSCRIBE_FIRST_CHAT = "reward_firstchat"; //首次开启聊天奖励
    public final static String SUBSCRIBE_HANG_UP = "reward_hangup"; //挂断后获取奖励
    public final static String SUBSCRIBE_RANG_AWARD = "reward_random"; //随机奖励
    public final static String SUBSCRIBE_COMMON_AWARD = "reward_box"; //固定奖励
    public final static String SUBSCRIBE_VIDEO = "reward_video";//固定奖励
    public final static String SUBSCRIBE_FRIEND_TALK = "friend_talk";//聊天好友通话
    public final static String SUBSCRIBE_FRIENDSHIP_REWARD = "friendship_reward";//一对一消息界面奖励

    public final static String SUBSCRIBE_FRIEND_REQUEST = "friend_request";//好友请求
    public final static String SUBSCRIBE_REQUEST_VIDEO_SWITCH = "talk_switchvideo";//请求切换视频
    public final static String SUBSCRIBE_RESPONSE_VIDEO_SWITCH = "talk_switchvideo_response";//响应切换视频请求
    public final static String SUBSCRIBE_NEWPEOPLE_FIRSTADDFRIEND = "newpeople_firstaddfriend";//首次加好友成功
    public final static String SUBSCRIBE_INVITESUCCESS = "daily_invitesuccess";//邀新成功奖励
    public final static String SUBSCRIBE_VIDEOTWICE = "daily_videotwice";//每日开启两次视频奖励
    public final static String SUBSCRIBE_ADDFRIEND_TWICE = "daily_addfriendtwice";//每日加两次好友奖励
    public final static String SUBSCRIBE_SIGN = "daily_sign";//每日签到奖励
    public final static String SUBSCRIBE_INVITESUM = "achieve_invitesum";//累计邀请人数奖励
    public final static String SUBSCRIBE_INVITE_NOFIRSTINVITE = "invite_nofirstinvite";//非首次拉新
    public final static String SUBSCRIBE_SIGN_OK = "daily_signin";//签到奖励
    public final static String SUBSCRIBE_HEART_BIT = "heartbeat_receipt";//socket心跳回执
    public final static String SUBSCRIBE_FIRST_VIDEO = "newpeople_firstvideo";//首次开启视频
    public final static String SUBSCRIBE_TWICE_MATCHING = "daily_twicematching";//每日两次匹配
    public final static String SUBSCRIBE_REWARD_SCENENTIMACY2 = "reward_scenentimacy";//累计在线两小时
    public final static String SUBSCRIBE_ONLINEONEHOUR = "daily_onlineonehour";//在线一小时
    public final static String SUBSCRIBE_CHANGE_PERSION = "change_persion";//切人通知
    public final static String SUBSCRIBE_AVATAR_FAILER = "slytherin_avatar_authfailed";//头像审核未通过
    public final static String SUBSCRIBE_SWITCH_PERSON = "switch_people_hangup";//切人通知
    public final static String SUBSCRIBE_FREEZE_COIN = "talk_start_freeze_coin";//匹配成功消耗金币

    public final static String SUBSCRIBE_USER_REPORT = "user_report";//被举报人收到
    public final static String SUBSCRIBE_TALK_HANGUP = "talk_hangup";//陌生人挂断电话
    public final static String SUBSCRIBE_OFFLINE = "offline";//强制注销下线
    public final static String SUBSCRIBE_REWARD_SCENENTIMACY = "reward_scenentimacy";//树洞亲密度奖励
    public final static String SUBSCRIBE_INVITE_FIRSTINVITE = "invite_firstinvite";//首次拉新发
    public final static String SUBSCRIBE_NEWPEOPLE_SCENEDURATION = "newpeople_sceneduration";//新手任务-树洞内累积沟通超过2小时
    public final static String SUBSCRIBE_TALK_MATCHING_SWITCHVIDEO = "talk_matching_switchvideo";//匹配通话中请求切换视频
    public final static String SUBSCRIBE_TALK_MATCHING_SWITCHVIDEO_RESPONSE = "talk_matching_switchvideo_response";//匹配通话中响应切换视频

    public final static String SUBSCRIBE_TALK_MATCHING = "talk_matching";//开始匹配
    public final static String SUBSCRIBE_SLYTHERIN_FRAME_HANGUP = "slytherin_frame_hangup";//视频内容违章挂断

    public final static String SUBSCRIBE_GAME_REQUEST = "request_dicegame";//请求玩游戏
    public final static String SUBSCRIBE_GAME_RESULT = "receive_dicegame";//游戏结果
    public final static String SUBSCRIBE_GAME_REFUSE = "reject_dicegame";//
    public final static String SUBSCRIBE_BANK_NEW = "friend_bank_new";//银行奖励提示

    public final static String SUBSCRIBE_TALK_MATCHING_BACKUP = "talk_matching_backup";//通过被选池被叫
    public final static String SUBSCRIBE_REWARD_TALK_MATCHING_BACKUP = "reward_talk_matching_backup";//被叫50金币奖励

    public final static String SUBSCRIBE_MATCHING_BACKUP_CANCEL = "matching_backup_cancel";//备选池呼叫取消

    public final static String SUBSCRIBE_RESET_STATUS = "reset_status";//备选池呼叫,接收方状态重置
    public final static String SUBSCRIBE_MATCHING_FAILED = "matching_failed";//无回执失败

    public final static String TASK_REWARD = "task_reward";
    public final static String LEVEL_UP = "level_up";
    public final static String THREEDAY_SUMMARY = "threeday_summary";

    public final static String SUBSCRIBE_NOTIFY_NEW_MESSAGE = "";

    public final static String SUBSCRIBE_REQUEST_GAME2 = "request_racinggame";//请求开始游戏消息格式
    public final static String SUBSCRIBE_RESPONSE_GAME2 = "receive_racinggame";//B同意后.mqtt通知双方开始游戏 消息格式
    public final static String SUBSCRIBE_REJECT_GAME2 = "reject_racinggame";//B拒绝开始游戏消息格式
    public final static String SUBSCRIBE_RESULT_GAME2 = "showrs_racinggame";//双方都已上报.mqtt下发胜负结果 消息格式

    public final static String SUBSCRIBE_UGC_VOTE = "ugc_vote";
    public final static String SUBSCRIBE_UGC_COMMIT = "ugc_commit";
    public final static String SUBSCRIBE_UGC_TOPIC = "ugc_topic";//收到好友圈 新动态

    public final static String SUBSCRIBE_SYSTEM_MESSAGE = "system_info";

    public final static String SUBSCRIBE_SQUARE_MESSAGE_TASK = "square_message_reward";
    public final static String SUBSCRIBE_REWARD_SQUARE_FIRST_COMMENT = "reward_square_first_comment";

    public final static String SUBSCRIBE_SEND_GIFT_ON_CHAT = "send_gift_on_chat";//聊天界面赠送礼物
}

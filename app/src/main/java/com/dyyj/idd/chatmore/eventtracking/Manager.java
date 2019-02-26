package com.dyyj.idd.chatmore.eventtracking;

import android.widget.Toast;

import com.dyyj.idd.chatmore.app.ChatApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * Created by wangbin on 2018/11/22.
 * 从数据库中读取数据
 * 定时发送网络请求
 */

public class Manager {
    //单例模式
    private static Manager instance = new Manager();

    //定义 埋点消息 数据结构
    //[事件；额外字段]
    //private List<Map<String, Object>> eventListMap = new ArrayList<Map<String, Object>>();
    private List<EventBeans> eventListMap = new ArrayList<EventBeans>();
    private int eventCacheNumber = 0;

    private Manager(){

    }

    public static Manager getInstance() {
        return instance;
    }

    /**
     * 添加埋点
     * @param eventMap
     */
    public void addEvent(EventBeans eventMap) {
        this.eventListMap.add(eventMap);
        //累计埋点数大于eventCacheNumber就发送消息
        if(eventListMap.size() > eventCacheNumber) {
            sendEventTrackingMessage(eventListMap);
        }
    }

    /**
     * 发送信息
     * @return
     */
    private void sendEventTrackingMessage(List<EventBeans> eventListMap) {
        //单独发送每一条埋点消息
        //反向发送保证删除时不会出问题
        for(int i = eventCacheNumber;i >= 0;i--) {
            //调用model发送
            Timber.tag("niushiqi-clickeventtrack").i("name:"+eventListMap.get(i).getEventName()
                    +" extra:"+eventListMap.get(i).getExtra());
            Disposable subscribe = ChatApp.Companion.getInstance().getDataRepository()
                    .postClickEventTrackMessage(eventListMap.get(i).getEventName(),
                            eventListMap.get(i).getExtra()).subscribe(statusResult -> {
                        Timber.tag("niushiqi-clickeventtrack").i("errorCode:"+statusResult.getErrorCode()
                                +" errorMsg:"+statusResult.getErrorMsg());
                        if (statusResult.getErrorCode() == 200) {

                        } else {

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
            new CompositeDisposable().add(subscribe);
            //发送完成后将埋点消息删掉
            eventListMap.remove(i);
        }
    }
}

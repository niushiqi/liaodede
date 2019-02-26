package com.dyyj.idd.chatmore.model.mqtt;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.dyyj.idd.chatmore.BuildConfig;
import com.dyyj.idd.chatmore.app.ChatApp;
import com.gt.common.gtchat.model.network.NetURL;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Author：LvQingYang
 * Date：2017/8/29
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：MQTT操作类
 *
 * @安卓-末班车 豪哥，SIP服务部署好了，端口50060，建了两个用户，
 * 用户名 1111 密码 1111
 * 用户名 2222 密码 2222
 */

public class ManageMqtt {
    private String TAG = "MQTT";

    /**
     * MQTT配置参数
     **/
    public static String host = NetURL.INSTANCE.getMQTT_HOST();
    //public static String host = "api.ddaylove.com";
    public static String port = NetURL.INSTANCE.getMQTT_HOST_PORT();
    public static int socket_port = 51900;
    private static String userID = "test";
    private static String passWord = "test";
    private static String clientID = UUID.randomUUID().toString();

    private BufferedSink mSink;
    private BufferedSource mSource;
    private PrintWriter printWriter;
    private BufferedReader in;
    private ExecutorService mExecutorService = Executors.newCachedThreadPool();
    private String receiveMsg;
    private Map<String, Class> map = new HashMap<>();
    private Socket socket;

    /**
     * MQTT状态信息
     **/
    private boolean isConnect = false;

    /**
     * MQTT支持类
     **/
    private MqttAsyncClient mqttClient = null;

    private MqttListener mMqttListener;

    /**
     * 注册返回类型
     */
    public ManageMqtt initRegisterMessageResult(String type, Class result) {
        map.put(type, result);
        return this;
    }

    /**
     * 获取返回类型
     */
    public Map<String, Class> getMessageResults() {
        return map;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.arg1) {
                case MqttTag.MQTT_STATE_CONNECTED:
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "handleMessage: connected");
                    }
                    mMqttListener.onConnected();
                    break;
                case MqttTag.MQTT_STATE_FAIL:
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "handleMessage: fail");
                    }
                    mMqttListener.onFail();
                    break;
                case MqttTag.MQTT_STATE_LOST:
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "handleMessage: lost");
                    }
                    mMqttListener.onLost();
                    break;
                case MqttTag.MQTT_STATE_RECEIVE:
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "handleMessage: receive");
                    }
                    mMqttListener.onReceive((String) message.obj);
                    break;
                case MqttTag.MQTT_STATE_SEND_SUCC:
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "handleMessage: send");
                    }
                    mMqttListener.onSendSucc();
                    break;
                default:
            }
            return true;
        }
    });

    /**
     * 自带的监听类，判断Mqtt活动变化
     */
    private IMqttActionListener mIMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            isConnect = true;
            Message msg = new Message();
            msg.arg1 = MqttTag.MQTT_STATE_CONNECTED;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            isConnect = false;
            Message msg = new Message();
            msg.arg1 = MqttTag.MQTT_STATE_FAIL;
            mHandler.sendMessage(msg);
        }
    };

    /**
     * 自带的监听回传类
     */
    private MqttCallback mMqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            isConnect = false;
            Message msg = new Message();
            msg.arg1 = MqttTag.MQTT_STATE_LOST;
            mHandler.sendMessage(msg);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Message msg = new Message();
            msg.arg1 = MqttTag.MQTT_STATE_RECEIVE;
            msg.obj = new String(message.getPayload());
            mHandler.sendMessage(msg);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Message msg = new Message();
            msg.arg1 = MqttTag.MQTT_STATE_SEND_SUCC;
            mHandler.sendMessage(msg);
        }
    };

    public ManageMqtt(MqttListener lis) {
        mMqttListener = lis;
    }

    public static void setMqttSetting(String host, String port, String userID, String passWord,
                                      String clientID) {
        ManageMqtt.host = host;
        ManageMqtt.port = port;
        ManageMqtt.userID = userID;
        ManageMqtt.passWord = passWord;
        ManageMqtt.clientID = clientID;
    }

    /**
     * 进行Mqtt连接
     */
    public void connectMqtt() {
        try {
            mqttClient = new MqttAsyncClient("tcp://" + NetURL.INSTANCE.getMQTT_HOST() + ":" + NetURL.INSTANCE.getMQTT_HOST_PORT(),
                    "ClientID" + ManageMqtt.clientID, new MemoryPersistence());
            mqttClient.connect(getOptions(), null, mIMqttActionListener);
            mqttClient.setCallback(mMqttCallback);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开Mqtt连接重新连接
     */
    public void reStartMqtt() {
        disConnectMqtt();
        connectMqtt();
    }

    /**
     * 断开重连socket
     */
    public void reSetSocket() {
        disConnectMqtt();
        disconnect();
        connectMqtt();
        connect();
    }

    /**
     * 断开Mqtt连接
     */
    public void disConnectMqtt() {
        try {
            if (mqttClient != null) {
                mqttClient.disconnect();
                mqttClient = null;
            }
//      if (mSink != null) {
//        try {
//          mSink.close();
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//        mSink = null;
//      }
//      if (mSource != null) {
//        mSource.close();
//        mSource = null;
//      }
//      if (socket != null && !socket.isClosed()) {
//        socket.close();
//      }
            isConnect = false;
        } catch (MqttException e) {
            e.printStackTrace();
        }
//    catch (IOException e) {
//      e.printStackTrace();
//    }
    }

    /**
     * 向Mqtt服务器发送数据
     */
    public void pubMsg(String Topic, String Msg, int Qos) {
        if (!isConnect) {
            Log.d(TAG, "Mqtt连接未打开");
            return;
        }
        try {
            /** Topic,Msg,Qos,Retained**/
            mqttClient.publish(Topic, Msg.getBytes(), Qos, false);
            Log.i("MQTT", "Topic=" + Topic);
            Log.i("MQTT", "Msg=" + Msg);
            Log.i("MQTT", "Qos=" + Qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向Mqtt服务器发送数据
     */
    public void pubMsg(String Topic, byte[] Msg, int Qos) {
        if (!isConnect) {
            Log.d(TAG, "Mqtt连接未打开");
            return;
        }
        try {
            /** Topic,Msg,Qos,Retained**/
            mqttClient.publish(Topic, Msg, Qos, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向Mqtt服务器订阅某一个Topic
     */
    public void subTopic(String Topic, int Qos) {
        if (!isConnect) {
            Log.d(TAG, "Mqtt连接未打开");
            return;
        }
        try {
            mqttClient.subscribe(Topic, Qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置Mqtt的连接信息
     */
    private MqttConnectOptions getOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        //重连不保持状态
        options.setCleanSession(true);
        if (ManageMqtt.userID != null
                && ManageMqtt.userID.length() > 0
                && ManageMqtt.passWord != null
                && ManageMqtt.passWord.length() > 0) {
            //设置服务器账号密码
            options.setUserName(ManageMqtt.userID);
            options.setPassword(ManageMqtt.passWord.toCharArray());
        }
        //设置连接超时时间
        options.setConnectionTimeout(10);
        //设置保持活动时间，超过时间没有消息收发将会触发ping消息确认
        options.setKeepAliveInterval(10);
        return options;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public static String getClientID() {
        return clientID;
    }

    public void connect() {
        mExecutorService.execute(new connectService());
    }

    public void send(String message) {
        mExecutorService.execute(new sendService(message));
    }

    public void disconnect() {
        mExecutorService.execute(new sendService("0"));
        try {
            if (mSink != null) {
                try {
                    mSink.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mSink = null;
            }
            if (mSource != null) {
                mSource.close();
                mSource = null;
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //private class sendService implements Runnable {
    //  private String msg;

    //  sendService(String msg) {
    //    this.msg = msg;
    //  }
    //
    //  @Override
    //  public void run() {
    //    Log.i(TAG, "socket:"+this.msg);
    //    printWriter.println(this.msg+"\r\n");
    //    Log.i(TAG, "socket发送成功");
    //  }
    //}
    //
    //private class connectService implements Runnable {
    //  @Override
    //  public void run() {
    //    try {
    //      Socket socket = new Socket(ManageMqtt.host, ManageMqtt.socket_port);
    //      socket.setSoTimeout(60000);
    //      printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
    //          socket.getOutputStream(), "UTF-8")), true);
    //      in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    //      receiveMsg();
    //    } catch (Exception e) {
    //      Log.e(TAG, ("connectService:" + e.getMessage()));
    //    }
    //  }
    //}
    //
    //private void receiveMsg() {
    //  try {
    //    while (true) {
    //      if ((receiveMsg = in.readLine()) != null) {
    //        Log.d(TAG, "receiveMsg:" + receiveMsg);
    //      }
    //    }
    //  } catch (IOException e) {
    //    Log.e(TAG, "receiveMsg: ");
    //    e.printStackTrace();
    //  }
    //}



    private class sendService implements Runnable {
        private String msg;

        sendService(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                Log.i(TAG, "socket:" + this.msg + "\r\n");
                Log.d("socketheard", "send#" + this.msg);
                if (mSink == null) {
                    return;
                }
                if (mSink.isOpen()) {
                    mSink.writeUtf8(this.msg + "\r\n");
                    mSink.flush();
                }
                //ChatApp.Companion.getInstance().getDataRepository().insertSocketLog()
                Log.i(TAG, "socket发送成功");
            } catch (IOException e) {
                //EventBus.getDefault().post("BufferedSinkIOException");
                Log.i("zhangwj", "java.lang.IOException");
                e.printStackTrace();
            } /*catch (IllegalStateException e) {
                EventBus.getDefault().post("BufferedSinkIllegalStateException");
                Log.i("zhangwj", "java.lang.IllegalStateException");
                e.printStackTrace();
            } catch (NullPointerException e) {
                EventBus.getDefault().post("BufferedSinkNullPointerException");
                Log.i("zhangwj", "java.lang.NullPointerException");
                e.printStackTrace();
            } catch (Exception e) {
                EventBus.getDefault().post("BufferedSinkException");
                Log.i("zhangwj", "java.lang.Exception");
                e.printStackTrace();
            }*/
        }
    }

    private class connectService implements Runnable {
        @Override
        public void run() {
            try {
                if (mSink != null) {
                    return;
                }
                socket = new Socket(NetURL.INSTANCE.getSOCKET_HOST(), NetURL.INSTANCE.getSOCKET_HOST_PORT());
                mSink = Okio.buffer(Okio.sink(socket));
                mSource = Okio.buffer(Okio.source(socket));
//        ChatApp.Companion.getInstance().getDataRepository().pubSocket();
                ChatApp.Companion.getInstance().getDataRepository().startHeardSocket();
                receiveMsg();
            } catch (Exception e) {
                Log.e(TAG, ("connectService:" + e.getMessage()));
            }
        }
    }

    private void receiveMsg() {
        try {
            while (true) {
                for (String receiveMsg; (receiveMsg = mSource.readUtf8Line()) != null; ) {
                    Log.d(TAG, "receiveMsg:" + receiveMsg);
                    Log.d("socketheard", "return#" + receiveMsg);
                    final String finalReceiveMsg = receiveMsg;
                    ChatApp.Companion.getInstance().getDataRepository().setSocketHeardReturnIndex(Long.valueOf(finalReceiveMsg));
                    //if (ChatApp.Companion.getInstance().getDataRepository().getSocketHeardSendIndex() == ChatApp.Companion.getInstance().getDataRepository().getSocketHeardReturnIndex()) {
                    //  ChatApp.Companion.getInstance().getDataRepository().insertSocketLog(host, ChatApp.Companion.getInstance().getDataRepository().getSocketHeadSendStr(), finalReceiveMsg, System.currentTimeMillis(), System.currentTimeMillis());
                    //} else {
                    //  ChatApp.Companion.getInstance().getDataRepository().insertSocketLog(host, "time-out", finalReceiveMsg, System.currentTimeMillis(), System.currentTimeMillis());
                    //}
                    Log.d(TAG, "receiveMsg: " + finalReceiveMsg);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "receiveMsg: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
package com.dyyj.idd.chatmore.weiget;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.app.ChatApp;
import com.dyyj.idd.chatmore.model.mqtt.result.Game2Result;
import com.dyyj.idd.chatmore.model.network.result.Game2RequestResult;
import com.dyyj.idd.chatmore.model.network.result.StartMatchingResult;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class GameWidget2 extends FrameLayout {

    public interface IEndListener {
        void onEnd();
    }

    private IEndListener listener;

    public void setListener(IEndListener listener) {
        this.listener = listener;
    }

    private final Context mContext;
    private CompositeDisposable compositeDisposable;

    public void setToUserInfo(@Nullable StartMatchingResult.Data model) {
        if (model != null && model.getMatchingUserNickname() != null) {
            txt_game_right.setText(model.getMatchingUserNickname());
        }
    }

    private String toUserId;
    private String gameId;
    private Game2Result gameResult;
    private Boolean isGame2Sender = true;

    public void setGameResult(Game2Result gameResult) {
        this.gameResult = gameResult;
        setStatue(STATUE_RESULT);
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public void setIsGame2Sender(String useraId, String userbId) {
        //Game2发起方为user A，Game2接收方为user B
        if(useraId.equals(ChatApp.Companion.getInstance().getDataRepository().getUserid())) {
            isGame2Sender = true;
        } else if (userbId.equals(ChatApp.Companion.getInstance().getDataRepository().getUserid())) {
            isGame2Sender = false;
        } else {
            isGame2Sender = true;
        }
    }

    public static final int STATUE_READY = 0;
    public static final int STATUE_READY_NO_PLAY = 1;
    public static final int STATUE_WAIT_OTHER = 2;
    public static final int STATUE_WAIT_SELF = 3;
    public static final int STATUE_PLAYING = 4;
    public static final int STATUE_RESULT = 5;
    public static final int STATUE_WIN = 6;
    public static final int STATUE_LOSS = 7;
    public static final int STATUE_PING = 8;
    private int STATUE_NOW = STATUE_READY;

    private ConstraintLayout cl_type_game_ready, cl_type_gaming, cl_type_game_waiting, cl_type_game_result;

    private TextView txt_no_play_tip;

    private FrameLayout fl_waiting, fl_waiting_other;
    private TextView txt_waiting_other_num, txt_waiting_num;
    private TextView txt_play, txt_no_play;

    private ConstraintLayout cl_game_win, cl_game_loss, cl_game_ping;
    private ImageView iv_anim_game_left, iv_anim_game_right;
    private TextView txt_game_left, txt_game_right;
    private TextView txt_play_again_win, txt_no_play_win;
    private TextView txt_play_again_loss, txt_no_play_loss;
    private TextView txt_play_again_ping, txt_no_play_ping;
    private TextView txt_result_tip;

    //游戏过程中定义
    private TextView txt_game_nums;
    private ConstraintLayout cl_game_action_left, cl_game_action_center, cl_game_action_right;
    private ImageView iv_game_people_left_down, iv_game_people_left_up;
    private ImageView iv_game_people_center_down, iv_game_people_center_up;
    private ImageView iv_game_people_right_down, iv_game_people_right_up;
    private TextView txtTime, txt_game_num_left, txt_game_num_right;

    public GameWidget2(Context context) {
        this(context, null);
    }

    public GameWidget2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameWidget2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.custom_game_type2, this);
        cl_type_game_ready = findViewById(R.id.cl_type_game_ready);
        cl_type_game_waiting = findViewById(R.id.cl_type_game_waiting);
        cl_type_game_result = findViewById(R.id.cl_type_game_result);
        cl_type_gaming = findViewById(R.id.cl_type_game_gaming);

        initReady();
        initWaiting();
        initResult();
        initGaming();
    }

    private int mClickNums;//点击的总次数

    private void initGaming() {
        txt_game_nums = findViewById(R.id.textView100);
        cl_game_action_left = findViewById(R.id.cs_game_left);
        cl_game_action_center = findViewById(R.id.cs_game_center);
        cl_game_action_right = findViewById(R.id.cs_game_right);
        iv_game_people_left_down = findViewById(R.id.iv_people_left_donw);
        iv_game_people_left_up = findViewById(R.id.iv_people_left_up);
        iv_game_people_center_down = findViewById(R.id.iv_people_center_donw);
        iv_game_people_center_up = findViewById(R.id.iv_people_center_up);
        iv_game_people_right_down = findViewById(R.id.iv_people_right_donw);
        iv_game_people_right_up = findViewById(R.id.iv_people_right_up);
        txtTime = findViewById(R.id.txt_donw_time);
        txt_game_num_left = findViewById(R.id.txt_game_num_left);
        txt_game_num_right = findViewById(R.id.txt_game_num_right);
        cl_game_action_left.setOnClickListener(view -> {
            mBlockingDeque.add(0);//0代表左侧
            mClickNums += 1;
            txt_game_nums.setText(String.valueOf(mClickNums));
        });
        cl_game_action_center.setOnClickListener(view -> {
            mBlockingDeque.add(1);//1代表中侧
            mClickNums += 1;
            txt_game_nums.setText(String.valueOf(mClickNums));
        });
        cl_game_action_right.setOnClickListener(view -> {
            mBlockingDeque.add(2);//2代表右侧
            mClickNums += 1;
            txt_game_nums.setText(String.valueOf(mClickNums));
        });
    }

    private long mSevenSecond = 7 * 10;
    private long mOneSecond = 1 * 10;
    private long mThreeSecond = 3 * 10;
    private long mFiveSecond = 5 * 10;
    private Disposable gameTime;
    private BlockingDeque<Integer> mBlockingDeque = new LinkedBlockingDeque<>();
    private boolean mStartingFlag = false;
    private GameThread mGameThread;

    /**
     * 开始游戏
     */
    public void playGame() {
        setStatue(STATUE_PLAYING);
        mStartingFlag = true;
        mGameThread = new GameThread();
        mGameThread.start();
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.clear();
        gameTime = Flowable.interval(0, 100, TimeUnit.MILLISECONDS).take(mSevenSecond + 1).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long i) throws Exception {
                Timber.tag("niushiqi-bengkui").i("playGame "+ i);
                cl_game_action_left.post(() -> {
                    if (mSevenSecond <= i) {
                        gameTime.dispose();
                        compositeDisposable.clear();
                        endGame();
                        return;
                    } else {
                        long milli = (mSevenSecond - i);
                        txtTime.setText("0" + (milli / 10) + ":" + (milli % 10) + "0");
                    }
                    if (i == 1) {
                        //第一次显示
                        mBlockingDeque.clear();
                        cl_game_action_left.setVisibility(View.VISIBLE);
                        cl_game_action_center.setVisibility(View.INVISIBLE);
                        cl_game_action_right.setVisibility(View.INVISIBLE);
                        iv_game_people_left_down.setVisibility(View.VISIBLE);
                        iv_game_people_left_up.setVisibility(View.INVISIBLE);
                    } else if (i == mOneSecond) {
                        //第二次显示
                        mBlockingDeque.clear();
                        cl_game_action_left.setVisibility(View.INVISIBLE);
                        cl_game_action_center.setVisibility(View.VISIBLE);
                        cl_game_action_right.setVisibility(View.INVISIBLE);
                        iv_game_people_center_down.setVisibility(View.VISIBLE);
                        iv_game_people_center_up.setVisibility(View.INVISIBLE);
                    } else if (i == mThreeSecond) {
                        //第三次显示
                        mBlockingDeque.clear();
                        cl_game_action_left.setVisibility(View.INVISIBLE);
                        cl_game_action_center.setVisibility(View.INVISIBLE);
                        cl_game_action_right.setVisibility(View.VISIBLE);
                        iv_game_people_right_down.setVisibility(View.VISIBLE);
                        iv_game_people_right_up.setVisibility(View.INVISIBLE);
                    } else if (i == mFiveSecond) {
                        //第四次显示
                        mBlockingDeque.clear();
                        cl_game_action_left.setVisibility(View.VISIBLE);
                        cl_game_action_center.setVisibility(View.INVISIBLE);
                        cl_game_action_right.setVisibility(View.INVISIBLE);
                        iv_game_people_left_down.setVisibility(View.VISIBLE);
                        iv_game_people_left_up.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        compositeDisposable.add(gameTime);
    }

    private class GameThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (mStartingFlag) {
                try {
                    Integer index = mBlockingDeque.take();
                    if (index == 0) {
                        //左侧游戏区域
                        ((Activity) mContext).runOnUiThread(() -> {
                            iv_game_people_left_down.setVisibility(View.INVISIBLE);
                            iv_game_people_left_up.setVisibility(View.VISIBLE);
                            cl_game_action_left.postDelayed(() -> {
                                iv_game_people_left_down.setVisibility(View.VISIBLE);
                                iv_game_people_left_up.setVisibility(View.INVISIBLE);
                            }, 250);
                        });
                    } else if (index == 1) {
                        //中侧游戏区域
                        ((Activity) mContext).runOnUiThread(() -> {
                            iv_game_people_center_down.setVisibility(View.INVISIBLE);
                            iv_game_people_center_up.setVisibility(View.VISIBLE);
                            cl_game_action_center.postDelayed(() -> {
                                iv_game_people_center_down.setVisibility(View.VISIBLE);
                                iv_game_people_center_up.setVisibility(View.INVISIBLE);
                            }, 250);
                        });
                    } else if (index == 2) {
                        //右侧游戏区域
                        ((Activity) mContext).runOnUiThread(() -> {
                            iv_game_people_right_down.setVisibility(View.INVISIBLE);
                            iv_game_people_right_up.setVisibility(View.VISIBLE);
                            cl_game_action_right.postDelayed(() -> {
                                iv_game_people_right_down.setVisibility(View.VISIBLE);
                                iv_game_people_right_up.setVisibility(View.INVISIBLE);
                            }, 250);
                        });
                    }
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mStartingFlag = false;
                }
            }
        }
    }

    /**
     * 结束游戏
     */
    private void endGame() {
        mStartingFlag = false;
//        mGameThread.interrupt();
        setStatue(STATUE_RESULT);
        ChatApp.Companion.getInstance().getDataRepository().submitGame2(gameId, String.valueOf(mClickNums), ChatApp.Companion.getInstance().getDataRepository().getUserid()).subscribe(statusResult -> {
            if (statusResult.getErrorCode() == 200) {
                mClickNums = 0;
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Timber.tag("niushiqi-bengkui").i("endGame异常");
            }
        });
    }

    private void initResult() {
        txt_result_tip = findViewById(R.id.textView60);
        cl_game_win = findViewById(R.id.cl_game_win);
        cl_game_loss = findViewById(R.id.cl_game_loss);
        cl_game_ping = findViewById(R.id.cl_game_ping);
        iv_anim_game_left = findViewById(R.id.iv_anim_game_left);
        iv_anim_game_right = findViewById(R.id.iv_anim_game_right);
        txt_game_left = findViewById(R.id.txt_game_left);
        txt_game_right = findViewById(R.id.txt_game_right);
        txt_play_again_win = findViewById(R.id.txt_play_final);
        txt_play_again_loss = findViewById(R.id.txt_play_final_loss);
        txt_play_again_ping = findViewById(R.id.txt_play_final_ping);
        txt_no_play_win = findViewById(R.id.txt_no_play_final);
        txt_no_play_loss = findViewById(R.id.txt_no_play_final_loss);
        txt_no_play_ping = findViewById(R.id.txt_no_play_final_ping);
        txt_play_again_win.setOnClickListener(v -> {
            cl_game_win.setVisibility(View.GONE);
            cl_game_loss.setVisibility(View.GONE);
            cl_game_ping.setVisibility(View.GONE);
//            setStatue(STATUE_READY);
            findViewById(R.id.txt_big).performClick();
        });
        txt_play_again_loss.setOnClickListener(v -> {
            cl_game_win.setVisibility(View.GONE);
            cl_game_loss.setVisibility(View.GONE);
            cl_game_ping.setVisibility(View.GONE);
//            setStatue(STATUE_READY);
            findViewById(R.id.txt_big).performClick();
        });
        txt_play_again_ping.setOnClickListener(view -> {
            cl_game_win.setVisibility(View.GONE);
            cl_game_loss.setVisibility(View.GONE);
            cl_game_ping.setVisibility(View.GONE);
//            setStatue(STATUE_READY);
            findViewById(R.id.txt_big).performClick();
        });
        txt_no_play_win.setOnClickListener(v -> {
            cl_game_win.setVisibility(View.GONE);
            cl_game_loss.setVisibility(View.GONE);
            cl_game_ping.setVisibility(View.GONE);
            setStatue(STATUE_READY);
        });
        txt_no_play_loss.setOnClickListener(v -> {
            cl_game_win.setVisibility(View.GONE);
            cl_game_loss.setVisibility(View.GONE);
            cl_game_ping.setVisibility(View.GONE);
            setStatue(STATUE_READY);
        });
        txt_no_play_ping.setOnClickListener(v -> {
            cl_game_win.setVisibility(View.GONE);
            cl_game_loss.setVisibility(View.GONE);
            cl_game_ping.setVisibility(View.GONE);
            setStatue(STATUE_READY);
        });
    }

    private void initWaiting() {
        fl_waiting = findViewById(R.id.fl_waiting);
        fl_waiting_other = findViewById(R.id.fl_waiting_other);
        txt_waiting_num = findViewById(R.id.txt_waiting_num);
        txt_waiting_other_num = findViewById(R.id.txt_waiting_other_num);
        txt_play = findViewById(R.id.txt_play);
        txt_no_play = findViewById(R.id.txt_no_play);
        txt_play.setOnClickListener(v -> {
            ChatApp.Companion.getInstance().getDataRepository().responseGame2(toUserId, gameId, "1", ChatApp.Companion.getInstance().getDataRepository().getUserid()).subscribe(statusResult -> {
                if (statusResult.getErrorCode() == 200) {

                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Timber.tag("niushiqi-bengkui").i("initWaiting异常");
                }
            });
        });
        txt_no_play.setOnClickListener(v -> {
            ChatApp.Companion.getInstance().getDataRepository().responseGame2(toUserId, gameId, "0", ChatApp.Companion.getInstance().getDataRepository().getUserid()).subscribe(statusResult -> {
                if (statusResult.getErrorCode() == 200) {
                    setStatue(STATUE_READY);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Timber.tag("niushiqi-bengkui").i("initWaiting异常");
                }
            });
        });
    }

    private void initReady() {
        txt_no_play_tip = findViewById(R.id.txt_no_play_tip);
        findViewById(R.id.txt_big).setOnClickListener(v -> {
            ChatApp.Companion.getInstance().getDataRepository().requestGame2(toUserId, ChatApp.Companion.getInstance().getDataRepository().getUserid()).subscribe((Consumer<Game2RequestResult>) gameHttpRequestResult -> {
                if (gameHttpRequestResult.getErrorCode() == 200) {
                    gameId = String.valueOf(gameHttpRequestResult.getData().getGameId());
                    setStatue(STATUE_WAIT_OTHER);
                    startTime();
                } else {
                    Toast.makeText(getContext(), gameHttpRequestResult.getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Timber.tag("niushiqi-bengkui").i("initReady异常");
                }
            });
        });
    }

    public void setStatue(int statue) {
        post(() -> {
            STATUE_NOW = statue;
            switch (statue) {
                case STATUE_READY:
                    if (subscribTime != null && !subscribTime.isDisposed()) {
                        subscribTime.dispose();
                    }
                    cl_type_game_ready.setVisibility(View.VISIBLE);
                    cl_type_game_waiting.setVisibility(View.GONE);
                    cl_type_gaming.setVisibility(View.GONE);
                    cl_type_game_result.setVisibility(View.GONE);
                    cl_game_win.setVisibility(View.GONE);
                    cl_game_loss.setVisibility(View.GONE);
                    cl_game_ping.setVisibility(View.GONE);
                    txt_no_play_tip.setVisibility(View.GONE);
                    break;
                case STATUE_READY_NO_PLAY:
                    cl_type_game_ready.setVisibility(View.VISIBLE);
                    cl_type_game_waiting.setVisibility(View.GONE);
                    cl_type_gaming.setVisibility(View.GONE);
                    cl_type_game_result.setVisibility(View.GONE);
                    cl_game_win.setVisibility(View.GONE);
                    cl_game_loss.setVisibility(View.GONE);
                    cl_game_ping.setVisibility(View.GONE);
                    txt_no_play_tip.setVisibility(View.VISIBLE);
                    break;
                case STATUE_WAIT_OTHER:
                    cl_type_game_ready.setVisibility(View.GONE);
                    cl_type_game_waiting.setVisibility(View.VISIBLE);
                    cl_type_gaming.setVisibility(View.GONE);
                    cl_type_game_result.setVisibility(View.GONE);
                    cl_game_win.setVisibility(View.GONE);
                    cl_game_loss.setVisibility(View.GONE);
                    cl_game_ping.setVisibility(View.GONE);
                    fl_waiting_other.setVisibility(View.VISIBLE);
                    fl_waiting.setVisibility(View.GONE);
                    break;
                case STATUE_WAIT_SELF:
                    cl_type_game_ready.setVisibility(View.GONE);
                    cl_type_game_waiting.setVisibility(View.VISIBLE);
                    cl_type_gaming.setVisibility(View.GONE);
                    cl_type_game_result.setVisibility(View.GONE);
                    cl_game_win.setVisibility(View.GONE);
                    cl_game_loss.setVisibility(View.GONE);
                    cl_game_ping.setVisibility(View.GONE);
                    fl_waiting_other.setVisibility(View.GONE);
                    fl_waiting.setVisibility(View.VISIBLE);
                    break;
                case STATUE_PLAYING:
                    if (subscribTime != null && !subscribTime.isDisposed()) {
                        subscribTime.dispose();
                    }
                    cl_type_game_ready.setVisibility(View.GONE);
                    cl_type_game_waiting.setVisibility(View.GONE);
                    cl_type_game_result.setVisibility(View.GONE);
                    cl_type_gaming.setVisibility(View.VISIBLE);
                    cl_game_win.setVisibility(View.GONE);
                    cl_game_loss.setVisibility(View.GONE);
                    cl_game_ping.setVisibility(View.GONE);
                    break;
                case STATUE_RESULT:
                    cl_type_game_ready.setVisibility(View.GONE);
                    cl_type_game_waiting.setVisibility(View.GONE);
                    cl_type_gaming.setVisibility(View.GONE);
                    cl_type_game_result.setVisibility(View.VISIBLE);
                    txt_result_tip.setVisibility(View.VISIBLE);
                    txt_game_num_left.setText(String.valueOf(mClickNums));
                    txt_game_num_right.setText("?");
                    if (gameResult != null) {
                        //左侧为自己分数，右侧为对方分数
                        if(isGame2Sender) {
                            //自己是Game2发起方
                            txt_game_num_left.setText(gameResult.getANum());
                            txt_game_num_right.setText(gameResult.getBNum());
                            cl_type_game_result.postDelayed(() -> {
                                txt_result_tip.setVisibility(View.INVISIBLE);
                                if (Integer.valueOf(gameResult.getANum()) > Integer.valueOf(gameResult.getBNum())) {
                                    setStatue(STATUE_WIN);
                                } else if (Integer.valueOf(gameResult.getANum()) < Integer.valueOf(gameResult.getBNum())) {
                                    setStatue(STATUE_LOSS);
                                } else {
                                    setStatue(STATUE_PING);
                                }
                                if (listener != null) {
                                    listener.onEnd();
                                }
                            }, 2000);
                        } else {
                            //自己是Game2接收方
                            txt_game_num_left.setText(gameResult.getBNum());
                            txt_game_num_right.setText(gameResult.getANum());
                            cl_type_game_result.postDelayed(() -> {
                                txt_result_tip.setVisibility(View.INVISIBLE);
                                if (Integer.valueOf(gameResult.getBNum()) > Integer.valueOf(gameResult.getANum())) {
                                    setStatue(STATUE_WIN);
                                } else if (Integer.valueOf(gameResult.getBNum()) < Integer.valueOf(gameResult.getANum())) {
                                    setStatue(STATUE_LOSS);
                                } else {
                                    setStatue(STATUE_PING);
                                }
                                if (listener != null) {
                                    listener.onEnd();
                                }
                            }, 2000);
                        }
                    }
                    break;
                case STATUE_WIN:
                    cl_type_game_ready.setVisibility(View.GONE);
                    cl_type_game_waiting.setVisibility(View.GONE);
                    cl_type_game_result.setVisibility(View.VISIBLE);
                    cl_type_gaming.setVisibility(View.GONE);
                    cl_game_win.setVisibility(View.VISIBLE);
                    cl_game_loss.setVisibility(View.GONE);
                    cl_game_ping.setVisibility(View.GONE);
                    break;
                case STATUE_LOSS:
                    cl_type_game_ready.setVisibility(View.GONE);
                    cl_type_game_waiting.setVisibility(View.GONE);
                    cl_type_game_result.setVisibility(View.VISIBLE);
                    cl_type_gaming.setVisibility(View.GONE);
                    cl_game_win.setVisibility(View.GONE);
                    cl_game_loss.setVisibility(View.VISIBLE);
                    cl_game_ping.setVisibility(View.GONE);
                    break;
                case STATUE_PING:
                    cl_type_game_ready.setVisibility(View.GONE);
                    cl_type_game_waiting.setVisibility(View.GONE);
                    cl_type_game_result.setVisibility(View.VISIBLE);
                    cl_type_gaming.setVisibility(View.GONE);
                    cl_game_win.setVisibility(View.GONE);
                    cl_game_loss.setVisibility(View.GONE);
                    cl_game_ping.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private Disposable subscribTime;
//    private CompositeDisposable compositeDisposable;

    public void startWaitingSelf() {
        setStatue(STATUE_WAIT_SELF);
        startTime();
    }

    //开始倒计时
    public void startTime() {
        if (subscribTime != null && !subscribTime.isDisposed()) {
            subscribTime.dispose();
        }
        gameResult = null;
        mClickNums = 0;
        subscribTime = Flowable.interval(0, 1, TimeUnit.SECONDS).observeOn(Schedulers.io()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long i) throws Exception {
                if (i > 10) {
                    setStatue(STATUE_READY);
                    //Toast.makeText(mContext, "网络不好，请重新发起", Toast.LENGTH_SHORT).show();
                    gameResult = null;
                    mClickNums = 0;
                    subscribTime.dispose();
                } else if (STATUE_NOW == STATUE_WAIT_SELF && i > 4) {
                    ChatApp.Companion.getInstance().getDataRepository().responseGame2(toUserId, gameId, "1", ChatApp.Companion.getInstance().getDataRepository().getUserid()).subscribe(statusResult -> {
                        if (statusResult.getErrorCode() == 200) {

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Timber.tag("niushiqi-bengkui").i("拼手速responseGame2崩溃");
                        }
                    });
                } else {
                    post(() -> {
                        if (5 - i >= 0) {
                            txt_waiting_num.setText(String.valueOf(5 - i));
                            txt_waiting_other_num.setText(String.valueOf(5 - i));
                        }
                    });
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Timber.tag("niushiqi-bengkui").i("startTime 崩溃");
            }
        });
    }

}

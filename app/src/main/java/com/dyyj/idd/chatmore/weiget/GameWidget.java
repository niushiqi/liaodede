package com.dyyj.idd.chatmore.weiget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.app.ChatApp;
import com.dyyj.idd.chatmore.model.mqtt.result.GameResult;
import com.dyyj.idd.chatmore.model.network.result.GameHttpRequestResult;
import com.dyyj.idd.chatmore.model.network.result.StartMatchingResult;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class GameWidget extends FrameLayout {

    public void setToUserInfo(@Nullable StartMatchingResult.Data model) {
      if (model != null && model.getMatchingUserNickname() != null) {
        txt_game_right.setText(model.getMatchingUserNickname());
      }
    }

    public interface IEndListener {
        void onEnd();
    }

    private IEndListener listener;

    public void setListener(IEndListener listener) {
        this.listener = listener;
    }

    //    public boolean isBigWin;
    private boolean isMyBig;
    private String toUserId;
    private String gameId;
    private GameResult gameResult;

    public void setGameResult(GameResult gameResult) {
        this.gameResult = gameResult;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public void setMyBig(boolean myBig) {
        isMyBig = myBig;
        txtDaxiaoTitle.setText(isMyBig ? "本局比大" : "本局比小");
        txtDaxiaoTitle2.setText(isMyBig ? "本局比大" : "本局比小");
    }

    public static final int STATUE_READY = 0;
    public static final int STATUE_READY_NO_PLAY = 1;
    public static final int STATUE_WAIT_OTHER = 2;
    public static final int STATUE_WAIT_SELF = 3;
    public static final int STATUE_PLAYING = 4;
    public static final int STATUE_RESULT = 5;
    public static final int STATUE_WIN = 6;
    public static final int STATUE_LOSS = 7;
    private int STATUE_NOW = STATUE_READY;

    private ConstraintLayout cl_type_game_ready, cl_type_game_waiting, cl_type_game_result;

    private TextView txt_no_play_tip;

    private TextView txtDaxiaoTitle, txtDaxiaoTitle2;
    private FrameLayout fl_waiting, fl_waiting_other;
    private TextView txt_waiting_other_num, txt_waiting_num;
    private TextView txt_play, txt_no_play;

    private ConstraintLayout cl_game_win, cl_game_loss;
    private ImageView iv_anim_game_left, iv_anim_game_right;
    private TextView txt_game_left, txt_game_right;
    private TextView txt_play_again_win, txt_no_play_win;
    private TextView txt_play_again_loss, txt_no_play_loss;

    public GameWidget(Context context) {
        this(context, null);
    }

    public GameWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.custom_game_type, this);
        cl_type_game_ready = findViewById(R.id.cl_type_game_ready);
        cl_type_game_waiting = findViewById(R.id.cl_type_game_waiting);
        cl_type_game_result = findViewById(R.id.cl_type_game_result);

        initReady();
        initWaiting();
        initResult();
    }

    private void initResult() {
        txtDaxiaoTitle2 = findViewById(R.id.textView60);
        cl_game_win = findViewById(R.id.cl_game_win);
        cl_game_loss = findViewById(R.id.cl_game_loss);
        iv_anim_game_left = findViewById(R.id.iv_anim_game_left);
        iv_anim_game_right = findViewById(R.id.iv_anim_game_right);
        txt_game_left = findViewById(R.id.txt_game_left);
        txt_game_right = findViewById(R.id.txt_game_right);
        txt_play_again_win = findViewById(R.id.txt_play_final);
        txt_play_again_loss = findViewById(R.id.txt_play_final_loss);
        txt_no_play_win = findViewById(R.id.txt_no_play_final);
        txt_no_play_loss = findViewById(R.id.txt_no_play_final_loss);
        txt_play_again_win.setOnClickListener(v -> {
            cl_game_win.setVisibility(View.GONE);
            cl_game_loss.setVisibility(View.GONE);
            setStatue(STATUE_READY);
            if (isMyBig) {
                findViewById(R.id.txt_big).performClick();
            } else {
                findViewById(R.id.txt_small).performClick();
            }
        });
        txt_play_again_loss.setOnClickListener(v -> {
            cl_game_win.setVisibility(View.GONE);
            cl_game_loss.setVisibility(View.GONE);
            setStatue(STATUE_READY);
            if (isMyBig) {
                findViewById(R.id.txt_big).performClick();
            } else {
                findViewById(R.id.txt_small).performClick();
            }
        });
        txt_no_play_win.setOnClickListener(v -> {
            cl_game_win.setVisibility(View.GONE);
            cl_game_loss.setVisibility(View.GONE);
            setStatue(STATUE_READY);
        });
        txt_no_play_loss.setOnClickListener(v -> {
            cl_game_win.setVisibility(View.GONE);
            cl_game_loss.setVisibility(View.GONE);
            setStatue(STATUE_READY);
        });
    }

    private void initWaiting() {
        txtDaxiaoTitle = findViewById(R.id.textView46);
        fl_waiting = findViewById(R.id.fl_waiting);
        fl_waiting_other = findViewById(R.id.fl_waiting_other);
        txt_waiting_num = findViewById(R.id.txt_waiting_num);
        txt_waiting_other_num = findViewById(R.id.txt_waiting_other_num);
        txt_play = findViewById(R.id.txt_play);
        txt_no_play = findViewById(R.id.txt_no_play);
        txt_play.setOnClickListener(v -> {
//            setStatue(STATUE_PLAYING);
            ChatApp.Companion.getInstance().getDataRepository().gameAction(toUserId, gameId, "1").subscribe(statusResult -> {
                if (statusResult.getErrorCode() == 200) {

                }
            });
        });
        txt_no_play.setOnClickListener(v -> {
            ChatApp.Companion.getInstance().getDataRepository().gameAction(toUserId, gameId, "0").subscribe(statusResult -> {
                if (statusResult.getErrorCode() == 200) {
                    setStatue(STATUE_READY);
                }
            });
        });
    }

    private void initReady() {
        txt_no_play_tip = findViewById(R.id.txt_no_play_tip);
        findViewById(R.id.txt_big).setOnClickListener(v -> {
            txtDaxiaoTitle.setText("本局比大");
            txtDaxiaoTitle2.setText("本局比大");
            isMyBig = true;
//            Disposable subscriber = ChatApp.Companion.getInstance().getDataRepository().gameStart(toUserId, "2").subscribe(statusResult -> {
//                if (statusResult.getErrorCode() == 200) {
//                    gameId = statusResult.getData().getGameId();
//                    setStatue(STATUE_WAIT_OTHER);
//                    startTime();
//                }
//            });
//            new CompositeDisposable().add(subscriber);
            Disposable disposable = ChatApp.Companion.getInstance().getDataRepository().gameStart(toUserId, "2").subscribe(new Consumer<GameHttpRequestResult>() {
                @Override
                public void accept(GameHttpRequestResult gameHttpRequestResult) throws Exception {
                    if (gameHttpRequestResult.getErrorCode() == 200) {
                        gameId = gameHttpRequestResult.getData().getGameId();
                        setStatue(STATUE_WAIT_OTHER);
                        startTime();
                    } else {
                        Toast.makeText(getContext(), gameHttpRequestResult.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Toast.makeText(getContext(), "数据异常", Toast.LENGTH_SHORT).show();
                }
            });
            new CompositeDisposable().add(disposable);
        });
        findViewById(R.id.txt_small).setOnClickListener(v -> {
            txtDaxiaoTitle.setText("本局比小");
            txtDaxiaoTitle2.setText("本局比小");
            isMyBig = false;
            Disposable subscribe = ChatApp.Companion.getInstance().getDataRepository().gameStart(toUserId, "1").subscribe(statusResult -> {
                if (statusResult.getErrorCode() == 200) {
                    gameId = statusResult.getData().getGameId();
                    setStatue(STATUE_WAIT_OTHER);
                    startTime();
                } else {
                    Toast.makeText(getContext(), statusResult.getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Toast.makeText(getContext(), "数据异常", Toast.LENGTH_SHORT).show();
                }
            });
            new CompositeDisposable().add(subscribe);
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
                    cl_type_game_result.setVisibility(View.GONE);
                    cl_game_win.setVisibility(View.GONE);
                    cl_game_loss.setVisibility(View.GONE);
                    txt_no_play_tip.setVisibility(View.GONE);
                    break;
                case STATUE_READY_NO_PLAY:
                    cl_type_game_ready.setVisibility(View.VISIBLE);
                    cl_type_game_waiting.setVisibility(View.GONE);
                    cl_type_game_result.setVisibility(View.GONE);
                    cl_game_win.setVisibility(View.GONE);
                    cl_game_loss.setVisibility(View.GONE);
                    txt_no_play_tip.setVisibility(View.VISIBLE);
                    break;
                case STATUE_WAIT_OTHER:
                    cl_type_game_ready.setVisibility(View.GONE);
                    cl_type_game_waiting.setVisibility(View.VISIBLE);
                    cl_type_game_result.setVisibility(View.GONE);
                    cl_game_win.setVisibility(View.GONE);
                    cl_game_loss.setVisibility(View.GONE);
                    fl_waiting_other.setVisibility(View.VISIBLE);
                    fl_waiting.setVisibility(View.GONE);
                    break;
                case STATUE_WAIT_SELF:
                    cl_type_game_ready.setVisibility(View.GONE);
                    cl_type_game_waiting.setVisibility(View.VISIBLE);
                    cl_type_game_result.setVisibility(View.GONE);
                    cl_game_win.setVisibility(View.GONE);
                    cl_game_loss.setVisibility(View.GONE);
                    fl_waiting_other.setVisibility(View.GONE);
                    fl_waiting.setVisibility(View.VISIBLE);
                    break;
                case STATUE_PLAYING:
                    if (subscribTime != null && !subscribTime.isDisposed()) {
                        subscribTime.dispose();
                    }
                    cl_type_game_ready.setVisibility(View.GONE);
                    cl_type_game_waiting.setVisibility(View.GONE);
                    cl_type_game_result.setVisibility(View.VISIBLE);
                    cl_game_win.setVisibility(View.GONE);
                    cl_game_loss.setVisibility(View.GONE);
                    iv_anim_game_left.setImageResource(R.drawable.anim_shaizi_playing);
                    AnimationDrawable animationDrawableLeft = (AnimationDrawable) iv_anim_game_left.getDrawable();
                    iv_anim_game_right.setImageResource(R.drawable.anim_shaizi_playing);
                    AnimationDrawable animationDrawableRight = (AnimationDrawable) iv_anim_game_right.getDrawable();
                    animationDrawableLeft.start();
                    animationDrawableRight.start();
                    cl_type_game_result.postDelayed(() -> {
//                        if (GameWidget.this.isShown()) {
                        setStatue(STATUE_RESULT);
                        animationDrawableLeft.stop();
                        animationDrawableRight.stop();
//                        }
                    }, 3000);
                    break;
                case STATUE_RESULT:
                    cl_type_game_ready.setVisibility(View.GONE);
                    cl_type_game_waiting.setVisibility(View.GONE);
                    cl_type_game_result.setVisibility(View.VISIBLE);

                    // TODO: 2018/10/18   玩筛子崩溃，角标越界
                    String[] aNums = gameResult.getANum().split(",");
                    String[] bNums = gameResult.getBNum().split(",");
                    if (aNums.length < 2 || bNums.length < 2) {
                        setStatue(STATUE_READY);
                        return;
                    }
                    String userAid = gameResult.getANum().split(",")[0];
                    String userAnum = gameResult.getANum().split(",")[1];
                    String userBid = gameResult.getBNum().split(",")[0];
                    String userBnum = gameResult.getBNum().split(",")[1];
                    if (listener != null) {
                        listener.onEnd();
                    }
                    if (TextUtils.equals(userAid, ChatApp.Companion.getInstance().getDataRepository().getUserid())) {
//                        Toast.makeText(getContext(), "win left:" + userAnum + "right:" + userBnum, Toast.LENGTH_SHORT).show();
                        if (TextUtils.equals(userAnum, "1")) {
                            iv_anim_game_left.setImageResource(R.drawable.ic_shaizi_1);
                        } else if (TextUtils.equals(userAnum, "2")) {
                            iv_anim_game_left.setImageResource(R.drawable.ic_shaizi_2);
                        } else if (TextUtils.equals(userAnum, "3")) {
                            iv_anim_game_left.setImageResource(R.drawable.ic_shaizi_3);
                        } else if (TextUtils.equals(userAnum, "4")) {
                            iv_anim_game_left.setImageResource(R.drawable.ic_shaizi_4);
                        } else if (TextUtils.equals(userAnum, "5")) {
                            iv_anim_game_left.setImageResource(R.drawable.ic_shaizi_5);
                        } else if (TextUtils.equals(userAnum, "6")) {
                            iv_anim_game_left.setImageResource(R.drawable.ic_shaizi_6);
                        }
                        if (TextUtils.equals(userBnum, "1")) {
                            iv_anim_game_right.setImageResource(R.drawable.ic_shaizi_1);
                        } else if (TextUtils.equals(userBnum, "2")) {
                            iv_anim_game_right.setImageResource(R.drawable.ic_shaizi_2);
                        } else if (TextUtils.equals(userBnum, "3")) {
                            iv_anim_game_right.setImageResource(R.drawable.ic_shaizi_3);
                        } else if (TextUtils.equals(userBnum, "4")) {
                            iv_anim_game_right.setImageResource(R.drawable.ic_shaizi_4);
                        } else if (TextUtils.equals(userBnum, "5")) {
                            iv_anim_game_right.setImageResource(R.drawable.ic_shaizi_5);
                        } else if (TextUtils.equals(userBnum, "6")) {
                            iv_anim_game_right.setImageResource(R.drawable.ic_shaizi_6);
                        }
                    } else {
//                        Toast.makeText(getContext(), "loss left:" + userBnum + "right:" + userAnum, Toast.LENGTH_SHORT).show();
                        if (TextUtils.equals(userAnum, "1")) {
                            iv_anim_game_right.setImageResource(R.drawable.ic_shaizi_1);
                        } else if (TextUtils.equals(userAnum, "2")) {
                            iv_anim_game_right.setImageResource(R.drawable.ic_shaizi_2);
                        } else if (TextUtils.equals(userAnum, "3")) {
                            iv_anim_game_right.setImageResource(R.drawable.ic_shaizi_3);
                        } else if (TextUtils.equals(userAnum, "4")) {
                            iv_anim_game_right.setImageResource(R.drawable.ic_shaizi_4);
                        } else if (TextUtils.equals(userAnum, "5")) {
                            iv_anim_game_right.setImageResource(R.drawable.ic_shaizi_5);
                        } else if (TextUtils.equals(userAnum, "6")) {
                            iv_anim_game_right.setImageResource(R.drawable.ic_shaizi_6);
                        }
                        if (TextUtils.equals(userBnum, "1")) {
                            iv_anim_game_left.setImageResource(R.drawable.ic_shaizi_1);
                        } else if (TextUtils.equals(userBnum, "2")) {
                            iv_anim_game_left.setImageResource(R.drawable.ic_shaizi_2);
                        } else if (TextUtils.equals(userBnum, "3")) {
                            iv_anim_game_left.setImageResource(R.drawable.ic_shaizi_3);
                        } else if (TextUtils.equals(userBnum, "4")) {
                            iv_anim_game_left.setImageResource(R.drawable.ic_shaizi_4);
                        } else if (TextUtils.equals(userBnum, "5")) {
                            iv_anim_game_left.setImageResource(R.drawable.ic_shaizi_5);
                        } else if (TextUtils.equals(userBnum, "6")) {
                            iv_anim_game_left.setImageResource(R.drawable.ic_shaizi_6);
                        }
                    }
                    cl_type_game_result.postDelayed(() -> {
                        if (gameResult.getRs() == 1) {
//                            if (TextUtils.equals(userAid, ChatApp.Companion.getInstance().getDataRepository().getUserid())) {
                            setStatue(STATUE_WIN);
                        } else {
                            setStatue(STATUE_LOSS);
                        }
//                        }
                    }, 2000);
                    break;
                case STATUE_WIN:
                    cl_type_game_ready.setVisibility(View.GONE);
                    cl_type_game_waiting.setVisibility(View.GONE);
                    cl_type_game_result.setVisibility(View.VISIBLE);
                    cl_game_win.setVisibility(View.VISIBLE);
                    cl_game_loss.setVisibility(View.GONE);
                    break;
                case STATUE_LOSS:
                    cl_type_game_ready.setVisibility(View.GONE);
                    cl_type_game_waiting.setVisibility(View.GONE);
                    cl_type_game_result.setVisibility(View.VISIBLE);
                    cl_game_win.setVisibility(View.GONE);
                    cl_game_loss.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private Disposable subscribTime;
    private CompositeDisposable compositeDisposable;

    public void startWaitingSelf() {
        setStatue(STATUE_WAIT_SELF);
        startTime();
    }

    //开始倒计时
    public void startTime() {
        if (subscribTime != null && !subscribTime.isDisposed()) {
            subscribTime.dispose();
        }
        compositeDisposable = new CompositeDisposable();
        subscribTime = Flowable.interval(0, 1, TimeUnit.SECONDS).observeOn(Schedulers.io()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long i) throws Exception {
//                if (GameWidget.this.isShown()) {
                if (i > 8) {
                    if (STATUE_NOW != STATUE_PLAYING || STATUE_NOW != STATUE_RESULT) {
                        ChatApp.Companion.getInstance().getDataRepository().getGameInfo(gameId).subscribe(gameInfoResult -> {
                            if (gameInfoResult.getErrorCode() == 200) {

                                if (TextUtils.equals(gameInfoResult.getData().getRs(), "0")) {
                                    Toast.makeText(getContext(), "网络不稳定，重新发起吧！", Toast.LENGTH_SHORT).show();
                                    setStatue(STATUE_READY);
                                    subscribTime.dispose();
                                } else if (TextUtils.equals(gameInfoResult.getData().getRs(), "1")) {
                                    setStatue(STATUE_READY);
                                } else {
                                    gameId = gameInfoResult.getData().getGameId();
                                    GameResult gameResult = new GameResult();
                                    gameResult.setGameId(Integer.valueOf(gameInfoResult.getData().getGameId()));
//                                    gameResult.setRs(Integer.valueOf(gameInfoResult.getData().getRs()));
                                    gameResult.setRs(TextUtils.equals(gameInfoResult.getData().getRs(), "2") ? 1 : 0);
                                    setGameResult(gameResult);
                                    setStatue(STATUE_PLAYING);
                                }
                            } else {
                                setStatue(STATUE_READY);
                            }
                        }, throwable -> {
                            setStatue(STATUE_READY);
                        });
                    }
                    subscribTime.dispose();
                } else if (STATUE_NOW == STATUE_WAIT_SELF && i > 4) {
                    ChatApp.Companion.getInstance().getDataRepository().gameAction(toUserId, gameId, "1").subscribe(statusResult -> {
                        if (statusResult.getErrorCode() == 200) {

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
//                } else {
//                    compositeDisposable.clear();
//                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Timber.tag("niushiqi-bengkui").i("startTime 崩溃");
            }
        });
        compositeDisposable.add(subscribTime);
    }

}

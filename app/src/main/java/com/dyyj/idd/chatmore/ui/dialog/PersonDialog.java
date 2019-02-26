package com.dyyj.idd.chatmore.ui.dialog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.app.ChatApp;
import com.dyyj.idd.chatmore.eventtracking.EventBeans;
import com.dyyj.idd.chatmore.model.mqtt.result.SignResult;
import com.dyyj.idd.chatmore.model.network.result.MyMatchStatus;
import com.dyyj.idd.chatmore.model.network.result.RelationResult;
import com.dyyj.idd.chatmore.model.network.result.SquareMessageConsumeStoneResult;
import com.dyyj.idd.chatmore.model.network.result.StartSquareMessageResult;
import com.dyyj.idd.chatmore.model.network.result.UserDetailInfoResult;
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity;
import com.dyyj.idd.chatmore.ui.task.activity.ChatActivity;
import com.dyyj.idd.chatmore.ui.user.activity.ShopActivity;
import com.dyyj.idd.chatmore.ui.user.photo.ImageLoader;
import com.dyyj.idd.chatmore.utils.AnimationUtils;
import com.dyyj.idd.chatmore.utils.EventTrackingUtils;
import com.dyyj.idd.chatmore.weiget.AutoFlowLayout;
import com.dyyj.idd.chatmore.weiget.FlowAdapter;
import com.dyyj.idd.chatmore.weiget.MyFrameAnimation;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 个人页卡
 */
@SuppressLint("ValidFragment")
public class PersonDialog extends CommonDialog implements View.OnClickListener,MyFrameAnimation.OnFrameAnimationListener {

    private String mUserId="";//个人id
    private TextView tv_name;
    private ImageView iv_sex;
    private TextView tv_age;
    private TextView tv_profession;
    private AutoFlowLayout fl_person;
    private ImageView iv_head;
    private ImageView iv_moshi;
    private String mTopicId;
    private String mUserName;
    private String mAvatar;
    private Button btn_chat;

    public PersonDialog() {
    }

    public PersonDialog(String userId) {
        this.mUserId = userId;
    }

    public PersonDialog setTopicID(String topicId) {
        this.mTopicId = topicId;
        return this;
    }

    @Override
    public int intLayoutId() {
        return R.layout.dialog_person;
    }

    @Override
    public void convertView(ViewHolder holder, CommonDialog dialog) {
        ImageView iv_close = holder.getView(R.id.iv_person_close);
        iv_head = holder.getView(R.id.iv_person_head);
        tv_name = holder.getView(R.id.tv_person_name);
        iv_sex = holder.getView(R.id.iv_person_sex);
        tv_age = holder.getView(R.id.tv_person_age);
        //职业
        tv_profession = holder.getView(R.id.tv_person_profession);
        fl_person = holder.getView(R.id.afl_person);
        btn_chat = holder.getView(R.id.btn_person_chat);//开启私聊
        Button btn_match = holder.getView(R.id.btn_person_match);
        iv_moshi = holder.getView(R.id.iv_person_moshi);
        getData();
        getSignData();
        getConsumeStoneData();
        getMatchStatus();
        iv_close.setOnClickListener(this);
        //btn_chat.setOnClickListener(this);
        btn_match.setOnClickListener(this);
        EventTrackingUtils.joinPoint(new EventBeans("ck_publicsquare_image", this.mUserId));
    }

    /**
     * 获取标签
     */
    @SuppressLint("CheckResult")
    private void getSignData() {
        ChatApp.Companion.getInstance().getDataRepository().getSigns(mUserId)
                .subscribe((Consumer<SignResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        List<SignResult.Data.Sign> tags = result.getData().getTags();
                        if (tags.size() > 0) {
                            fl_person.setAdapter(new FlowAdapter(tags) {
                                @Override
                                public View getView(int position) {
                                    View item = View.inflate(getActivity(), R.layout.item_person_tag, null);
                                    TextView tvAttrTag = item.findViewById(R.id.tv_item_tag);
                                    tvAttrTag.setText(tags.get(position).getTagName());
                                    return item;
                                }
                            });
                        }
                    }
                }, throwable -> {
                });
    }

    /**
     * 获取个人信息
     */
    @SuppressLint("CheckResult")
    private void getData() {
        ChatApp.Companion.getInstance().getDataRepository().getUserDetailInfo(mUserId)
                .subscribe((Consumer<UserDetailInfoResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        UserDetailInfoResult.DetailUserBaseInfo userInfo = result.getData().getUserBaseInfo();
                        ImageLoader.loadHead(iv_head, userInfo.getAvatar());
                        tv_name.setText(userInfo.getNickname());
                        tv_age.setText(userInfo.getAge() + "岁");
                        this.mUserName = userInfo.getNickname();
                        this.mAvatar = userInfo.getAvatar();
                        //下面这行不知为啥执行不了啊 by niushiqi
                        //tv_profession.setText(userInfo.getUserExperience().);
                        if (TextUtils.equals("2", userInfo.getGender())) {
                            iv_sex.setImageResource(R.drawable.ic_sex_femal);
                        } else {
                            iv_sex.setImageResource(R.drawable.ic_gender_main_normal);
                        }
                    }
                }, throwable -> {
                });
    }

    /**
     * 获取开始私聊消耗魔石数量
     */
    @SuppressLint("CheckResult")
    private void getConsumeStoneData() {
        ChatApp.Companion.getInstance().getDataRepository().getSquareMessageConsumeStone(mUserId)
                .subscribe((Consumer<SquareMessageConsumeStoneResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        Integer consumeStone = result.getData().getConsumeStone();
                        btn_chat.setText("开启私聊，消耗" + consumeStone.toString() + "魔石");
                    }
                }, throwable -> {
                });
    }

    /**
     * 获取是否允许广场匹配
     */
    @SuppressLint("CheckResult")
    private void getMatchStatus() {
        ChatApp.Companion.getInstance().getDataRepository().getUserMatchingStatus(mUserId)
                .subscribe((Consumer<MyMatchStatus>) result -> {
                    if (result.getErrorCode() == 200) {
                        if(result.getData().getMatchingEnable().equals("1") || result.getData().getMatchingEnable().equals("2")) {
                            //允许广场匹配
                            btn_chat.setBackgroundResource(R.drawable.bg_btn_square);
                            btn_chat.setTextColor(Color.parseColor("#884D00"));
                            btn_chat.setOnClickListener(this);
                            netCheckRelation();
                        } else {
                            //不允许广场匹配
                            btn_chat.setBackgroundResource(R.drawable.bg_btn_square_invalid);
                            btn_chat.setTextColor(Color.parseColor("#969696"));
                            btn_chat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getActivity(), "对方设置了拒绝接收广场消息哦", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }, throwable -> {
                });
    }

    /**
     * 获取是否允许广场匹配
     */
    @SuppressLint("CheckResult")
    private void netCheckRelation() {
        ChatApp.Companion.getInstance().getDataRepository().checkRelationApi(mUserId)
                .subscribe((Consumer<RelationResult>) result -> {
                    if (result.getErrorCode() == 200) {
                        if(result.getData().isFriend().equals("1")) {
                            //是好友哦
                            btn_chat.setText("开启好友私聊");
                        }
                    }
                }, throwable -> {
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_person_close:
                dismiss();
                break;
            case R.id.btn_person_chat://开始私聊
                if (mTopicId == null) {
                    Toast.makeText(getActivity(), "topic id 为 null 哦！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (this.mUserId.equals(ChatApp.Companion.getInstance().getDataRepository().getUserid())) {
                    Toast.makeText(getActivity(), "自己不能和自己聊天", Toast.LENGTH_SHORT).show();
                    return;
                }

                ChatApp.Companion.getInstance().getDataRepository().startSquareMessage(ChatApp.Companion.getInstance().getDataRepository().getUserid(), mUserId)
                        .subscribe((Consumer<StartSquareMessageResult>) result -> {
                            if (result.getErrorCode() == 200) {
                                //不是好友,消耗10魔石开始聊天
                                if(result.getData().getEndTimestamp().equals("")) {
                                    //只有首次进入才消耗魔石
                                    ChatActivity.Companion.startBySquare(getActivity(), this.mUserId, this.mUserName,
                                            this.mAvatar, new ArrayList<String>(Arrays.asList(this.mTopicId)));
                                } else {
                                    AnimationUtils.INSTANCE.start(getActivity(), iv_moshi, "xiahaomoshi10", 1, 40, 30, this);
                                }
                            } else if (result.getErrorCode() == 3017) {
                                //已经是好友
                                ChatActivity.Companion.start(getActivity(), this.mUserId,
                                        this.mUserName, this.mAvatar, null);
                            } else if (result.getErrorCode() == 3026) {
                                //不允许广场匹配
                                Toast.makeText(getActivity(), "对方设置了拒绝接收广场消息，开启聊天偶遇ta", Toast.LENGTH_SHORT).show();
                            } else if (result.getErrorCode() == 3005) {
                                //魔石不足弹窗
                                NiceDialog.init()
                                        .setLayoutId(R.layout.dialog_magic_stone_not_enough)
                                        .setConvertListener(new ViewConvertListener() {
                                            @Override
                                            public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                                                holder.setOnClickListener(R.id.store_btn, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        EventTrackingUtils.joinPoint(new EventBeans("ck_stonelack_toshop", ""));
                                                        dialog.dismiss();
                                                        ShopActivity.Companion.start(getActivity(),0);
                                                    }
                                                });
                                                holder.setOnClickListener(R.id.gift_btn, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        EventTrackingUtils.joinPoint(new EventBeans("ck_stonelack_totask", ""));
                                                        dialog.dismiss();
                                                        MainActivity.Companion.startOpenCall(getActivity());
                                                    }
                                                });
                                                holder.setOnClickListener(R.id.close_iv, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        EventTrackingUtils.joinPoint(new EventBeans("ck_stonelack_close", ""));
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }
                                        })
                                        .setDimAmount(0.8f)
                                        .setShowBottom(false)
                                        .setWidth(260)
                                        .setHeight(300)
                                        .setOutCancel(false)
                                        .show(getActivity().getSupportFragmentManager());
                            }
                        }, throwable -> {
                        });
                EventTrackingUtils.joinPoint(new EventBeans("ck_publicsquare_privatechat1", this.mUserId));
                break;
            case R.id.btn_person_match://开始匹配
                if (this.mUserId.equals(ChatApp.Companion.getInstance().getDataRepository().getUserid())) {
                    Toast.makeText(getActivity(), "自己不能和自己偶遇", Toast.LENGTH_SHORT).show();
                    return;
                }
                MainActivity.Companion.startOutgoing(getActivity());
                EventTrackingUtils.joinPoint(new EventBeans("ck_publicsquare_startmatch", this.mUserId));
                break;
        }
    }

    public void onStartFrameAnimation() {
        iv_moshi.setVisibility(View.VISIBLE);
    }

    public void onEnd() {
        iv_moshi.setVisibility(View.GONE);
        ChatActivity.Companion.startBySquare(getActivity(), this.mUserId, this.mUserName,
                this.mAvatar, new ArrayList<String>(Arrays.asList(this.mTopicId)));
    }

}

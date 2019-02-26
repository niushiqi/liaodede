package com.dyyj.idd.chatmore.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.dyyj.idd.chatmore.model.network.result.StartSquareMessageResult;
import com.dyyj.idd.chatmore.model.network.result.UserDetailInfoResult;
import com.dyyj.idd.chatmore.ui.event.TaskStartChatEvent;
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity;
import com.dyyj.idd.chatmore.ui.task.activity.ChatActivity;
import com.dyyj.idd.chatmore.ui.user.activity.ShopActivity;
import com.dyyj.idd.chatmore.ui.user.photo.ImageLoader;
import com.dyyj.idd.chatmore.utils.ActManagerUtils;
import com.dyyj.idd.chatmore.utils.EventTrackingUtils;
import com.dyyj.idd.chatmore.weiget.AutoFlowLayout;
import com.dyyj.idd.chatmore.weiget.FlowAdapter;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 个人页卡-用于好友圈展示
 * by niushiqi
 */
@SuppressLint("ValidFragment")
public class PersonDialogForCircle extends CommonDialog implements View.OnClickListener {

    private String mUserId="";//个人id
    private TextView tv_name;
    private ImageView iv_sex;
    private TextView tv_age;
    private TextView tv_profession;
    private AutoFlowLayout fl_person;
    private ImageView iv_head;
    private String mUserName;
    private String mAvatar;

    public PersonDialogForCircle() {
    }

    public PersonDialogForCircle(String userId) {
        this.mUserId = userId;
    }

    @Override
    public int intLayoutId() {
        return R.layout.dialog_person_for_circle;
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
        Button btn_chat = holder.getView(R.id.btn_person_chat);
        getData();
        getSignData();
        iv_close.setOnClickListener(this);
        btn_chat.setOnClickListener(this);
        //EventTrackingUtils.joinPoint(new EventBeans("ck_publicsquare_image", this.mUserId));
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
                        tv_profession.setText(userInfo.getUserExperience());
                        if (TextUtils.equals("2", userInfo.getGender())) {
                            iv_sex.setImageResource(R.drawable.ic_sex_femal);
                        } else {
                            iv_sex.setImageResource(R.drawable.ic_gender_main_normal);
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
                if (this.mUserId.equals(ChatApp.Companion.getInstance().getDataRepository().getUserid())) {
                    Toast.makeText(getActivity(), "自己不能和自己聊天", Toast.LENGTH_SHORT).show();
                    return;
                }

                ChatActivity.Companion.start(getActivity(), this.mUserId, this.mUserName, this.mAvatar, null);

                //EventTrackingUtils.joinPoint(new EventBeans("ck_publicsquare_privatechat1", this.mUserId));
                break;
        }
    }
}

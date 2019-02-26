package com.dyyj.idd.chatmore.weiget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.eventtracking.EventBeans;
import com.dyyj.idd.chatmore.eventtracking.EventConstant;
import com.dyyj.idd.chatmore.eventtracking.EventMessage;
import com.dyyj.idd.chatmore.utils.EventTrackingUtils;

import me.jessyan.autosize.utils.AutoSizeUtils;
import org.greenrobot.eventbus.EventBus;


public class DontLikeDialog extends Dialog {
    private Context context;
    public DontLikeDialog(Context context, String userId) {
        super(context, R.style.MyDialog);
        this.context = context;
        View contentView = getLayoutInflater().inflate(R.layout.dialog_dont_like, null);
        contentView.findViewById(R.id.close_iv).setOnClickListener(view ->{
            dismiss();
            EventTrackingUtils.joinPoint(new EventBeans("ck_textmessage_matchevaluate_badclose", userId));
        });
        contentView.findViewById(R.id.btn_no_voice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventMessage(EventConstant.TAG.TAG_CHAT_ACTIVITY,EventConstant.WHAT.WHAT_REPORT,"没声音不说话"));
                dismiss();
                EventTrackingUtils.joinPoint(new EventBeans("ck_textmessage_matchevaluate_badreason", "1"));
            }
        });
        contentView.findViewById(R.id.btn_sex).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventMessage(EventConstant.TAG.TAG_CHAT_ACTIVITY,EventConstant.WHAT.WHAT_REPORT,"色情/威胁/辱骂"));
                dismiss();
                EventTrackingUtils.joinPoint(new EventBeans("ck_textmessage_matchevaluate_badreason", "2"));
            }
        });
        contentView.findViewById(R.id.btn_ad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventMessage(EventConstant.TAG.TAG_CHAT_ACTIVITY,EventConstant.WHAT.WHAT_REPORT,"发广告"));
                dismiss();
                EventTrackingUtils.joinPoint(new EventBeans("ck_textmessage_matchevaluate_badreason", "3"));
            }
        });
        contentView.findViewById(R.id.btn_no_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventMessage(EventConstant.TAG.TAG_CHAT_ACTIVITY,EventConstant.WHAT.WHAT_REPORT,"说不清"));
                dismiss();
                EventTrackingUtils.joinPoint(new EventBeans("ck_textmessage_matchevaluate_badreason","4"));
            }
        });
        super.setContentView(contentView);
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        getWindow().setAttributes(p);
        p.height = AutoSizeUtils.dp2px(context,270);
        p.width = AutoSizeUtils.dp2px(context,260);
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);
    }
}

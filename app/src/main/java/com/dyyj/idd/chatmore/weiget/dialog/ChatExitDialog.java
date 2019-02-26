package com.dyyj.idd.chatmore.weiget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.eventtracking.EventBeans;
import com.dyyj.idd.chatmore.eventtracking.EventConstant;
import com.dyyj.idd.chatmore.eventtracking.EventMessage;
import com.dyyj.idd.chatmore.utils.EventTrackingUtils;

import org.greenrobot.eventbus.EventBus;

import me.jessyan.autosize.utils.AutoSizeUtils;


public class ChatExitDialog extends Dialog {
    private Context context;
    public ChatExitDialog(Context context, String userId) {
        super(context, R.style.MyDialog);
        this.context = context;
        View contentView = getLayoutInflater().inflate(R.layout.dialog_chat_exit, null);
        contentView.findViewById(R.id.close_iv).setOnClickListener(view ->{
            dismiss();
            EventTrackingUtils.joinPoint(new EventBeans("ck_textmessage_out_close", userId));
        });
        contentView.findViewById(R.id.btn_stay).setOnClickListener(view ->{
            dismiss();
            EventTrackingUtils.joinPoint(new EventBeans("ck_textmessage_out_retalk", userId));
        });
        contentView.findViewById(R.id.btn_exit).setOnClickListener(view -> {
            EventBus.getDefault().post(new EventMessage(EventConstant.TAG.TAG_CHAT_ACTIVITY, EventConstant.WHAT.WHAT_CHAT_EXIT));
            EventTrackingUtils.joinPoint(new EventBeans("ck_textmessage_out_quit", userId));
        });

        super.setContentView(contentView);
        EventTrackingUtils.joinPoint(new EventBeans("ck_textmessage_out",""));
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        getWindow().setAttributes(p);
        p.height = AutoSizeUtils.dp2px(context,190);
        p.width = AutoSizeUtils.dp2px(context,260);
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);
    }
}

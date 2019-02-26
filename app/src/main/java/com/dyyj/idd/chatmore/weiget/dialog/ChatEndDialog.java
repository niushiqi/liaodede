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


public class ChatEndDialog extends Dialog {
    private Context context;
    public ChatEndDialog(Context context, String userId) {
        super(context, R.style.MyDialog);
        this.context = context;
        View contentView = getLayoutInflater().inflate(R.layout.dialog_chat_end, null);
        contentView.findViewById(R.id.close_iv).setOnClickListener(view ->{
            dismiss();
            EventTrackingUtils.joinPoint(new EventBeans("ck_textmessage_matchevaluate_close", userId));
        });
        contentView.findViewById(R.id.iv_dont_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventMessage(EventConstant.TAG.TAG_CHAT_ACTIVITY,EventConstant.WHAT.WHAT_REPORT_LIKE_OR_NO,"2"));
                EventTrackingUtils.joinPoint(new EventBeans("ck_textmessage_matchevaluate_bad", userId));
            }
        });
        contentView.findViewById(R.id.iv_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventMessage(EventConstant.TAG.TAG_CHAT_ACTIVITY,EventConstant.WHAT.WHAT_REPORT_LIKE_OR_NO,"1"));
                EventTrackingUtils.joinPoint(new EventBeans("ck_textmessage_matchevaluate_good", userId));
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

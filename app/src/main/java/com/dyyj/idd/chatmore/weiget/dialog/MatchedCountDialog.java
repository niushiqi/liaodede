package com.dyyj.idd.chatmore.weiget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import com.dyyj.idd.chatmore.R;
import me.jessyan.autosize.utils.AutoSizeUtils;


public class MatchedCountDialog extends Dialog {
    private Context context;
    public MatchedCountDialog(Context context,int count) {
        super(context, R.style.MyDialog);
        this.context = context;
        View contentView = getLayoutInflater().inflate(R.layout.dialog_matched_count, null);
        contentView.findViewById(R.id.close_iv).setOnClickListener(view -> dismiss());
        ((TextView)contentView.findViewById(R.id.tv_count)).setText(count+"");
        contentView.findViewById(R.id.btn_exit).setOnClickListener(view -> {
            dismiss();
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
        p.height = AutoSizeUtils.dp2px(context,190);
        p.width = AutoSizeUtils.dp2px(context,260);
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);
    }
}

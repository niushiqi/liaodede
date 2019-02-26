package com.dyyj.idd.chatmore.weiget.pop;

import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.dyyj.idd.chatmore.R;

public class DynamicsCommentPop extends PopupWindow {

    public EditText etInput;

    public interface ICommentListener {
        void onSend(String msg);
    }

    private ICommentListener listener;

    public void setListener(ICommentListener listener) {
        this.listener = listener;
    }

    public ICommentListener getListener() {
        return listener;
    }

    public DynamicsCommentPop(Context context) {
        this(context, null);
    }

    public DynamicsCommentPop(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.pop_comment_input, null);
        etInput = view.findViewById(R.id.et_input);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(view);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
//        etInput.requestFocus();
        setFocusable(true);

        view.findViewById(R.id.txt_send).setOnClickListener(v -> {
            if (getListener() != null) {
                getListener().onSend(etInput.getText().toString().trim());
            }
            dismiss();
        });
        view.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }, 100);
    }
}

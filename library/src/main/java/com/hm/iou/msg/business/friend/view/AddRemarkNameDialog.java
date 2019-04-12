package com.hm.iou.msg.business.friend.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.hm.iou.msg.R;
import com.hm.iou.tools.ToastUtil;

/**
 * Created by hjy on 2018/6/5.
 */
public class AddRemarkNameDialog extends Dialog implements DialogInterface.OnShowListener {

    public interface OnModifyNameListener {
        void onClickSend(String content);
    }

    private EditText mEtContent;
    private OnModifyNameListener mListener;

    public AddRemarkNameDialog(@NonNull final Context context) {
        super(context, R.style.UikitAlertDialogStyle_FromBottom);
        setContentView(R.layout.msgcenter_dialog_modify_name);

        final Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.x = 0;
            lp.y = 0;
            lp.width = getContext().getResources().getDisplayMetrics().widthPixels;
            dialogWindow.setAttributes(lp);
        }

        mEtContent = findViewById(R.id.et_dialog_name);
        findViewById(R.id.btn_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mEtContent.getText().toString();
                if (TextUtils.isEmpty(content) || content.length() <= 1) {
                    ToastUtil.showMessage(context, "请输入正确的名字");
                    return;
                }
                dismiss();
                if (mListener != null) {
                    mListener.onClickSend(content);
                }
            }
        });

        setOnShowListener(this);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        mEtContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) mEtContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.showSoftInput(mEtContent, 0);
                }
            }
        }, 100);
    }

    public String getContent() {
        if (mEtContent != null) {
            return mEtContent.getText().toString();
        }
        return null;
    }

    public void updateContent(String content) {
        if (mEtContent != null) {
            mEtContent.setText(content);
            mEtContent.setSelection(mEtContent.getText().length());
        }
    }

    public void setOnModifyNameListener(OnModifyNameListener listener) {
        mListener = listener;
    }

}

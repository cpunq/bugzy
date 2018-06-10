package in.bugzy.ui.common;

import in.bugzy.R;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BugzyAlertDialog extends AlertDialog {
    private View.OnClickListener mOnPositiveButtonClickListener;
    private View.OnClickListener mOnNegativeButtonClickListener;
    private String mMessage;
    private String mTitle;

    private String mPostiveButtonText;
    private String mNegativeButtonText;

    @BindView(R.id.tv_title)
    TextView mTitleView;

    @BindView(R.id.tv_message)
    TextView mMessageView;

    @BindView(R.id.btn_positive)
    Button mPositiveButton;

    @BindView(R.id.btn_negative)
    Button mNegativeButton;


    BugzyAlertDialog(@NonNull Context context) {
        super(context);
    }

    public BugzyAlertDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    BugzyAlertDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alert);
        ButterKnife.bind(this);
        mMessageView.setText(mMessage);
        mTitleView.setText(mTitle);
        mPositiveButton.setText(mPostiveButtonText);
        mNegativeButton.setText(mNegativeButtonText);
    }

    @Override
    public void setMessage(CharSequence message) {
        super.setMessage(message);
        mMessage = message.toString();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mTitle = title.toString();
    }


    public void setPositiveButtonText(String text) {
        mPostiveButtonText = text;
    }


    public void setNegativeButtonText(String text) {
        mNegativeButtonText = text;
    }

    @OnClick(R.id.btn_positive)
    public void onPositiveButtonClick(View view) {
        if (mPositiveButton != null) {
            mOnPositiveButtonClickListener.onClick(view);
        }
    }

    @OnClick(R.id.btn_negative)
    public void setNegativeButton(View view) {
        if (mNegativeButton != null) {
            mOnNegativeButtonClickListener.onClick(view);
        }
    }

    public void setOnPositiveButtonClickListener(View.OnClickListener onPositiveButtonClickListener) {
        mOnPositiveButtonClickListener = onPositiveButtonClickListener;
    }

    public void setOnNegativeButtonClickListener(View.OnClickListener onNegativeButtonClickListener) {
        mOnNegativeButtonClickListener = onNegativeButtonClickListener;
    }
}

package com.bluestacks.bugzy.ui.common;

import com.bluestacks.bugzy.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ErrorView extends RelativeLayout {

    @BindView(R.id.error_text)
    TextView mErrorView;

    public ErrorView(Context context) {
        super(context);
        init(context);
    }

    public ErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        inflate(getContext(), R.layout.view_error, this);
        ButterKnife.bind(this);
    }

    public void setErrorText(String text) {
        mErrorView.setText(text);
    }
}

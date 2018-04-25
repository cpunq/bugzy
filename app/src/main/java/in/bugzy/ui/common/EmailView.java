package in.bugzy.ui.common;

import in.bugzy.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmailView extends RelativeLayout {

    @BindView(R.id.tv_from)
    public TextView mFromView;

    @BindView(R.id.tv_date)
    public TextView mDateView;

    @BindView(R.id.tv_to)
    public TextView mToView;

    @BindView(R.id.tv_subject)
    public TextView mSubject;

    @BindView(R.id.tv_cc)
    public TextView mCcView;

    @BindView(R.id.tv_body)
    public TextView mBodyContent;

    public EmailView(Context context) {
        super(context);
        init(context);
    }

    public EmailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(getContext(), R.layout.view_email, this);
        ButterKnife.bind(this);
    }
}

package com.bluestacks.bugzy.ui.common;

import com.bluestacks.bugzy.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ErrorView extends RelativeLayout {

    @BindView(R.id.text_message)
    TextView mMessageView;

    @BindView(R.id.img_error)
    ImageView mErrorImageView;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

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

    private void init(Context context) {
        inflate(getContext(), R.layout.view_error, this);
        ButterKnife.bind(this);
        // Hide this view by default
        this.hide();
    }

    private void setErrorImage(Drawable d) {
        mErrorImageView.setImageDrawable(d);
    }

    /**
     * Shows the progressbar along with the given message
     * @param message
     */
    public void showProgress(String message) {
        // Ensure that the view is visible itself
        this.show();
        this.setMessage(message);
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorImageView.setVisibility(View.GONE);
    }

    /**
     * Shows the error with the given image and given errorMessage
     * @param errorImage
     * @param errorMessage
     */
    public void showError(Drawable errorImage, String errorMessage) {
        // Set the error image and message
        this.setErrorImage(errorImage);
        this.showError(errorMessage);
    }

    public void showError(String errorMessage) {
        // Ensure that the view is visible itself
        this.show();

        this.setMessage(errorMessage);

        // Change the visibility
        mProgressBar.setVisibility(View.GONE);
        mErrorImageView.setVisibility(View.VISIBLE);
    }

    /**
     * Updates the current message
     * This functions assumes that the view is already visible, hence we won't call show()
     * @param message
     */
    public void setMessage(String message) {
        mMessageView.setText(message);
    }

    /**
     * - Hides all its contents
     */
    public void hide() {
        this.setVisibility(View.GONE);
    }

    public void show() {
        this.setVisibility(View.VISIBLE);
    }

    // TODO : Rename this to setMessageText
    public void setErrorText(String text) {
        mMessageView.setText(text);
    }
}

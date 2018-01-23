package com.bluestacks.bugzy.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluestacks.bugzy.HomeActivity;
import com.bluestacks.bugzy.R;
import com.jsibbold.zoomage.ZoomageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullScreenImageFragment extends Fragment implements Injectable{

    private static FullScreenImageFragment mFragment;

    @BindView(R.id.full_image)
    protected ZoomageView mFullImage;

    private HomeActivity mParentActivity;

    private Bitmap mImagePath;

    public static FullScreenImageFragment getInstance() {
        if(mFragment == null) {
            mFragment = new FullScreenImageFragment();
            return mFragment;
        }
        else {
            return mFragment;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mParentActivity = (HomeActivity)getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        mImagePath = extras.getParcelable("img_src");
        //Log.d(Const.TAG,"Image Path is : " + mImagePath);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.imageview_fullscreen, null);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParentActivity.hideFab();
        mFullImage.setImageBitmap(mImagePath);
        mParentActivity.hideActionIcons();
    }

}

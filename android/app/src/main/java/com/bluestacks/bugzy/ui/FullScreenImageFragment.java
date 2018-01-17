package com.bluestacks.bugzy.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.ImageView;

import com.bluestacks.bugzy.HomeActivity;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.common.Const;
import com.bluestacks.bugzy.models.resp.Case;
import com.bluestacks.bugzy.net.FogbugzApiFactory;
import com.bluestacks.bugzy.net.FogbugzApiService;
import com.bluestacks.bugzy.utils.PrefHelper_;
import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import io.realm.Realm;

/**
 * Created by msharma on 09/08/17.
 */
@EFragment(R.layout.imageview_fullscreen)
public class FullScreenImageFragment extends Fragment{

    private static FullScreenImageFragment mFragment;

    @ViewById(R.id.full_image)
    protected ZoomageView mFullImage;

    private HomeActivity mParentActivity;

    private Bitmap mImagePath;

    public static FullScreenImageFragment getInstance() {
        if(mFragment == null) {
            mFragment = new FullScreenImageFragment_();
            return mFragment;
        }
        else {
            return mFragment;
        }
    }

    @Pref
    PrefHelper_ mPrefs;





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        mImagePath = extras.getParcelable("img_src");
        mParentActivity = (HomeActivity)getActivity();
        //Log.d(Const.TAG,"Image Path is : " + mImagePath);
    }

    @AfterViews
    protected void onViewsReady() {
        mParentActivity.hideFab();
       mFullImage.setImageBitmap(mImagePath);
       mParentActivity.hideActionIcons();
    }

}

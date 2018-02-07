package com.bluestacks.bugzy.ui;

import com.bluestacks.bugzy.models.resp.Case;

import android.support.v4.app.Fragment;

public interface NavigationActivityBehavior {
    public void setContentFragment(Fragment fragment, boolean addToBackStack, String tag);

    public void onContentFragmentsActivityCreated(Fragment fragment, String title, String tag);

    public void setTitle(String title);

    public void openCaseDetailsActivity(Case cas);
}

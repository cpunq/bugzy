package com.bluestacks.bugzy.ui;

import android.support.v4.app.Fragment;

public interface NavigationActivityBehavior {
    public void setContentFragment(Fragment fragment, boolean addToBackStack);

    public void onContentFragmentsActivityCreated(Fragment fragment, String title);

    public void setTitle(String title);
}

package com.bluestacks.bugzy.ui.about;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluestacks.bugzy.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class AboutActivityFragment extends Fragment {

    public AboutActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }
}

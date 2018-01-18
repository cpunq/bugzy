package com.bluestacks.bugzy.adapters;

import android.content.Context;

import com.bluestacks.bugzy.models.db.Case;

import io.realm.RealmResults;

/**
 * Created by msharma on 12/07/17.
 */
public class RealmCasesAdapter extends RealmModelAdapter<Case> {

    public RealmCasesAdapter(Context context, RealmResults<Case> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
}

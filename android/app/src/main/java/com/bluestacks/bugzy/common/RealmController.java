package com.bluestacks.bugzy.common;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.bluestacks.bugzy.models.db.Case;
import com.bluestacks.bugzy.models.db.CaseEvent;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by msharma on 12/07/17.
 */
public class RealmController {
    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }

    //clear all objects from Book.class
    public void clearAll() {

        realm.beginTransaction();
        realm.clear(Case.class);
        realm.clear(CaseEvent.class);
        realm.commitTransaction();
    }

    //find all objects in the Case.class
    public RealmResults<Case> getCases() {

        return realm.where(Case.class).findAll();
    }

    public RealmResults<CaseEvent> getCaseEvents() {
        return realm.where(CaseEvent.class).findAll();
    }

    public CaseEvent getCaseEventsForBug(String bugId) {
        return realm.where(CaseEvent.class).equalTo("bugId",bugId).findFirst();
    }

    public boolean hasCaseEvents() {
        return realm.allObjects(CaseEvent.class).isEmpty();
    }

    //query a single item with the given id
    public Case getCase(String id) {

        return realm.where(Case.class).equalTo("bugId", id).findFirst();
    }

    //check if Case.class is empty
    public boolean hasCases() {

        return !realm.allObjects(Case.class).isEmpty();
    }

}

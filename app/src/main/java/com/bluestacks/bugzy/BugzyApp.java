package com.bluestacks.bugzy;

import android.app.Application;
//import com.bluestacks.bugzy.utils.Utils;
import com.bluestacks.bugzy.models.resp.Person;
import com.bluestacks.bugzy.utils.PrefHelper_;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by msharma on 19/06/17.
 */


    @EApplication
    public class BugzyApp extends Application {


        @Pref
        PrefHelper_ mPrefs;

        public List<Person> persons;

        @Override
        public void onCreate() {
            super.onCreate();
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                    .name(Realm.DEFAULT_REALM_NAME)
                    .schemaVersion(0)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            Realm.setDefaultConfiguration(realmConfiguration);
}

}

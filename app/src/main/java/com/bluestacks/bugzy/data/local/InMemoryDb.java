package com.bluestacks.bugzy.data.local;


import com.bluestacks.bugzy.models.resp.Case;
import com.bluestacks.bugzy.models.resp.Person;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryDb implements DatabaseHelper {

    HashMap<String, List<Case>> mCasesMap = new HashMap<>();
    List<Person> mPersonList;

    public InMemoryDb() {
        mCasesMap = new HashMap<>();
        mPersonList = new ArrayList<>();
    }

    @Override
    public void setCases(@NonNull List<Case> filterList, String sFilter) {
        mCasesMap.put(sFilter, filterList);
    }

    @Override
    public List<Case> getCases(String sFilter) {
        if (mCasesMap.containsKey(sFilter)) {
            return mCasesMap.get(sFilter);
        } else {
            // Return an empty array list
            return new ArrayList<>();
        }
    }

    @Override
    public void setPeople(@NonNull List<Person> people) {
        mPersonList.clear();
        mPersonList.addAll(people);
    }

    @Override
    public List<Person> getPeople() {
        return mPersonList;
    }
}

package com.bluestacks.bugzy.data.local;


import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.data.model.Person;


import java.util.List;

public interface DatabaseHelper {
    void setCases(List<Case> filterList, String sFilter);
    List<Case> getCases(String sFilter);

    void setPeople(List<Person> people);
    List<Person> getPeople();

}

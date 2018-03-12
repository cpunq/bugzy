package com.bluestacks.bugzy.models.resp;

import com.google.gson.annotations.SerializedName;

import com.bluestacks.bugzy.data.model.Person;

import java.util.List;

public class ListPeopleData {

    @SerializedName("people")
    private List<Person> persons;


    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}

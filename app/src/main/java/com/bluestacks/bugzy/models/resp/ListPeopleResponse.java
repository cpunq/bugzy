package com.bluestacks.bugzy.models.resp;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by msharma on 20/06/17.
 */
@Root(name = "response")
public class ListPeopleResponse {

    @Element(name= "people")
    private Persons persons;

    public List<Person> getPersons() {
        return persons.getPersons();
    }

    public void setPersons(Persons persons) {
        this.persons = persons;
    }
}

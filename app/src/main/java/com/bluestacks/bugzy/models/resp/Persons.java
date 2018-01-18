package com.bluestacks.bugzy.models.resp;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by msharma on 20/06/17.
 */
@Root(name = "people")
public class Persons {

        @ElementList(inline = true)
        private List<Person> persons;

        public List<Person> getPersons() {
            return persons;
        }

        public void setPersons(List<Person> persons) {
            this.persons = persons;
        }

}

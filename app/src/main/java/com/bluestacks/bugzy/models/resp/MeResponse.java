package com.bluestacks.bugzy.models.resp;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by msharma on 22/06/17.
 */

    @Root(name = "response")
    public class MeResponse {

        @Element(name= "person")
        private Person person;

        public Person getPerson() {
            return person;
        }

        public void setPerson(Person person) {
            this.person = person;
        }
    }


package com.bluestacks.bugzy.models.db;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by msharma on 12/07/17.
 */
public class Case extends RealmObject {

    @PrimaryKey
    private int bugId;

    private String operations;

    private String title;

    private int priority;


    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBugId() {
        return bugId;
    }

    public void setBugId(int ixBug) {
        this.bugId = ixBug;
    }

    public String getOperations() {
        return operations;
    }

    public void setOperations(String operations) {
        this.operations = operations;
    }
}

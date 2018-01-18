package com.bluestacks.bugzy.models.resp;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.Serializable;
import java.util.List;

/**
 * Created by msharma on 19/06/17.
 */
@Root
public class Case implements Serializable {



    @Attribute(name = "ixBug",required = false)
    int ixBug;

    @Attribute(name = "operations",required = false)
    String operations;

    @Element(name = "sTitle",required = false)
    String title;

    @Element(name = "ixPriority",required = false)
    int priority;

    @Element(name = "sFixFor",required = false , data = true)
    String fixFor;

    @Element(name = "sProject",required = false)
    String projectName;

    @Element(name = "sArea",required = false)
    String projectArea;

    @Element(name = "sStatus", required = false)
    String status;

    @Element(name = "sPersonAssignedTo",required = false)
    String personAssignedTo;

    @Element(name = "sPersonOpenedBy",required = false)
    String personOpenedBy;

    @Element(name = "events",required = false)
    CaseEvents caseevents;


    public String getFixFor() {
        return fixFor;
    }

    public void setFixFor(String fixFor) {
        this.fixFor = fixFor;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectArea() {
        return projectArea;
    }

    public void setProjectArea(String projectArea) {
        this.projectArea = projectArea;
    }

    public String getPersonAssignedTo() {
        return personAssignedTo;
    }

    public void setPersonAssignedTo(String personAssignedTo) {
        this.personAssignedTo = personAssignedTo;
    }

    public String getPersonOpenedBy() {
        return personOpenedBy;
    }

    public void setPersonOpenedBy(String personOpenedBy) {
        this.personOpenedBy = personOpenedBy;
    }

    public CaseEvents getCaseevents() {
        return caseevents;
    }

    public void setCaseevents(CaseEvents caseevents) {
        this.caseevents = caseevents;
    }

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

    public int getIxBug() {
        return ixBug;
    }

    public void setIxBug(int ixBug) {
        this.ixBug = ixBug;
    }

    public String getOperations()
    {
        return operations;
    }

    public void setOperations(String operations) {
        this.operations = operations;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

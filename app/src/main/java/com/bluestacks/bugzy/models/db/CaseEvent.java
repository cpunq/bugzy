package com.bluestacks.bugzy.models.db;

public class CaseEvent {

    private String bugEvent;
    private String eventType;
    private String eventVerb;
    private String person;
    private String personAssignedTo;
    private String timeStamp;
    private String changes;
    private String eventDescription;
    private String content;

    private int bugId;

    public int getBugId() {
        return bugId;
    }

    public void setBugId(int bugId) {
        this.bugId = bugId;
    }

    public String getBugEvent() {
        return bugEvent;
    }

    public void setBugEvent(String bugEvent) {
        this.bugEvent = bugEvent;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventVerb() {
        return eventVerb;
    }

    public void setEventVerb(String eventVerb) {
        this.eventVerb = eventVerb;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getPersonAssignedTo() {
        return personAssignedTo;
    }

    public void setPersonAssignedTo(String personAssignedTo) {
        this.personAssignedTo = personAssignedTo;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getChanges() {
        return changes;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

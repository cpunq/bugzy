package com.bluestacks.bugzy.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CaseEvent implements Serializable {
    @SerializedName( "ixBug")
    String bug_id;

    @SerializedName( "sFileName")
    String filename;

    @SerializedName( "ixBugEvent" )
    String bugEvent;

    @SerializedName( "evt")
    int evt;

    @SerializedName( "sVerb")
    String verb;

    @SerializedName( "ixPerson" )
    String personid;

    @SerializedName( "ixPersonAssignedTo")
    String personAssignedTo;

    @SerializedName( "dt" )
    Date date;

    @SerializedName( "sURL")
    String url;

    @SerializedName( "s")
    String content;

    @SerializedName( "fEmail")
    boolean fEmail;

    @SerializedName( "fHTML")
    boolean fHTML;

    @SerializedName( "fExternal")
    boolean fExternal;

    @SerializedName( "sChanges")
    String sChanges;

    @SerializedName( "sFormat")
    String sFormat;

    @SerializedName( "attachment")
    String attachment;

    @SerializedName( "rgAttachments")
    List<Attachment> sAttachments;

    @SerializedName( "sFrom")
    String sFrom;

    @SerializedName( "sTo")
    String sTo;

    @SerializedName( "sCC")
    String sCC;

    @SerializedName( "sBCC")
    String sBCC;

    @SerializedName( "sReplyTo")
    String sReplyTo;

    @SerializedName( "sSubject")
    String sSubject;

    @SerializedName( "sDate")
    String sDate;

    @SerializedName( "sBodyText")
    String sBodyText;

    @SerializedName( "sBodyHTML")
    String sBodyHTML;

    @SerializedName( "sMessageId")
    String sMessageId;

    @SerializedName( "evtDescription")
    String eventDescription;

    @SerializedName( "bEmail")
    String bEmail;

    @SerializedName( "bExternal")
    String bExternal;

    @SerializedName( "sPerson")
    String sPerson;

    @SerializedName( "sHtml")
    String contentHtml;

    public String getBugEvent() {
        return bugEvent;
    }

    public void setBugEvent(String bugEvent) {
        this.bugEvent = bugEvent;
    }

    public int getEvt() {
        return evt;
    }

    public void setEvt(int evt) {
        this.evt = evt;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public String getPersonAssignedTo() {
        return personAssignedTo;
    }

    public void setPersonAssignedTo(String personAssignedTo) {
        this.personAssignedTo = personAssignedTo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isfEmail() {
        return fEmail;
    }

    public void setfEmail(boolean fEmail) {
        this.fEmail = fEmail;
    }

    public boolean isfHTML() {
        return fHTML;
    }

    public void setfHTML(boolean fHTML) {
        this.fHTML = fHTML;
    }

    public boolean isfExternal() {
        return fExternal;
    }

    public void setfExternal(boolean fExternal) {
        this.fExternal = fExternal;
    }

    public String getsChanges() {
        return sChanges;
    }

    public void setsChanges(String sChanges) {
        this.sChanges = sChanges;
    }

    public String getsFormat() {
        return sFormat;
    }

    public void setsFormat(String sFormat) {
        this.sFormat = sFormat;
    }

    public List<Attachment> getsAttachments() {
        return sAttachments;
    }

    public void setsAttachments(List<Attachment> sAttachments) {
        this.sAttachments = sAttachments;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getbEmail() {
        return bEmail;
    }

    public void setbEmail(String bEmail) {
        this.bEmail = bEmail;
    }

    public String getbExternal() {
        return bExternal;
    }

    public void setbExternal(String bExternal) {
        this.bExternal = bExternal;
    }

    public String getsPerson() {
        return sPerson;
    }

    public void setsPerson(String sPerson) {
        this.sPerson = sPerson;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }

    public String getBug_id() {
        return bug_id;
    }

    public void setBug_id(String bug_id) {
        this.bug_id = bug_id;
    }

    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = date;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getsFrom() {
        return sFrom;
    }

    public void setsFrom(String sFrom) {
        this.sFrom = sFrom;
    }

    public String getsTo() {
        return sTo;
    }

    public void setsTo(String sTo) {
        this.sTo = sTo;
    }

    public String getsCC() {
        return sCC;
    }

    public void setsCC(String sCC) {
        this.sCC = sCC;
    }

    public String getsBCC() {
        return sBCC;
    }

    public void setsBCC(String sBCC) {
        this.sBCC = sBCC;
    }

    public String getsReplyTo() {
        return sReplyTo;
    }

    public void setsReplyTo(String sReplyTo) {
        this.sReplyTo = sReplyTo;
    }

    public String getsSubject() {
        return sSubject;
    }

    public void setsSubject(String sSubject) {
        this.sSubject = sSubject;
    }

    public String getsDate() {
        return sDate;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public String getsBodyText() {
        return sBodyText;
    }

    public void setsBodyText(String sBodyText) {
        this.sBodyText = sBodyText;
    }

    public String getsBodyHTML() {
        return sBodyHTML;
    }

    public void setsBodyHTML(String sBodyHTML) {
        this.sBodyHTML = sBodyHTML;
    }
}

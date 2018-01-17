package com.bluestacks.bugzy.models.resp;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by msharma on 26/07/17.
 */
@Root(name = "event")
public class CaseEvent {

    @Attribute(name = "ixBugEvent", required = false)
    String bugEventAttr;

    @Attribute(name = "ixBug", required = false)
    String bug_id;


    @Element(name = "sFileName",required = false)
    String filename;

    @Element(name = "ixBugEvent" , required = false)
    String bugEvent;

    @Element(name = "evt", required = false)
    String evt;

    @Element(name = "sVerb", required = false)
    String verb;

    @Element(name = "ixPerson" , required = false)
    String personid;

    @Element(name = "ixPersonAssignedTo", required = false)
    String personAssignedTo;

    @Element(name = "dt" , required = false)
    String date;

    @Element(name = "sURL", required = false)
    String url;

    @Element(name = "s", required = false)
    String content;

    @Element(name = "fEmail", required = false)
    boolean fEmail;

    @Element(name = "fHTML", required = false)
    boolean fHTML;

    @Element(name = "fExternal", required = false)
    boolean fExternal;

    @Element(name = "sChanges", required = false)
    String sChanges;

    @Element(name = "sFormat", required = false)
    String sFormat;

    @Element(name = "attachment", required = false)
    String attachment;

    @ElementList(name = "rgAttachments", required = false)
    List<Attachment> sAttachments;

    @Element(name = "sFrom", required = false)
    String sFrom;

    @Element(name = "sTo", required = false)
    String sTo;

    @Element(name = "sCC", required = false)
    String sCC;

    @Element(name = "sBCC", required = false)
    String sBCC;

    @Element(name = "sReplyTo", required = false)
    String sReplyTo;

    @Element(name = "sSubject", required = false)
    String sSubject;

    @Element(name = "sDate", required = false)
    String sDate;

    @Element(name = "sBodyText", required = false)
    String sBodyText;

    @Element(name = "sBodyHTML", required = false)
    String sBodyHTML;

    @Element(name = "sMessageId", required = false)
    String sMessageId;

    @Element(name = "evtDescription", required = false)
    String eventDescription;

    @Element(name = "bEmail", required = false)
    String bEmail;

    @Element(name = "bExternal", required = false)
    String bExternal;

    @Element(name = "sPerson", required = false)
    String sPerson;

    @Element(name = "sHtml", required = false)
    String contentHtml;



    public String getBugEvent() {
        return bugEvent;
    }

    public void setBugEvent(String bugEvent) {
        this.bugEvent = bugEvent;
    }

    public String getEvt() {
        return evt;
    }

    public void setEvt(String evt) {
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

    public String getBugEventAttr() {
        return bugEventAttr;
    }

    public void setBugEventAttr(String bugEventAttr) {
        this.bugEventAttr = bugEventAttr;
    }

    public String getBug_id() {
        return bug_id;
    }

    public void setBug_id(String bug_id) {
        this.bug_id = bug_id;
    }

    public String getDate() {
        return date;
    }


    public void setDate(String date) {
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
}

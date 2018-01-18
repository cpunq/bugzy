package com.bluestacks.bugzy.models.resp;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by msharma on 20/06/17.
 */
@Root
public class Person {

    @Element(name = "ixPerson",required = false)
    String personid;

    @Element(name = "sFullName",required = false)
    String fullname;

    @Element(name = "sEmail",required = false)
    String email;

    @Element(name = "sPhone",required = false)
    String phone;

    @Element(name = "fAdministrator",required = false)
    boolean administrator;

    @Element(name = "fCommunity",required = false)
    boolean community;

    @Element(name = "fVirtual",required = false)
    boolean virtual;

    @Element(name = "fDeleted",required = false)
    boolean deleted;

    @Element(name = "fNotify",required = false)
    boolean notify;

    @Element(name = "sHomepage",required = false)
    String homepage;

    @Element(name = "sLocale",required = false)
    String locale;

    @Element(name = "sLanguage",required = false)
    String language;

    @Element(name = "sTimeZoneKey",required = false)
    String timezonekey;

    @Element(name = "sLDAPUid",required = false)
    String LDAPUid;

    @Element(name = "dtLastActivity",required = false)
    String lastactivity;

    @Element(name = "fRecurseBugChildren",required = false)
    boolean recursebugchildren;

    @Element(name = "fPaletteExpanded",required = false)
    boolean paletteexpanded;

    @Element(name = "ixBugWorkingOn",required = false)
    String bugworkingon;

    @Element(name = "sFrom",required = false)
    String from;

    @Element(name = "sSnippetKey",required = false)
    String snippetkey;

    @Element(name = "nType",required = false)
    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSnippetkey() {
        return snippetkey;
    }

    public void setSnippetkey(String snippetkey) {
        this.snippetkey = snippetkey;
    }

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    public boolean isCommunity() {
        return community;
    }

    public void setCommunity(boolean community) {
        this.community = community;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimezonekey() {
        return timezonekey;
    }

    public void setTimezonekey(String timezonekey) {
        this.timezonekey = timezonekey;
    }

    public String getLDAPUid() {
        return LDAPUid;
    }

    public void setLDAPUid(String LDAPUid) {
        this.LDAPUid = LDAPUid;
    }

    public String getLastactivity() {
        return lastactivity;
    }

    public void setLastactivity(String lastactivity) {
        this.lastactivity = lastactivity;
    }

    public boolean isRecursebugchildren() {
        return recursebugchildren;
    }

    public void setRecursebugchildren(boolean recursebugchildren) {
        this.recursebugchildren = recursebugchildren;
    }

    public boolean isPaletteexpanded() {
        return paletteexpanded;
    }

    public void setPaletteexpanded(boolean paletteexpanded) {
        this.paletteexpanded = paletteexpanded;
    }

    public String getBugworkingon() {
        return bugworkingon;
    }

    public void setBugworkingon(String bugworkingon) {
        this.bugworkingon = bugworkingon;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}

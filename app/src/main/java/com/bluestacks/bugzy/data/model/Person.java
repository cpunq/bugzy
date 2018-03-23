package com.bluestacks.bugzy.data.model;

import com.google.gson.annotations.SerializedName;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Person {
    @PrimaryKey
    @SerializedName( "ixPerson")
    int personid;

    @SerializedName( "sFullName")
    String fullname;

    @SerializedName( "sEmail")
    String email;

    @SerializedName( "sPhone")
    String phone;

    @SerializedName( "fAdministrator")
    boolean administrator;

    @SerializedName( "fCommunity")
    boolean community;

    @SerializedName( "fVirtual")
    boolean virtual;

    @SerializedName( "fDeleted")
    boolean deleted;

    @SerializedName( "fNotify")
    boolean notify;

    @SerializedName( "sHomepage")
    String homepage;

    @SerializedName( "sLocale")
    String locale;

    @SerializedName( "sLanguage")
    String language;

    @SerializedName( "sTimeZoneKey")
    String timezonekey;

    @SerializedName( "sLDAPUid")
    String LDAPUid;

    @SerializedName( "dtLastActivity")
    String lastactivity;

    @SerializedName( "fRecurseBugChildren")
    boolean recursebugchildren;

    @SerializedName( "fPaletteExpanded")
    boolean paletteexpanded;

    @SerializedName( "ixBugWorkingOn")
    int bugworkingon;

    @SerializedName( "sFrom")
    String from;

    @SerializedName( "sSnippetKey")
    String snippetkey;

    @SerializedName( "nType")
    int type;

    public int getPersonid() {
        return personid;
    }

    public void setPersonid(int personid) {
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

    public int getBugworkingon() {
        return bugworkingon;
    }

    public void setBugworkingon(int bugworkingon) {
        this.bugworkingon = bugworkingon;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSnippetkey() {
        return snippetkey;
    }

    public void setSnippetkey(String snippetkey) {
        this.snippetkey = snippetkey;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return fullname;
    }
}

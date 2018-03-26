package com.bluestacks.bugzy.data.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import java.util.Date;

@Entity(primaryKeys = "id")
public class RecentSearch {
    private int id;
    private String text;
    private Date createdAt;

    @Ignore
    public RecentSearch(String text) {
        this.id = text.hashCode();
        this.createdAt = new Date();
    }

    public RecentSearch(int id, String text, Date createdAt) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

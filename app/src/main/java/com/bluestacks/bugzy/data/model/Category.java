package com.bluestacks.bugzy.data.model;


import com.google.gson.annotations.SerializedName;

import android.arch.persistence.room.Entity;

@Entity(primaryKeys = "id")
public class Category {
    @SerializedName("ixCategory")
    private int id;

    @SerializedName("sCategory")
    private String name;

    @SerializedName("iOrder")
    private int order;

    @SerializedName("ixStatusDefault")
    private int defaultStatus;

    public Category(int id, String name, int order, int defaultStatus) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.defaultStatus = defaultStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(int defaultStatus) {
        this.defaultStatus = defaultStatus;
    }
}

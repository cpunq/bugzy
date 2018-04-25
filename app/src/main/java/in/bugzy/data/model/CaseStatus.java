package in.bugzy.data.model;


import com.google.gson.annotations.SerializedName;

import android.arch.persistence.room.Entity;

@Entity(primaryKeys = "id")
public class CaseStatus {
    public static final String CLOSED = "closed";
    public static final String ACTIVE = "active";
    public static final String OPEN = "open";
    public static final String RESOLVED = "resolved";
    public static final String VERIFIED = "verified";

    @SerializedName("ixStatus")
    private int id;

    @SerializedName("ixCategory")
    private int category;

    @SerializedName("sStatus")
    private String name;

    public CaseStatus(int id, int category, String name) {
        this.id = id;
        this.category = category;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

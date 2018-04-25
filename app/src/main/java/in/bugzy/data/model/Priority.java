package in.bugzy.data.model;


import com.google.gson.annotations.SerializedName;

import android.arch.persistence.room.Entity;

@Entity(primaryKeys = "id")
public class Priority {
    @SerializedName("ixPriority")
    private int id;

    @SerializedName("fDefault")
    private boolean isDefault;

    @SerializedName("sPriority")
    private String name;

    public Priority(int id, boolean isDefault, String name) {
        this.id = id;
        this.isDefault = isDefault;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
